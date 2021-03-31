package Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Random;
import Common.IUser;
import Common.IObserver;
import Observer.LocalObserver;
import User.LocalUser;
import java.util.Scanner;


public class GameManager {
    String[] avatars = {"@", "¤", "$", "~"};
    ArrayList<String> playerAvatars = new ArrayList<>(Arrays.asList(avatars));
    LinkedHashMap<String, Player> allPlayers = new LinkedHashMap<>();
    LinkedHashSet<ICharacter> allCharacters = new LinkedHashSet<>();
    ArrayList<Player> exitedPlayers = new ArrayList<>();
    ArrayList<Player> expelledPlayers = new ArrayList<>();
    Level currentLevel;
    ArrayList<IObserver> observers = new ArrayList<>();
    ArrayList<IUser> users = new ArrayList<>();

    public GameManager(ArrayList<Level> allLevels) {
        this.currentLevel = allLevels.get(0);
    }

    /**
     * This method kick-starts the Snarl game.
     */
    public void startGame() {
        Scanner sc = new Scanner(System.in);
        registerParticipants(sc);
        boolean gameStillGoing = true;
        int index = 0;
        while (gameStillGoing) {
            ICharacter character = (ICharacter)allCharacters.toArray()[index];
            boolean playerIsActive = checkPlayerActiveStatus(character);
            IUser currentUser = getUserByName(character);
            currentUser.broadcastUpdate(this.currentLevel, character, playerIsActive);
            if (playerIsActive) {
                Position requestedMove = currentUser.getUserMove(sc, character);
                GameStatus moveStatus = callRuleChecker(character, requestedMove);
                gameStillGoing = parseMoveStatusAndDoAction(moveStatus.name(), requestedMove, character);
                sendUpdatesToObservers(character, requestedMove, moveStatus, this.currentLevel, exitedPlayers, expelledPlayers);
            }
            // check if we're on the last character in the list and if so, loop back to the beginning
            if (index == allCharacters.size() - 1) {
                index = 0;
            }
            else {
                index++;
            }
        }
        sc.close();
        System.out.println("Game has ended.");
        // add other game terminus actions once networking elements/client/scanner/etc elements are known
    }

    /**
     * This registers players and adversaries as participants of the game via STDin.
     */
    private void registerParticipants(Scanner sc) {
        System.out.println("Would you like to register as a player or adversary? Select 'P' for player or 'A' for adversary.");
        String whichTypeOfParticipant = sc.nextLine();
        //in case user does not enter 'P' or 'A'
        while (!whichTypeOfParticipant.equalsIgnoreCase("P") && !whichTypeOfParticipant.equalsIgnoreCase("A")) {
            System.out.println("Invalid response. Please enter 'P' for player or 'A' for adversary");
            whichTypeOfParticipant = sc.nextLine();
        }
        if (whichTypeOfParticipant.equalsIgnoreCase("P")) {
            System.out.println("Please enter a username for your player");
            String playerName = sc.nextLine();
            registerPlayer(playerName);

        }
        else if (whichTypeOfParticipant.equalsIgnoreCase("A")) {
            System.out.println("Please enter a username for your adversary");
            String adversaryName = sc.nextLine();
            System.out.println("Please select which type of adversary to register. Enter 'Ghost' for ghost or 'Zombie' for zombie");

            String adversaryType = sc.nextLine();
            //in case user does not enter 'Ghost' or 'Zombie'
            while(!adversaryType.equalsIgnoreCase("Ghost") && !adversaryType.equalsIgnoreCase("Zombie")) {
                System.out.println("Sorry, invalid response. Please enter 'Ghost' for ghost or 'Zombie' for zombie");
                adversaryType = sc.nextLine();
            }
            registerAdversary(adversaryName, adversaryType);
        }
        //Register more participants?
        System.out.println("Would you like to register another participant? Enter 'Y' for yes or 'N' for no");
        String wantsToRegisterMore = sc.nextLine();
        //in case user does not enter 'Y' or 'N' to register another participant
        while(!wantsToRegisterMore.equalsIgnoreCase("Y") && !wantsToRegisterMore.equalsIgnoreCase("N")) {
            System.out.println("Sorry, invalid response. Please enter 'Y' for yes or 'N' for no");
            wantsToRegisterMore = sc.nextLine();
        }

        if (wantsToRegisterMore.equalsIgnoreCase("N")) {
            System.out.println("Okay, we're ready to start the game!");
        }
        else {
            registerParticipants(sc);
        }

    }

