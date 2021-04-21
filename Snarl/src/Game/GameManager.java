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
    String ZOMBIE_NAME = "zombie";
    String GHOST_NAME = "ghost";
    String[] avatars = {Avatars.PLAYER_1.toString(), Avatars.PLAYER_2.toString(), Avatars.PLAYER_3.toString(), Avatars.PLAYER_4.toString()};
    ArrayList<String> playerAvatars = new ArrayList<>(Arrays.asList(avatars));
    LinkedHashMap<String, Player> allPlayers = new LinkedHashMap<>();
    LinkedHashSet<ICharacter> allCharacters = new LinkedHashSet<>();
    ArrayList<IAdversary> remoteAdversaries = new ArrayList<>();
    ArrayList<Level> allLevels;
    Level currentLevel;
    ArrayList<IObserver> observers = new ArrayList<>();
    ArrayList<IUser> users = new ArrayList<>();
    int startLevel;
    boolean isNewLevel = false;
    boolean observerView = false;
    String playerWhoFoundKey = null;

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
                index = 0;
                sendUpdateToUsers(UpdateType.START_LEVEL, GameStatus.NONE, null);
                sendUpdateToUsers(UpdateType.START_ROUND, GameStatus.NONE, null);
            }
            ICharacter character = (ICharacter) allCharacters.toArray()[index];
            boolean playerIsActive = checkPlayerActiveStatus(character);
            IUser currentUser = getUserByName(character.getName());

            // Move the adversary
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
        sendUpdateToUsers(UpdateType.END_GAME, GameStatus.NONE, null);
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
        Position chosenMove;
        AdversaryMovement am = new AdversaryMovement(this.currentLevel);
        if (currentUser instanceof LocalUser) {
            chosenMove = am.chooseAdversaryMove(currentUser, (IAdversary) character);
        }
        else {
            currentUser.renderObserverView(this.currentLevel);
            chosenMove = currentUser.getUserMove(character);
        }

        GameStatus moveStatus = callRuleChecker(character, chosenMove);

        int invalidCount = 0;
        // generate a new move until we get one that's not invalid
        while (moveStatus.equals(GameStatus.INVALID)) {
            invalidCount++;
            // if there are no valid moves in any cardinal direction, keep the adversary stationary
            if (invalidCount == 4) {
                moveStatus = GameStatus.VALID;
                chosenMove = character.getCharacterPosition();
                break;
            } else {
                if(currentUser instanceof LocalUser) {
                    chosenMove = am.chooseAdversaryMove(currentUser, (IAdversary) character);
                }
                else {
                    chosenMove = currentUser.getUserMove(character);
                }
                moveStatus = callRuleChecker(character, chosenMove);
            }
        }

        parseMoveStatusAndDoAction(moveStatus, chosenMove, character);
        currentUser.sendMoveUpdate(moveStatus, chosenMove, character);
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
            if (requestedMove == null) {
                this.currentLevel.restoreCharacterTile(character);
                sendUpdateToUsers(UpdateType.PLAYER_UPDATE, GameStatus.PLAYER_SELF_ELIMINATES, character);
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
            currentUser.sendMoveUpdate(moveStatus, requestedMove, character);
            requestedMove = currentUser.getUserMove(character);
            moveStatus = callRuleChecker(character, requestedMove);
        }
        currentUser.sendMoveUpdate(moveStatus, requestedMove, character);
        boolean gameStillGoing = checkGameStatus(moveStatus);
        parseMoveStatusAndDoAction(moveStatus, requestedMove, character);
        sendUpdateToUsers(UpdateType.PLAYER_UPDATE, moveStatus, character);
        return gameStillGoing;
    }

    /**
     * Returns a boolean indicating if the game is still going or not.
     */
    public boolean checkGameStatus(GameStatus moveStatus) {
        return !moveStatus.equals(GameStatus.GAME_WON) && !(moveStatus.equals(GameStatus.GAME_LOST));
    }

    /**
     * Checks if the current user is a Player who has been expelled from the game.
     */
    public boolean checkPlayerActiveStatus(ICharacter character) {
        if (character instanceof Player) {
            return !(this.currentLevel.getExitedPlayers()).contains(character) &&
                    !(this.currentLevel.getExpelledPlayers()).contains(character);
        }
        return true;
    }

    /**
     * Calls the RuleChecker Interface depending on if the user is a Player or an Adversary.
     * Returns a GameStatus that indicates what kind of move the requested move was/ how it impacted the game.
     */
    public GameStatus callRuleChecker(ICharacter character, Position requestedMove) {
        GameStatus moveStatus;
        if (character instanceof Player) {
            RuleCheckerPlayer rcPlayer = new RuleCheckerPlayer(currentLevel, (Player) character);
            moveStatus = rcPlayer.runRuleChecker(requestedMove);
        }
        else if (character instanceof Zombie) {
            IRuleChecker rcAdversary = new RuleCheckerZombie(currentLevel, (IAdversary) character);
            moveStatus = rcAdversary.runRuleChecker(requestedMove);
        }
        else {
            IRuleChecker rcAdversary = new RuleCheckerGhost(currentLevel, (IAdversary) character);
            moveStatus = rcAdversary.runRuleChecker(requestedMove);
        }
        return moveStatus;
    }

    /**
     * Parses the given GameStatus type and applies specific actions to the level/game based on which type of move it is.
     */
    public void parseMoveStatusAndDoAction(GameStatus moveStatus, Position destination, ICharacter c) {
        switch (moveStatus) {
            case VALID:
                this.currentLevel.moveCharacter(c, destination);
                break;
            case KEY_FOUND:
                this.currentLevel.moveCharacter(c, destination);
                this.currentLevel.openExitTile();
                this.playerWhoFoundKey = c.getName();
                ((Player) c).increaseNumOfKeysFound();
                break;
            case PLAYER_SELF_ELIMINATES:
                this.currentLevel.restoreCharacterTile(c);
                this.currentLevel.expelPlayer((Player) c);
                ((Player) c).increaseNumOfTimesExpelled();
                break;
            case PLAYER_EXPELLED:
                playerExpelled(destination, c);
                break;
            case PLAYER_EXITED:
                this.currentLevel.restoreCharacterTile(c);
                this.currentLevel.playerLeavesTheLevel(c);
                ((Player) c).increaseNumOfTimesExited();
                break;
            case GHOST_TRANSPORTS:
                Position newGhostPos = this.currentLevel.pickRandomPositionForCharacterInLevel();
                currentLevel.moveCharacter(c, newGhostPos);
                break;
            case LEVEL_WON:
                levelWon(destination, c);
                break;
            case GAME_WON:
                gameWon(destination, c);
                break;
            case GAME_LOST:
                gameLost(destination, c);
                break;
            default:
                System.out.println("Something went wrong.");
        }
    }

    /**
     * Actions to conduct when a player has been expelled by an adversary.
     */
    private void playerExpelled(Position destination, ICharacter c) {
        Player p = currentLevel.playerAtGivenPosition(destination);
        currentLevel.expelPlayer(p);
        p.increaseNumOfTimesExpelled();
        currentLevel.moveCharacter(c, destination);
        IUser playerUser = getUserByName(p.getName());
        playerUser.sendMoveUpdate(GameStatus.PLAYER_EXPELLED, null, null);
        sendUpdateToUsers(UpdateType.PLAYER_UPDATE, GameStatus.PLAYER_EXPELLED, p);
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
        this.currentLevel.restoreCharacterTile(c);
        addToListOfExitedOrExpelled(destination, c);
    }

    /**
     * Actions to conduct when the game has been lost by the players.
     */
    private void gameLost(Position destination, ICharacter c) {
        this.currentLevel.restoreCharacterTile(c);
        addToListOfExitedOrExpelled(destination,c);
    }

    /**
     * Actions to conduct when the level has been won.
     */
    private void levelWon(Position destination, ICharacter c) {
        addToListOfExitedOrExpelled(destination, c);
        this.currentLevel.restoreCharacterTile(c);
        sendUpdateToUsers(UpdateType.END_LEVEL, GameStatus.NONE, null);
        resurrectPlayers();
        int newLevelIndex = getNewLevelNum();
        this.currentLevel = this.allLevels.get(newLevelIndex);
        if (newLevelIndex == this.allLevels.size() - 1) {
            this.currentLevel.setIsLastLevelOfGame(true);
        }
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
        }
        else {
            if (((Player) c).getIsExpelled()) {
                ((Player)c).increaseNumOfTimesExpelled();
                this.currentLevel.expelPlayer((Player) c);
            }
            else {
                ((Player)c).increaseNumOfTimesExited();
                this.currentLevel.playerLeavesTheLevel(c);
            }
        }
    }

    /**
     * Creates a list of the players' rankings during the Game, based on the number of times they exited or found keys.
     */
    public String printPlayerRankings(HashMap<String,Player> allPlayers, TileType exitedOrKey) {
        HashMap<String, Integer> playerExitedOrExpelledNumbers = new HashMap<>();

        for (Player p : allPlayers.values()) {
            if (exitedOrKey.equals(TileType.UNLOCKED_EXIT)) {
                playerExitedOrExpelledNumbers.put(p.getName(), p.getNumOfTimesExited());
            }
            else if (exitedOrKey.equals(TileType.KEY)){
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
        // reset data structures for new level
        this.playerWhoFoundKey = null;

        // add all players in the game to the new level
        addAllClientsInGameToNewLevel();

        // register appropriate number of adversaries to new level, if some are needed
        registerAutomatedAdversaries();
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
        int levelNum = this.allLevels.indexOf(this.currentLevel) + 1;
        int numOfZombiesNeeded = (int) (Math.floor(levelNum / 2) + 1);
        int numOfGhostsNeeded = (int) Math.floor((levelNum - 1) / 2);
        int numOfZombiesToFill = numOfZombiesNeeded;
        int numOfGhostsToFill = numOfGhostsNeeded;

        int currentNumOfZombies = 0;
        int currentNumOfGhosts = 0;
        for (ICharacter character : allCharacters) {
            if (character instanceof Zombie) {
                numOfZombiesToFill--;
                currentNumOfZombies++;
            }
            if (character instanceof Ghost) {
                numOfGhostsToFill--;
                currentNumOfGhosts++;
            }
        }

        if (currentNumOfZombies + currentNumOfGhosts != numOfZombiesNeeded + numOfGhostsNeeded) {
            for (int z = 1; z < numOfZombiesToFill + 1; z++) {
                registerAdversary(ZOMBIE_NAME + z, Avatars.ZOMBIE, Registration.LOCAL);
            }
            for (int g = 1; g < numOfGhostsToFill + 1; g++) {
                registerAdversary(GHOST_NAME + g, Avatars.GHOST, Registration.LOCAL);
            }
        }
    }

    /**
     * Registers an adversary with a given unique name and add them to the level.
     */
    public Registration registerAdversary(String name, Avatars type, Registration adversaryType) {
        for (ICharacter character : allCharacters) {
            if (character.getName().equals(name)) {
                return Registration.DUPLICATE_NAME;
            }
        }
        IAdversary adversary = null;
        if (type.equals(Avatars.ZOMBIE)) {
            adversary = new Zombie(name);
        }
        else if (type.equals(Avatars.GHOST)) {
            adversary = new Ghost(name);
        }

        if (adversaryType.equals(Registration.REMOTE)) {
            remoteAdversaries.add(adversary);
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

    /**
     * Adds a user to the level -- the tie from the game state to the client/player playing the game.
     */
    public void addUser(String name, Registration playerType) {
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
     * Sends specific update methods to all users depending on the update type input.
     */
    public void sendUpdateToUsers(UpdateType type, GameStatus moveStatus, ICharacter character) {
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
     * --------------------------GETTERS AND SETTERS-----------------------------
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
     * Returns the ICharacter associated with a given user name.
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
     * Returns the current level of the game.
     */
    public Level getCurrentLevel() {
        return this.currentLevel;
    }

    public ArrayList<Player> getLevelsExitedPlayers() {
        return this.currentLevel.getExitedPlayers();
    }

    public ArrayList<Player> getLevelsExpelledPlayers() {
        return this.currentLevel.getExpelledPlayers();
    }

    /**
     * Gets a new index for the allLevels list to generate a new level to be played.
     */
    private int getNewLevelNum() {
        return this.allLevels.indexOf(this.currentLevel) + 1;
    }

    /**
     * Returns the name of the player who found the key.
     */
    public String getPlayerWhoFoundKey() {
        return this.playerWhoFoundKey;
    }

    /**
     * Get the list of remote adversaries.
     */
    public ArrayList<IAdversary> getRemoteAdversaries() {
        return this.remoteAdversaries;
    }
}