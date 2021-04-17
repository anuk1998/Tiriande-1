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
import User.LocalUser;
import User.RemoteUser;
import Remote.ClientThread;

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
    String playerWhoFoundKey = "";

    public GameManager(ArrayList<Level> allLevels, int startLevel) {
        this.allLevels = allLevels;
        this.currentLevel = allLevels.get(startLevel - 1);
        this.startLevel = startLevel;
    }

    /**
     * This method kick-starts the Snarl game.
     */
    public void runGame() {
        boolean gameStillGoing = true;
        int index = 0;

        while (gameStillGoing) {
            if (isNewLevel) {
                resetForNewLevel();
                isNewLevel = false;
                sendUpdateToUsers(UpdateType.START_LEVEL, "", null);
                sendUpdateToUsers(UpdateType.START_ROUND, "", null);
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
                //currentUser.broadcastUpdate(this.currentLevel, character, playerIsActive);
                gameStillGoing = playersMove(character, currentUser, playerIsActive);
            }
            // check if we're on the last character in the list and if so, loop back to the beginning
            if (index == allCharacters.size() - 1) {
                index = 0;
            } else {
                index++;
            }
        }
        sendUpdateToUsers(UpdateType.END_GAME, "", null);
    }

    /**
     *
     *
     * --------------------------PLAYING A LEVEL METHODS-----------------------------
     *
     *
     */

    /**
     * Selects the given adversary's next move based on player locations.
     */
    private boolean adversarysMove(ICharacter character, IUser currentUser) {
        Position chosenMove = null;
        AdversaryMovement am = new AdversaryMovement(this.currentLevel);
        if (currentUser instanceof LocalUser) {
            chosenMove = am.chooseAdversaryMove(currentUser, (IAdversary) character);
        }
        else {
            currentUser.renderObserverView(this.currentLevel);
            chosenMove = currentUser.getUserMove(character);
        }

        GameStatus moveStatus = callRuleChecker(character, chosenMove);
        System.out.println("DEBUG: moveStatus from adversary's chosen move is: " + moveStatus);
        int invalidCount = 0;
        // generate a new move until we get one that's not invalid
        while (moveStatus.equals(GameStatus.INVALID)) {
            invalidCount++;
            // if there are no valid moves in any cardinal direction, keep the adversary stationary
            if (invalidCount == 4) {
                moveStatus = GameStatus.VALID;
                chosenMove = character.getCharacterPosition();
                break;
            }
            else {
                chosenMove = am.chooseAdversaryMove(currentUser, (IAdversary) character);
                moveStatus = callRuleChecker(character, chosenMove);
            }
        }
        System.out.println("DEBUG: The moveStatus (after checking invalid move) is " + moveStatus);
        parseMoveStatusAndDoAction(moveStatus.name(), chosenMove, character);
        currentUser.sendMoveUpdate(moveStatus.toString(), chosenMove, character);
        return checkGameStatus(moveStatus);
    }

    /**
     * Checks if a player is active and if so, requests a move from them. If observer view is enabled,
     * the current level view is outputted to the user.
     */
    private boolean playersMove(ICharacter character, IUser currentUser, boolean playerIsActive) {
        if (playerIsActive) {
            System.out.println("DEBUG: It is " + character.getName() + "'s turn.");
            if (observerView) {
                currentUser.renderObserverView(this.currentLevel);
            }
            Position requestedMove = currentUser.getUserMove(character);
            if (requestedMove == null) {
                this.currentLevel.restoreCharacterTile(character);
                sendUpdateToUsers(UpdateType.PLAYER_UPDATE, GameStatus.PLAYER_SELF_ELIMINATES.name(), character);
                return true;
            }
            GameStatus moveStatus = callRuleChecker(character, requestedMove);
            return getPlayerMoveAndExecute(requestedMove, moveStatus, character, currentUser);
        }
        return true;
    }

    /**
     * Requests a move from the given player and assesses its validity then executes it. A player has
     * three tries to give a valid move before losing their turn.
     */
    private boolean getPlayerMoveAndExecute(Position requestedMove, GameStatus moveStatus, ICharacter character, IUser currentUser) {
        int invalidCount = 0;
        // Continues to ask for a move until the requested move from player is valid
        while (moveStatus.equals(GameStatus.INVALID)) {
            invalidCount++;
            //if the player inputs 3 invalid moves, their turn is skipped and they remain in place
            if (invalidCount == 3) {
                currentUser.sendNoMoveUpdate();
                moveStatus = GameStatus.VALID;
                requestedMove = character.getCharacterPosition();
                break;
            }
            currentUser.sendMoveUpdate(moveStatus.toString(), requestedMove, character);
            requestedMove = currentUser.getUserMove(character);
            moveStatus = callRuleChecker(character, requestedMove);
        }
        currentUser.sendMoveUpdate(moveStatus.toString(), requestedMove, character);
        boolean gameStillGoing = checkGameStatus(moveStatus);
        parseMoveStatusAndDoAction(moveStatus.name(), requestedMove, character);
        sendUpdateToUsers(UpdateType.PLAYER_UPDATE, moveStatus.name(), character);
        return gameStillGoing;
    }

    /**
     * Returns a boolean indicating if the game is still going or not.
     * @param moveStatus the status of the actor's most recent move
     * @return a boolean true if the game is still going, false otherwise
     */
    public boolean checkGameStatus(GameStatus moveStatus) {
        return !moveStatus.toString().equals("GAME_WON") && !(moveStatus.toString().equals("GAME_LOST"));
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
                System.out.println("DEBUG: Sending requestedMove to RuleCheckerAdversary");
                rcAdversary = new RuleCheckerZombie(this, currentLevel, (IAdversary) character);
            } else rcAdversary = new RuleCheckerGhost(this, currentLevel, (IAdversary) character);

            moveStatus = rcAdversary.runRuleChecker(requestedMove);
        }
        return moveStatus;
    }

    /**
     * Parses the given GameStatus type and applies specific actions to the level/game based on which type of move it is.
     */
    public void parseMoveStatusAndDoAction(String moveStatus, Position destination, ICharacter c) {
        switch (moveStatus) {
            case "VALID":
                currentLevel.moveCharacter(c, destination);
                break;
            case "KEY_FOUND":
                currentLevel.moveCharacter(c, destination);
                currentLevel.openExitTile();
                this.playerWhoFoundKey = c.getName();
                ((Player) c).increaseNumOfKeysFound();
                break;
            case "PLAYER_SELF_ELIMINATES":
                currentLevel.restoreCharacterTile(c);
                currentLevel.expelPlayer((Player) c);
                expelledPlayers.add((Player) c);
                break;
            case "PLAYER_EXPELLED":
                playerExpelled(destination, c);
                break;
            case "PLAYER_EXITED":
                currentLevel.restoreCharacterTile(c);
                currentLevel.playerLeavesTheLevel(c);
                exitedPlayers.add((Player) c);
                break;
            case "GHOST_TRANSPORTS":
                Position newGhostPos = currentLevel.pickRandomPositionForCharacterInLevel();
                currentLevel.moveCharacter(c, newGhostPos);
                break;
            case "LEVEL_WON":
                levelWon(destination, c);
                break;
            case "GAME_WON":
                gameWon(destination, c);
                break;
            case "GAME_LOST":
                gameLost(destination, c);
                break;
            default:
        }
    }

    private void playerExpelled(Position destination, ICharacter c) {
        Player p = currentLevel.playerAtGivenPosition(destination);
        currentLevel.expelPlayer(p);
        expelledPlayers.add(p);
        p.increaseNumOfTimesExpelled();
        currentLevel.moveCharacter(c, destination);
        IUser playerUser = getUserByName(p.getName());
        playerUser.sendMoveUpdate(GameStatus.PLAYER_EXPELLED.name(), null, null);
        sendUpdateToUsers(UpdateType.PLAYER_UPDATE, GameStatus.PLAYER_EXPELLED.name(), p);
    }

    /**
     *
     *
     * --------------------------END OF LEVEL/GAME METHODS-----------------------------
     *
     *
     */

    /**
     * Actions to conduct when the game has been won by the players.
     */
    private void gameWon(Position destination, ICharacter c) {
        currentLevel.restoreCharacterTile(c);
        addToListOfExitedOrExpelled(destination, c);
    }

    /**
     * Actions to conduct when the game has been lost by the players.
     */
    private void gameLost(Position destination, ICharacter c) {
        currentLevel.restoreCharacterTile(c);
        addToListOfExitedOrExpelled(destination,c);
    }

    /**
     * Actions to conduct when the level has been won.
     */
    private void levelWon(Position destination, ICharacter c) {
        addToListOfExitedOrExpelled(destination, c);
        currentLevel.restoreCharacterTile(c);
        currentLevel.playerLeavesTheLevel(c);
        resurrectPlayers();
        this.currentLevel = this.allLevels.get(getNewLevelNum());
        this.isNewLevel = true;
    }

    /**
     * Adds the given character to the list of exited players if
     * or to the list of expelled players if the character is an adversary.
     */
    public void addToListOfExitedOrExpelled(Position destination, ICharacter c) {
        if (c instanceof IAdversary) {
            Player p2 = currentLevel.playerAtGivenPosition(destination);
            this.currentLevel.expelPlayer(p2);
            p2.increaseNumOfTimesExpelled();
            expelledPlayers.add(p2);
        }
        else {
            if (((Player) c).getIsExpelled()) {
                ((Player)c).increaseNumOfTimesExpelled();
                expelledPlayers.add((Player) c);
            }
            else {
                ((Player)c).increaseNumOfTimesExited();
                exitedPlayers.add((Player) c);
            }
        }
    }

    /**
     * Creates a list of the players' rankings during the Game, based on the number of times they exited or found keys.
     *
     * @return a list-as-string
     */
    public String printPlayerRankings(HashMap<String,Player> allPlayers, String exitedOrKey) {
        HashMap<String, Integer> playerExitedOrExpelledNumbers = new HashMap<>();

        for (Player p : allPlayers.values()) {
            if(exitedOrKey.equals("exited")) {
                playerExitedOrExpelledNumbers.put(p.getName(), p.getNumOfTimesExited());
            }
            else if (exitedOrKey.equals("key")){
                playerExitedOrExpelledNumbers.put(p.getName(), p.getNumOfKeysFound());
            }
        }
        HashMap<String, Integer> playerExitedNumbersSorted = playerExitedOrExpelledNumbers.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        return playerExitedNumbersSorted.toString();
    }

    /**
     *
     *
     * --------------------------LEVEL RESET METHODS-----------------------------
     *
     *
     */

    /**
     * Places all the players on new random positions in the new level.
     * Generate new adversaries and new positions for them.
     */
    private void resetForNewLevel() {
        sendUpdateToUsers(UpdateType.END_LEVEL, "", null);
        // reset data structures for new level
        //this.allCharacters = new LinkedHashSet<>();
        this.exitedPlayers = new ArrayList<>();
        this.expelledPlayers = new ArrayList<>();
        this.playerWhoFoundKey = "";

        // add all players in the game to the new level
        addAllClientsInGameToNewLevel();

        //register appropriate number of adversaries to new level
        // TODO: determine if there are enough remote adversaries for the level, if not, register
        // TODO: automated adversaries to fill in the gaps (don't reset allCharacters completely, just keep adding adversaries with each level)
        //registerAutomatedAdversaries();
    }

    /**
     * Helper method for resetForNewLevel(). Adds all players in the game to the new level
     */
    public void addAllClientsInGameToNewLevel() {
        for (IUser user : this.users) {
            String name = user.getUserName();
            ICharacter character = getActorFromName(name);
            Position randomPos = this.currentLevel.pickRandomPositionForCharacterInLevel();
            currentLevel.addCharacter(character, randomPos);
            allCharacters.add(character);
        }
    }

    /**
     * Resurrects all expelled players once the level has been won by players, so that they can all
     * move onto the next level.
     */
    private void resurrectPlayers() {
        for (Player player : this.allPlayers.values()) {
            if (player.getIsExpelled()) {
                player.setIsExpelled(false);
            }
        }
    }

    /**
     *
     *
     * --------------------------GAME REGISTRATION METHODS-----------------------------
     *
     *
     */

    /**
     * Registers a player with a given unique name and add them to the level.
     *
     * @param name the name of player to register
     */
    public Registration registerPlayer(String name, Registration playerType) {
        if (allPlayers.containsKey(name)) {
            return Registration.DUPLICATE_NAME;
        }
        else if (allPlayers.size() < 4) {
            addUser(name, playerType);
            Player newPlayer = new Player(name);
            assignPlayerAvatar(newPlayer);
            allPlayers.put(name, newPlayer);
            allCharacters.add(newPlayer);
            Position randomPos = currentLevel.pickRandomPositionForCharacterInLevel();
            currentLevel.addCharacter(newPlayer, randomPos);
            return Registration.REGISTERED;
        }
        else {
            return Registration.AT_CAPACITY;
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
     * Helper method for resetNewLevel(). Creates the correct amount of adversaries for the new level.
     */
    public void registerAutomatedAdversaries() {
        int levelNum = this.allLevels.indexOf(this.currentLevel);
        int numOfZombies = (int) (Math.floor((levelNum + 1) / 2) + 1);
        int numOfGhosts = (int) Math.floor(levelNum / 2);

        for (int z = 1; z < numOfZombies + 1; z++) {
            registerAdversary("zombie" + z, "zombie", Registration.LOCAL);
        }
        for (int g = 1; g < numOfGhosts + 1; g++) {
            registerAdversary("ghost" + g, "ghost", Registration.LOCAL);
        }
    }

    /**
     * Registers an adversary with a given unique name and add them to the level.
     *
     * @param name the name of adversary to register
     * @param type which type of adversary it is
     */
    public Registration registerAdversary(String name, String type, Registration adversaryType) {
        if (allCharacters.contains(name)) {
            return Registration.DUPLICATE_NAME;
        }
        IAdversary adversary = null;
        if (type.equalsIgnoreCase("zombie")) {
            adversary = new Zombie(name);
        }
        else if (type.equalsIgnoreCase("ghost")) {
            adversary = new Ghost(name);
        }
        addUser(name, adversaryType);
        this.allCharacters.add(adversary);
        Position pickedPos = currentLevel.pickRandomPositionForCharacterInLevel();
        currentLevel.addCharacter(adversary, new Position(pickedPos.getRow(), pickedPos.getCol()));
        return Registration.REGISTERED;
    }

    /**
     *
     *
     * --------------------------USER/OBSERVER METHODS-----------------------------
     *
     *
     */

    private void addUser(String name, Registration playerType) {
        IUser user;
        if (playerType.equals(Registration.LOCAL)) {
            user = new LocalUser(name);
        }
        else {
            user = new RemoteUser(name);
        }
        this.users.add(user);
    }

    public void sendUpdateToUsers(UpdateType type, String moveStatus, ICharacter character) {
        for (IUser user : users) {
            if (user instanceof RemoteUser) {
                RemoteUser ru = (RemoteUser) user;
                ICharacter usersCharacter = getActorFromName(user.getUserName());
                switch (type) {
                    case START_LEVEL:
                        ru.sendStartLevelMessage(this.allLevels.indexOf(this.currentLevel) + 1);
                        break;
                    case START_ROUND:
                        ru.sendInitialUpdate(usersCharacter);
                        break;
                    case PLAYER_UPDATE:
                        ru.sendPlayerUpdateMessage(moveStatus, character, usersCharacter);
                        break;
                    case END_LEVEL:
                        ru.sendEndLevelMessage();
                        break;
                    case END_GAME:
                        ru.sendEndGameMessage();
                        break;
                    default:
                }
            }
        }
    }

    /**
     * Sends a series of updates about the game state to all observers, including the character whose turn it is,
     * where they requested to move, the status of that move, and lists representing other information about the level.
     */
    private void sendUpdatesToObservers(ICharacter character, Position requestedMove, GameStatus moveStatus,
                                        Level currentLevel, ArrayList<Player> exitedPlayers, ArrayList<Player> expelledPlayers) {
        for (IObserver observer : observers) {
            observer.sendUpdates(character, requestedMove, moveStatus, currentLevel, exitedPlayers, expelledPlayers);
        }
    }

    /**
     * Creates a connection between the remote user and game manager.
     */
    public void passConnectionToRemoteUser(String name, ClientThread conn) {
        RemoteUser user = (RemoteUser) getUserByName(name);
        user.setRemoteUserConnection(conn);
    }

    /**
     *
     *
     * --------------------------GETTERS AND SETTERS -----------------------------
     *
     *
     */


    /**
     * Sets the observer view to whatever boolean is given.
     */
    public void setObserverView(boolean isObserverView) {
        this.observerView = isObserverView;
    }

    /**
     * Returns the IUser object based on the current character's name.
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
     * Returns a Player object based on a given avatar.
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
     * Returns the ICharacter associated with a given userName
     */
    private ICharacter getActorFromName(String userName) {
        for (ICharacter character : allCharacters) {
            if (character.getName().equals(userName)) {
                return character;
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

    /**
     * Returns the current level of the game
     */
    public Level getCurrentLevel() {
        return this.currentLevel;
    }

    /**
     * Gets a new index for the allLevels list to generate a new level to be played.
     *
     * @return a number representing
     */
    private int getNewLevelNum() {
        return this.allLevels.indexOf(this.currentLevel) + 1;
    }

    /**
     * Getter for playerWhoFoundKey
     */
    public String getPlayerWhoFoundKey() {
        return this.playerWhoFoundKey;
    }
}