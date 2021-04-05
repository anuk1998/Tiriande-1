package Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import Common.IUser;
import Common.IObserver;
import Observer.LocalObserver;
import User.LocalUser;
import java.util.Scanner;
import java.lang.*;
import java.util.stream.Collectors;


public class GameManager {
    String[] avatars = {Avatars.PLAYER_1.toString(), Avatars.PLAYER_2.toString(), Avatars.PLAYER_3.toString(), Avatars.PLAYER_4.toString()};
    ArrayList<String> playerAvatars = new ArrayList<>(Arrays.asList(avatars));
    LinkedHashMap<String, Player> allPlayers = new LinkedHashMap<>();
    LinkedHashSet<ICharacter> allCharacters = new LinkedHashSet<>();
    ArrayList<Player> exitedPlayers = new ArrayList<>();
    ArrayList<Player> expelledPlayers = new ArrayList<>();
    ArrayList<Level> allLevels;
    Level currentLevel;
    ArrayList<IObserver> observers = new ArrayList<>();
    ArrayList<IUser> users = new ArrayList<>();
    int startLevel;
    boolean isNewLevel = false;
    boolean observerView = false;

    public GameManager(ArrayList<Level> allLevels, int startLevel) {
        this.allLevels = allLevels;
        this.currentLevel = allLevels.get(startLevel - 1);
        this.startLevel = startLevel;
    }

    /**
     * This method kick-starts the Snarl game.
     */
    public void runGame() {
        Scanner sc = new Scanner(System.in);
        boolean gameStillGoing = true;
        int index = 0;

        while (gameStillGoing) {
            if (isNewLevel) {
                resetForNewLevel();
                isNewLevel = false;
            }
            ICharacter character = (ICharacter) allCharacters.toArray()[index];
            boolean playerIsActive = checkPlayerActiveStatus(character);
            IUser currentUser = getUserByName(character.getName());

            // Move the adversary automatically
            if (character instanceof IAdversary) {
                gameStillGoing = adversarysMove(character, currentUser);
            }

            // Ask the player for a move and validate/execute that move
            else {
                // move this broadcast line in a later milestone -- here for now because this Milestone doesn't broadcast to adversaries
                currentUser.broadcastUpdate(this.currentLevel, character, playerIsActive);
                if (playerIsActive) {
                    if (observerView) {
                        System.out.println("\nThis is the Observer View of the Level:");
                        System.out.println(this.currentLevel.renderLevel());
                    }
                    Position requestedMove = currentUser.getUserMove(sc, character);
                    GameStatus moveStatus = callRuleChecker(character, requestedMove);
                    gameStillGoing = parseMoveStatusAndDoAction(moveStatus.name(), requestedMove, character);
                    sendUpdatesToObservers(character, requestedMove, moveStatus, this.currentLevel, exitedPlayers, expelledPlayers);
                }
            }

            // check if we're on the last character in the list and if so, loop back to the beginning
            if (index == allCharacters.size() - 1) {
                index = 0;
            } else {
                index++;
            }
        }

        System.out.println("\nGame has ended.\n");

        //rank player exited numbers
        System.out.println("Players Ranked By Number Of Times Successfully Exited in the Game:");
        printPlayerExitedRankings();

        //rank players based on keys found
        System.out.println("\nPlayers Ranked By Number Of Keys Found in the Game:");
        printPlayerKeyFoundRankings();

        sc.close();
        // add other game terminus actions once networking elements/client/scanner/etc elements are known
    }

    private boolean adversarysMove(ICharacter character, IUser currentUser) {
        Position chosenMove = chooseAdversaryMove(currentUser, (IAdversary) character);
        GameStatus moveStatus = callRuleChecker(character, chosenMove);

        int invalidCount = 0;
        // generate a new move until we get one that's not invalid
        while (moveStatus.name().equals("INVALID")) {
            invalidCount++;
            // if there are no valid moves in any cardinal direction, keep the adversary stationary
            if (invalidCount == 4) {
                moveStatus = GameStatus.VALID;
                chosenMove = character.getCharacterPosition();
                break;
            }
            chosenMove = chooseAdversaryMove(currentUser, (IAdversary) character);
            moveStatus = callRuleChecker(character, chosenMove);
        }
        return parseMoveStatusAndDoAction(moveStatus.name(), chosenMove, character);
    }