    /**
     * Returns the LocalUser object based on the current character's name.
     *
     * @param character the character whose turn it is
     * @return the IUser object for the corresponding character
     */
    private IUser getUserByName(ICharacter character) {
        IUser currentUser = null;
        for (IUser u: this.users) {
            if (u.getUserName().equals(character.getName())){
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
     * Checks if the current user is a Player who has been expelled from the game.
     *
     * @param character the user whose turn it is
     * @return Returns true if the Player has been expelled/are no longer active, false if the Player is
     *         still active or if the character is an Adversary (i.e. ghost, zombie)
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
     * @param character the character whose turn it is
     * @param requestedMove the goal Position the character wants to move to
     * @return the GameStatus depending on what kind of move it is
     */
    public GameStatus callRuleChecker(ICharacter character, Position requestedMove) {
        GameStatus moveStatus = GameStatus.DEFAULT;
        if (character instanceof Player) {
            RuleCheckerPlayer rcPlayer = new RuleCheckerPlayer(currentLevel, (Player)character);
            moveStatus = rcPlayer.runRuleChecker(requestedMove);
        }
        else if (character instanceof IAdversary) {
            IRuleChecker rcAdversary;
            if (((IAdversary) character).getType().equals("zombie")) {
                rcAdversary = new RuleCheckerZombie(currentLevel, (IAdversary)character);
            }
            else rcAdversary = new RuleCheckerGhost(currentLevel, (IAdversary)character);

            moveStatus = rcAdversary.runRuleChecker(requestedMove);
        }
        return moveStatus;
    }

    /**
     * Parses the given GameStatus type and applies specific actions based on which type of move it is.
     *
     * @param moveStatus: the returned enum GameStatus that signifies what type the requested move is
     * @param destination: the requested destination from the user
     * @param c: the current character whose move it is
     * @return a boolean indicating if the game is still in play
     */
    public boolean parseMoveStatusAndDoAction(String moveStatus, Position destination, ICharacter c) {
        switch (moveStatus) {
            case "VALID":
                currentLevel.moveCharacter(c, destination);
                return true;
            case "INVALID":
                System.out.println("Requested move was invalid. You miss your turn.");
                return true;
            case "KEY_FOUND":
                currentLevel.moveCharacter(c, destination);
                currentLevel.openExitTile();
                return true;
            case "PLAYER_SELF_ELIMINATES":
                currentLevel.restoreCharacterTile(c);
                currentLevel.expelPlayer((Player) c);
                expelledPlayers.add((Player) c);
                return true;
            case "PLAYER_EXPELLED":
                currentLevel.expelPlayer(currentLevel.playerAtGivenPosition(destination));
                expelledPlayers.add(currentLevel.playerAtGivenPosition(destination));
                currentLevel.moveCharacter(c, destination);
                return true;
            case "PLAYER_EXITED":
                currentLevel.restoreCharacterTile(c);
                currentLevel.playerPassedThroughExit(c);
                exitedPlayers.add((Player) c);
                return true;
            case "LEVEL_WON":
                currentLevel.restoreCharacterTile(c);
                currentLevel.playerPassedThroughExit(c);
                exitedPlayers.add((Player) c);
                resurrectPlayers();
                //System.out.print("Congrats!! Players have won the level!");
                return false; // THIS CAN CHANGE TO TRUE ONCE WE'RE DEALING WITH MORE THAN ONE LEVEL
            case "GAME_WON":
                currentLevel.restoreCharacterTile(c);
                currentLevel.playerPassedThroughExit(c);
                exitedPlayers.add((Player) c);
                //System.out.print("Congrats!! Players have won the game!");
                return false;
            case "GAME_LOST":
                currentLevel.restoreCharacterTile(c);
                currentLevel.expelPlayer((Player) c);
                expelledPlayers.add((Player) c);
                //System.out.print("Sorry :( Players have lost the game! Play again?");
                return false;
            default:
                //System.out.print("Default case.Should never get here.");
        }
        // will never get here
        return false;
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
            // System.out.println("Player " + name + " has been registered at position: [" + newPlayer.getCharacterPosition().getRow() + ", " +
            //        newPlayer.getCharacterPosition().getCol() + "] with avatar: " + newPlayer.getAvatar());
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
            // IUser user = new LocalUser(name);
            //addUser(user);
        }
        else if (type.equalsIgnoreCase("ghost")) {
            adversary = new Ghost(name);
            // IUser user = new LocalUser(name);
            // addUser(user);
        }
        this.allCharacters.add(adversary);
        Position pickedPos = currentLevel.pickRandomPositionForCharacterInLevel();
        currentLevel.addCharacter(adversary, new Position(pickedPos.getRow(), pickedPos.getCol()));
        //System.out.println("New adversary " + name + " of type " + type + " has been registered.");
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
     * Returns an IUser instance based on the given name.
     *
     * @param name the name of the IUser to be found.
     * @return an IUser instance
     */
    public IUser getUserFromName(String name) {
        for (IUser user: users) {
            if (user.getUserName().equals(name)) {
                return user;
            }
        }
        return null;
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
}