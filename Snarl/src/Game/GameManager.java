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
                currentUser.broadcastUpdate(this.currentLevel, character, playerIsActive);
                gameStillGoing = playersMove(character, currentUser, playerIsActive);
            }
            // check if we're on the last character in the list and if so, loop back to the beginning
            if (index == allCharacters.size() - 1) {
                index = 0;
            } else {
                index++;
            }
        }
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
        Position chosenMove = chooseAdversaryMove(currentUser, (IAdversary) character);
        GameStatus moveStatus = callRuleChecker(character, chosenMove);
        int invalidCount = 0;
        // generate a new move until we get one that's not invalid
        while (moveStatus.toString().equals("INVALID")) {
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
        parseMoveStatusAndDoAction(moveStatus.name(), chosenMove, character, currentUser);
        return checkGameStatus(moveStatus);
    }

    /**
     * Checks if a player is active and if so, requests a move from them. If observer view is enabled,
     * the current level view is outputted to the user.
     */
    private boolean playersMove(ICharacter character, IUser currentUser, boolean playerIsActive) {
        if (playerIsActive) {
            if (observerView) {
                currentUser.renderObserverView(this.currentLevel);
            }
            Position requestedMove = currentUser.getUserMove(character);
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
        while (moveStatus.toString().equals("INVALID")) {
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
        parseMoveStatusAndDoAction(moveStatus.name(), requestedMove, character, currentUser);
        sendUpdateToUsers(moveStatus.name(), character);
        return gameStillGoing;
    }

    /**
     * Requests a move from the given player and assesses its validity then executes it.
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
                rcAdversary = new RuleCheckerZombie(this, currentLevel, (IAdversary) character);
            } else rcAdversary = new RuleCheckerGhost(this, currentLevel, (IAdversary) character);

            moveStatus = rcAdversary.runRuleChecker(requestedMove);
        }
        return moveStatus;
    }

    /**
     * Parses the given GameStatus type and applies specific actions to the level/game based on which type of move it is.
     */
    public void parseMoveStatusAndDoAction(String moveStatus, Position destination, ICharacter c, IUser currentUser) {
        switch (moveStatus) {
            case "VALID":
                currentLevel.moveCharacter(c, destination);
                break;
            case "KEY_FOUND":
                currentLevel.moveCharacter(c, destination);
                currentLevel.openExitTile();
                ((Player) c).increaseNumOfKeysFound();
                break;
            case "PLAYER_SELF_ELIMINATES":
                currentLevel.restoreCharacterTile(c);
                currentLevel.expelPlayer((Player) c);
                expelledPlayers.add((Player) c);
                break;
            case "PLAYER_EXPELLED":
                Player p = currentLevel.playerAtGivenPosition(destination);
                currentLevel.expelPlayer(p);
                expelledPlayers.add(p);
                currentLevel.moveCharacter(c, destination);
                break;
            case "PLAYER_EXITED":
                currentLevel.restoreCharacterTile(c);
                currentLevel.playerPassedThroughExit(c);
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
        currentUser.sendMoveUpdate(moveStatus, destination, c);
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
        Player p3 = currentLevel.playerAtGivenPosition(destination);
        currentLevel.restoreCharacterTile(c);
        currentLevel.playerPassedThroughExit(c);
        addToListOfExitedOrExpelled(p3, c);
    }

    /**
     * Actions to conduct when the game has been lost by the players.
     */
    private void gameLost(Position destination, ICharacter c) {
        Player p4 = currentLevel.playerAtGivenPosition(destination);
        currentLevel.restoreCharacterTile(c);
        if (c instanceof Player) {
            currentLevel.expelPlayer((Player) c);
            expelledPlayers.add((Player) c);
        } else {
            currentLevel.expelPlayer(p4);
            expelledPlayers.add(p4);
        }
    }

    /**
     * Actions to conduct when the level has been won.
     */
    private void levelWon(Position destination, ICharacter c) {
        Player p2 = currentLevel.playerAtGivenPosition(destination);
        currentLevel.restoreCharacterTile(c);
        currentLevel.playerPassedThroughExit(c);
        addToListOfExitedOrExpelled(p2, c);
        resurrectPlayers();
        this.currentLevel = this.allLevels.get(getNewLevelNum());
        this.isNewLevel = true;
    }

    /**
     * Adds the given character to the list of exited players if they are a player and they left the game,
     * or to the list of expelled players if the character is an adversary.
     */
    private void addToListOfExitedOrExpelled(Player p,ICharacter c) {
        if (c instanceof Player) {
            exitedPlayers.add((Player) c);
        } else {
            expelledPlayers.add(p);
        }
    }

    /**
     * Creates a list of the players' rankings during the Game, based on the number of times they exited or found keys.
     *
     * @return a list-as-string
     */
    public String printPlayerExitedOrKeyRankings(HashMap<String,Player> allPlayers, String exitedOrKey) {
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
     * Gets a new index for the allLevels list to generate a new level to be played.
     *
     * @return a number representing
     */
    private int getNewLevel() {
        return this.allLevels.indexOf(this.currentLevel) + 1;
    }

    /**
     * Helper method for resetForNewLevel(). Adds all players in the game to the new level
     */
    public void addAllPlayersInGameToNewLevel() {
        for (Player player : this.allPlayers.values()) {
            Position randomPos = this.currentLevel.pickRandomPositionForCharacterInLevel();
            currentLevel.addCharacter(player, randomPos);
            allCharacters.add(player);
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

    /**
     * Helper method for resetNewLevel(). Creates the correct amount of adversaries for the new level.
     */
    public void registerAutomatedAdversaries() {
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
     * Registers an adversary with a given unique name and add them to the level.
     *
     * @param name the name of adversary to register
     * @param type which type of adversary it is
     */
    public void registerAdversary(String name, String type) {
        IAdversary adversary = null;
        if (type.equalsIgnoreCase("zombie")) {
            adversary = new Zombie(name);
            LocalUser user = new LocalUser(name);
            this.users.add(user);
        }
        else if (type.equalsIgnoreCase("ghost")) {
            adversary = new Ghost(name);
            LocalUser user = new LocalUser(name);
            this.users.add(user);
        }
        this.allCharacters.add(adversary);
        Position pickedPos = currentLevel.pickRandomPositionForCharacterInLevel();
        currentLevel.addCharacter(adversary, new Position(pickedPos.getRow(), pickedPos.getCol()));
    }

    /**
     *
     *
     * --------------------------USER/OBSERVER METHODS-----------------------------
     *
     *
     */



    public void sendInitialUpdateToUsers() {
        for (IUser user : users) {
            if (user instanceof RemoteUser) {
                RemoteUser ru = (RemoteUser) user;
                ICharacter usersCharacter = getPlayerFromName(user.getUserName());
                ru.sendInitialUpdate(usersCharacter);
            }
        }
    }

    private void sendUpdateToUsers(String moveStatus, ICharacter character) {
        for (IUser user : users) {
            if (user instanceof RemoteUser) {
                RemoteUser ru = (RemoteUser) user;
                ICharacter usersCharacter = getPlayerFromName(user.getUserName());
                ru.sendPlayerUpdateMessage(moveStatus, character, usersCharacter);
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
    private ICharacter getPlayerFromName(String userName) {
        Player p = allPlayers.get(userName);
        return p;
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
}