    /**
     * Places all the players on new random positions in the new level.
     * Generate new adversaries and new positions for them.
     */
    private void resetForNewLevel() {
        System.out.println("----------------------------");
        System.out.println("Starting the next level...\n\n");
        // reset data structures for new level
        this.allCharacters = new LinkedHashSet<>();
        this.exitedPlayers = new ArrayList<>();
        this.expelledPlayers = new ArrayList<>();
        this.users = new ArrayList<>();

        // add all players in the game to the new level
        for (Player player : this.allPlayers.values()) {
            Position randomPos = this.currentLevel.pickRandomPositionForCharacterInLevel();
            currentLevel.addCharacter(player, randomPos);
            allCharacters.add(player);

            IUser user = new LocalUser(player.getName());
            addUser(user);
        }

        int levelNum = this.allLevels.indexOf(this.currentLevel);

        int numOfZombies = (int) (Math.floor((levelNum + 1) / 2) + 1);
        int numOfGhosts = (int) Math.floor(levelNum / 2);

        for (int z = 1; z < numOfZombies + 1; z++) {
            registerAdversary("zombie" + z, "zombie");
        }
        for (int g = 1; g < numOfGhosts + 1; g++) {
            registerAdversary("ghost" + g, "ghost");
        }
    }

    /**
     * Chooses the given adversary's next move and then sends it to RuleChecker before being executed.
     *
     * @param currentUser current adversary's user instance
     * @param character   adversary whose move it is
     * @return a chosen position for the given adversary
     */
    public Position chooseAdversaryMove(IUser currentUser, IAdversary character) {
        LocalUser user = (LocalUser) currentUser;
        ArrayList<Position> playerPositions = user.getAllPlayerLocations(this.currentLevel);
        Position chosenPosition;
        if (character.getType().equals("zombie"))
            chosenPosition = chooseZombieMove(character, playerPositions);
        else chosenPosition = chooseGhostMove(character, playerPositions);

        return chosenPosition;
    }

    /**
     * Chooses the given Zombie's next move based on what other players are in its room. If there
     * are no players in its room, a random cardinal position is chosen. If there are multiple players
     * in the room, the zombie will move towards the closest one.
     *
     * @param character       the Zombie
     * @param playerPositions list of all the players' positions
     * @return a chosen position for the zombie to move to
     */
    public Position chooseZombieMove(IAdversary character, ArrayList<Position> playerPositions) {
        Position chosenMove = null;
        Zombie zom = (Zombie) character;
        Position zomPos = zom.getCharacterPosition();
        Room zombiesRoom = zom.getZombiesRoom();
        ArrayList<Position> playersInRoomWithZombie = new ArrayList<>();

        // determines which players are in the same room as the zombie and adds their positions to a list
        for (Position playerPos : playerPositions) {
            for (Position roomPos : zombiesRoom.getListOfAllPositionsLevelScale()) {
                if (playerPos.toString().equals(roomPos.toString())) {
                    playersInRoomWithZombie.add(playerPos);
                }
            }
        }

        // Based on how many players are in the room with the Zombie, choose which player to attack
        // and then subsequently which cardinal move is closest to that chosen player
        ArrayList<Position> cardinalPositions = this.currentLevel.getAllAdjacentTiles(zomPos);
        if (playersInRoomWithZombie.size() == 0) {
            Random rand = new Random();
            int cardinalIndex = rand.nextInt(cardinalPositions.size() - 1);
            chosenMove = cardinalPositions.get(cardinalIndex);
        } else if (playersInRoomWithZombie.size() == 1) {
            chosenMove = getClosestPositionTo(cardinalPositions, playersInRoomWithZombie.get(0));
        } else {
            Position closestPlayerPos = getClosestPositionTo(playersInRoomWithZombie, zomPos);
            chosenMove = getClosestPositionTo(cardinalPositions, closestPlayerPos);
        }

        return chosenMove;
    }

    /**
     * Chooses the given Ghost's next move based on what other players are closest to it by distance,
     * regardless of what room they are in.
     *
     * @param character       the Ghost
     * @param playerPositions list of all the players' positions
     * @return a chosen position for the ghost to move to
     */
    public Position chooseGhostMove(IAdversary character, ArrayList<Position> playerPositions) {
        Position playerPositionToAttack = getClosestPositionTo(playerPositions, character.getCharacterPosition());
        ArrayList<Position> ghostAdjacentTiles = currentLevel.getAllAdjacentTiles(character.getCharacterPosition());
        return getClosestPositionTo(ghostAdjacentTiles, playerPositionToAttack);
    }

    /**
     * Determines which position in a given list that is closest to the given source position.
     * Helper for chooseGhostMove and chooseZombieMove
     *
     * @param positionsToCompare list of positions
     * @param source             position to compare distance to
     * @return the closest position in distance
     */
    public Position getClosestPositionTo(ArrayList<Position> positionsToCompare, Position source) {
        HashMap<Position, Double> positionsAndDistances = new HashMap<>();
        int sourceRow = source.getRow();
        int sourceCol = source.getCol();

        for (Position pos : positionsToCompare) {
            double distance = Math.sqrt(Math.pow(sourceRow - pos.getRow(), 2) + Math.pow(sourceCol - pos.getCol(), 2));
            positionsAndDistances.put(pos, distance);
        }

        Position closestPos = null;
        double lowestDistance = 1000.0;
        for (Map.Entry entry : positionsAndDistances.entrySet()) {
            if ((double) entry.getValue() < lowestDistance) {
                lowestDistance = (double) entry.getValue();
                closestPos = (Position) entry.getKey();
            }
        }
        return closestPos;
    }

    /**
     * Checks if the current user is a Player who has been expelled from the game.
     *
     * @param character the user whose turn it is
     * @return Returns true if the Player has been expelled/are no longer active, false if the Player is
     * still active or if the character is an Adversary (i.e. ghost, zombie)
     */
    public boolean checkPlayerActiveStatus(ICharacter character) {
        if (character instanceof Player) {
            return !exitedPlayers.contains(character) && !expelledPlayers.contains(character);
        }
        return true;
    }

    /**
     * Calls the RuleChecker Interface depending on if the user is a Player or an Adversary.
     *
     * @param character     the character whose turn it is
     * @param requestedMove the goal Position the character wants to move to
     * @return the GameStatus depending on what kind of move it is
     */
    public GameStatus callRuleChecker(ICharacter character, Position requestedMove) {
        GameStatus moveStatus = GameStatus.DEFAULT;
        if (character instanceof Player) {
            RuleCheckerPlayer rcPlayer = new RuleCheckerPlayer(this, currentLevel, (Player) character);
            moveStatus = rcPlayer.runRuleChecker(requestedMove);
        } else if (character instanceof IAdversary) {
            IRuleChecker rcAdversary;
            if (((IAdversary) character).getType().equals("zombie")) {
                rcAdversary = new RuleCheckerZombie(this, currentLevel, (IAdversary) character);
            } else rcAdversary = new RuleCheckerGhost(this, currentLevel, (IAdversary) character);

            moveStatus = rcAdversary.runRuleChecker(requestedMove);
        }
        return moveStatus;
    }

    /**
     * Parses the given GameStatus type and applies specific actions based on which type of move it is.
     *
     * @param moveStatus:  the returned enum GameStatus that signifies what type the requested move is
     * @param destination: the requested destination from the user
     * @param c:           the current character whose move it is
     * @return a boolean indicating if the game is still in play
     */
    public boolean parseMoveStatusAndDoAction(String moveStatus, Position destination, ICharacter c) {
        switch (moveStatus) {
            case "VALID":
                currentLevel.moveCharacter(c, destination);
                return true;
            case "INVALID":
                System.out.println("Requested move to " + destination.toString() + " was invalid. You miss your turn.");
                return true;
            case "KEY_FOUND":
                currentLevel.moveCharacter(c, destination);
                currentLevel.openExitTile();
                System.out.println("Player " + c.getName() + " found the key.");
                ((Player) c).increaseNumOfKeysFound();
                return true;
            case "PLAYER_SELF_ELIMINATES":
                currentLevel.restoreCharacterTile(c);
                currentLevel.expelPlayer((Player) c);
                expelledPlayers.add((Player) c);
                return true;
            case "PLAYER_EXPELLED":
                Player p = currentLevel.playerAtGivenPosition(destination);
                currentLevel.expelPlayer(p);
                expelledPlayers.add(p);
                currentLevel.moveCharacter(c, destination);
                System.out.println("Player " + p.getName() + " was expelled.");
                return true;
            case "PLAYER_EXITED":
                currentLevel.restoreCharacterTile(c);
                currentLevel.playerPassedThroughExit(c);
                exitedPlayers.add((Player) c);
                System.out.println("Player " + c.getName() + " exited.");
                return true;
            case "GHOST_TRANSPORTS":
                currentLevel.moveCharacter(c, currentLevel.pickRandomPositionForCharacterInLevel());
                return true;
            case "LEVEL_WON":
                Player p2 = currentLevel.playerAtGivenPosition(destination);
                currentLevel.restoreCharacterTile(c);
                currentLevel.playerPassedThroughExit(c);
                if (c instanceof Player) {
                    exitedPlayers.add((Player) c);
                } else {
                    expelledPlayers.add(p2);
                }
                resurrectPlayers();
                System.out.println("Congrats!! Players have won the level!");
                this.currentLevel = this.allLevels.get(getNewLevel());
                isNewLevel = true;
                return true;
            case "GAME_WON":
                Player p3 = currentLevel.playerAtGivenPosition(destination);
                currentLevel.restoreCharacterTile(c);
                currentLevel.playerPassedThroughExit(c);
                if (c instanceof Player) {
                    exitedPlayers.add((Player) c);
                } else {
                    expelledPlayers.add(p3);
                }
                System.out.print("Congrats! Players have won the game!");

                return false;
            case "GAME_LOST":
                Player p4 = currentLevel.playerAtGivenPosition(destination);
                currentLevel.restoreCharacterTile(c);
                if (c instanceof Player) {
                    currentLevel.expelPlayer((Player) c);
                    expelledPlayers.add((Player) c);
                } else {
                    currentLevel.expelPlayer(p4);
                    expelledPlayers.add(p4);
                }
                System.out.println("Sorry :( Players have lost the game!");


                return false;
            default:
                //System.out.print("Default case.Should never get here.");
        }
        // will never get here
        return false;
    }

    public void printPlayerExitedRankings() {
        HashMap<String, Integer> playerExitedNumbers = new HashMap<>();

        for (Player p : this.allPlayers.values()) {
            playerExitedNumbers.put(p.getName(), p.getNumOfTimesExited());
        }

        HashMap<String, Integer> playerExitedNumbersSorted = playerExitedNumbers.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        System.out.println(playerExitedNumbersSorted.toString());
    }

    private void printPlayerKeyFoundRankings() {
        HashMap<String, Integer> playerKeyNumbers = new HashMap<>();

        for (Player p : this.allPlayers.values()) {
            playerKeyNumbers.put(p.getName(), p.getNumOfKeysFound());
        }

        HashMap<String, Integer> playerKeyNumbersSorted = playerKeyNumbers.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        System.out.println(playerKeyNumbersSorted.toString());
    }

    /**
     * Gets a new index for the allLevels list to generate a new level to be played.
     *
     * @return a number representing
     */
    private int getNewLevel() {
        return this.allLevels.indexOf(this.currentLevel) + 1;
    }

    /**
     * Resurrects all expelled players once the level has been won by players, so that they can all
     * move onto the next level.
     *
     * CURRENTLY NOT USED.
     */
    private void resurrectPlayers() {
        for (Player player : this.allPlayers.values()) {
            if (player.getIsExpelled()) {
                player.setIsExpelled(false);
            }
        }
    }

    /**
     * Randomly chooses the avatar for that new player from a list of avatars.
     *
     * @param newPlayer the current player we're registering
     */
    public void assignPlayerAvatar(Player newPlayer) {
        Random rand = new Random();
        int randomIndex = rand.nextInt(playerAvatars.size());
        String randomAvatar = playerAvatars.get(randomIndex);
        newPlayer.setAvatar(randomAvatar);
        playerAvatars.remove(randomAvatar);
    }

    /**
     * Registers a certain number of players based on the given number.
     *
     * @param numOfPlayers number of players to register
     * @param scanner instance to read in from STDin
     */
    public void registerPlayers(int numOfPlayers, Scanner scanner) {
        while (numOfPlayers > 0) {
            System.out.println("Please enter a username for your player:");
            String playerName = scanner.nextLine();
            registerPlayer(playerName);
            numOfPlayers--;
        }
        System.out.println("-----------------------------");
        System.out.println("All players have been registered. Let's start the game.");
        System.out.println();
    }

    /**
     * Registers a player with a given unique name and add them to the level.
     * Not called anywhere for Milestone 5 because we don't know user entry point yet.
     *
     * @param name the name of player to register
     */
    public void registerPlayer(String name) {
        if (allPlayers.containsKey(name)) {
            System.out.println("Cannot register Player with name `" + name + "`. Name already has been taken. Please pick again.");
        }
        else if (allPlayers.size() < 4) {
            IUser user = new LocalUser(name);
            addUser(user);

            Player newPlayer = new Player(name);
            assignPlayerAvatar(newPlayer);
            allPlayers.put(name, newPlayer);
            allCharacters.add(newPlayer);

            Position randomPos = currentLevel.pickRandomPositionForCharacterInLevel();
            currentLevel.addCharacter(newPlayer, randomPos);
            System.out.println("Player " + name + " has been registered at position: " + randomPos.toString() + " with avatar: " + newPlayer.getAvatar());
        }
        else {
            System.out.println("Cannot register player " + name + ". Game has reached maximum participant count. Sorry!");
            registerObservers();
        }
    }


    /**
     * Registers an adversary with a given unique name and add them to the level.
     * Not called anywhere for Milestone 5 because we don't know user entry point yet.
     *
     * @param name the name of adversary to register
     * @param type which type of adversary it is
     */
    public void registerAdversary(String name, String type) {
        IAdversary adversary = null;
        if (type.equalsIgnoreCase("zombie")) {
            adversary = new Zombie(name);
            LocalUser user = new LocalUser(name);
            addUser(user);
            /** Commented for now because we don't want players to see the entire level via STDIN */
            //user.renderLevelForAdversary(this.currentLevel);
        }
        else if (type.equalsIgnoreCase("ghost")) {
            adversary = new Ghost(name);
            LocalUser user = new LocalUser(name);
            addUser(user);
            /** Commented for now because we don't want players to see the entire level via STDIN */
            //user.renderLevelForAdversary(this.currentLevel);
        }
        this.allCharacters.add(adversary);
        Position pickedPos = currentLevel.pickRandomPositionForCharacterInLevel();
        currentLevel.addCharacter(adversary, new Position(pickedPos.getRow(), pickedPos.getCol()));
        //System.out.println("New adversary " + name + " of type " + type + " has been registered.")
    }

    private void registerObservers() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Would you like to register as an observer? Enter 'Y' for yes or 'N' for no.");
        String wantsToMakeObserver = sc.nextLine();
        while(!(wantsToMakeObserver.equalsIgnoreCase("Y") || wantsToMakeObserver.equalsIgnoreCase("N")) ) {
            System.out.println("Sorry, invalid response. Please enter 'Y' for yes or 'N' for no");
            wantsToMakeObserver = sc.nextLine();
        }

        if(wantsToMakeObserver.equalsIgnoreCase("N")) {
            System.out.println("Okay, you will not be joining as an observer.");
        }
        else if(wantsToMakeObserver.equalsIgnoreCase("Y")) {
            System.out.println("Please enter a username for your observer");
            String observerName = sc.nextLine();
            IObserver observer = new LocalObserver(observerName);
            addObserver(observer);
            System.out.println(observerName + " has been added to the game as an Observer.");
        }
    }

    /**
     * Returns the LocalUser object based on the current character's name.
     *
     * @param name the name of the character whose turn it is
     * @return the IUser object for the corresponding character
     */
    public IUser getUserByName(String name) {
        IUser currentUser = null;
        for (IUser u : this.users) {
            if (u.getUserName().equals(name)) {
                currentUser = u;
            }
        }
        return currentUser;
    }

    /**
     * Adds an observer to the list of observers
     *
     * @param observer the new observer to be added
     */
    public void addObserver(IObserver observer) {
        this.observers.add(observer);
    }

    /**
     * Adds a user to the list of users
     *
     * @param user the new user to be added
     */
    public void addUser(IUser user) {
        this.users.add(user);
    }

    /**
     * Sends a series of updates about the game state to all observers.
     *
     * @param character the current character whose move it is
     * @param requestedMove the character's requested destination
     * @param moveStatus what type of move the character requested
     * @param currentLevel the level being played
     * @param exitedPlayers a list of players who have successfully exited the game
     * @param expelledPlayers a list of players who have been expelled from the game
     */
    private void sendUpdatesToObservers(ICharacter character, Position requestedMove, GameStatus moveStatus,
                                        Level currentLevel, ArrayList<Player> exitedPlayers, ArrayList<Player> expelledPlayers) {
        for (IObserver observer : observers) {
            observer.sendUpdates(character, requestedMove, moveStatus, currentLevel, exitedPlayers, expelledPlayers);
        }
    }

    /**
     * Sets the observer view to whatever boolean is given.
     *
     * @param isObserverView boolean indicating if we want the observer view
     */
    public void setObserverView(boolean isObserverView) {
        this.observerView = isObserverView;
    }



    /**
     * Returns a Player object based on a given avatar.
     *
     * @param avatar a String avatar that represents a player on the level board
     * @return a Player object or null if no player exists for that avatar
     */
    public Player getPlayerFromAvatar(String avatar) {
        for (Player p : allPlayers.values()) {
            if (p.getAvatar().equals(avatar)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Returns the list of expelled players.
     */
    public ArrayList<Player> getExpelledPlayers(){
        return this.expelledPlayers;
    }

    /**
     * Returns the list of levels.
     */
    public ArrayList<Level> getAllLevels() {
        return this.allLevels;
    }


    /**
     * Returns the list of allPlayers
     */
    public LinkedHashMap<String, Player> getAllPlayers() {
        return this.allPlayers;
    }

    /**
     * Returns the list of exitedPlayers
     */
    public ArrayList<Player> getExitedPlayers() {
        return this.exitedPlayers;
    }


}