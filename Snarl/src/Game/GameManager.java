package Game;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class GameManager {
    LinkedHashMap<String, Player> allPlayers = new LinkedHashMap<>();
    LinkedHashSet<ICharacter> allCharacters = new LinkedHashSet<>();
    ArrayList<Level> allLevels = new ArrayList<Level>();
    Level currentLevel;

    public GameManager(ArrayList<Level> allLevels) {
        this.currentLevel = allLevels.get(0);
    }

    /**
     * This method kick-starts the Snarl game.
     */
    public void startGame() {
        boolean gameStillGoing = true;
        int index = 0;
        while (gameStillGoing) {
            ICharacter character = (ICharacter)allCharacters.toArray()[index];
            boolean playerIsActive = checkPlayerActiveStatus(character);
            broadcastTurn(character, playerIsActive);
            if (playerIsActive) {
                Position requestedMove = getUserMove(character); // will be more legitimately implemented at a later milestone
                GameStatus moveStatus = getGameStatusOfMove(character, requestedMove);
                gameStillGoing = parseMoveStatusAndDoAction(moveStatus, requestedMove, character);
            }
            // check if we're on the last character in the list and if so, loop back to the beginning
            if (index == allCharacters.size() - 1) {
                index = 0;
            }
            else {
                index++;
            }
        }
        System.out.println("Game has ended.");
        // add other game terminus actions once networking elements/client/scanner/etc elements are known
    }

    /**
     * Calls the rule checker to retrieve the status of the game with the requested move.
     * If the move is invalid, the user is asked for a new move until the status is not invalid.
     *
     * @param character the character whose turn it is
     * @param requestedMove the requested destination by the user
     * @return a non-invalid GameStatus for the requested move
     */
    public GameStatus getGameStatusOfMove(ICharacter character, Position requestedMove) {
        GameStatus moveStatus = callRuleChecker(character, requestedMove);
        while (moveStatus.equals(GameStatus.INVALID)) {
            System.out.println("The move you requested was invalid, please make another move.");
            requestedMove = getUserMove(character);
            moveStatus = callRuleChecker(character, requestedMove);
        }
        return moveStatus;
    }

    /**
     * Checks if the current user is a Player who has been expelled from the game.
     *
     * @param character the user whose turn it is
     * @return Returns true if the Player has been expelled/are no longer active, false if the Player is
     *         still active or if the character is an Adversary (i.e. ghost, zombie)
     */
    private boolean checkPlayerActiveStatus(ICharacter character) {
        if (character instanceof Player) {
            return !((Player) character).getIsExpelled();
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
            System.out.println(character.getCharacterPosition());
            RuleCheckerPlayer rcPlayer = new RuleCheckerPlayer(currentLevel, (Player)character);
            moveStatus = rcPlayer.runRuleChecker(requestedMove);
        }
        else if (character instanceof IAdversary) {
            RuleCheckerAdversary rcAdversary = new RuleCheckerAdversary(currentLevel, (IAdversary)character);
            moveStatus = rcAdversary.runRuleChecker(requestedMove);
        }
        return moveStatus;
    }

    /**
     * Broadcasts to the character that it's their turn and then renders their specific level view
     * (i.e., for a Player, show maximum two tiles away for all directions)
     *
     * @param character the current character whose turn it is
     */
    private void broadcastTurn(ICharacter character, boolean isPlayerActive) {
        if (isPlayerActive) {
            System.out.print(character.getName() + ": it is your turn to make a move. Here is your view:");
        }
        else {
            System.out.print("Sorry, " + character.getName() + ", you're expelled. No move for you! Here's the view of your last position:");
        }
        renderView(character);
    }

    /**
     * Renders the view for the given character before they request a move.
     * Currently only accounts for players, whose maximum tile view is 2 on all sides.
     *
     * @param character the character whose turn it is
     */
    private void renderView(ICharacter character) {
    }


    /**
     * The purpose of this function is to collect the user's desired move position.
     * This function will be implemented at a later milestone.
     *
     * @return the resulting Position in level where the user would like to move to
     */
    private Position getUserMove(ICharacter character) {
        return new Position(0,0);
    }

    /**
     * Parses the given GameStatus type and applies specific actions based on which type of move it is.
     *
     * @param moveStatus: the returned enum GameStatus that signifies what type the requested move is
     * @param destination: the requested destination from the user
     * @param c: the current character whose move it is
     * @return a boolean indicating if the game is still in play
     */
    private boolean parseMoveStatusAndDoAction(GameStatus moveStatus, Position destination, ICharacter c) {
        switch (moveStatus) {
            case VALID:
                currentLevel.moveCharacter(c, destination);
                return true;
            case KEY_FOUND:
                currentLevel.moveCharacter(c, destination);
                currentLevel.openExitTile();
                return true;
            case PLAYER_SELF_ELIMINATES:
                currentLevel.moveCharacter(c, destination);
                currentLevel.expelPlayer((Player) c);
                return true;
            case PLAYER_EXPELLED:
                currentLevel.moveCharacter(c, destination);
                currentLevel.expelPlayer(currentLevel.playerAtGivenPosition(destination));
                return true;
            case PLAYER_EXITED:
                currentLevel.moveCharacter(c, destination);
                currentLevel.playerPassedThroughExit(c);
                return true;
            case LEVEL_WON:
                currentLevel.moveCharacter(c, destination);
                currentLevel.playerPassedThroughExit(c);
                resurrectPlayers();
                System.out.print("Congrats!! Players have won the level!");
                return false; // THIS CAN CHANGE TO TRUE ONCE WE'RE DEALING WITH MORE THAN ONE LEVEL
            case GAME_WON:
                System.out.print("Congrats!! Players have won the game!");
                return false;
            case GAME_LOST:
                System.out.print("Sorry :( Players have lost the game! Play again?");
                return false;
            default:
                System.out.print("Default case.Should never get here.");
        }
        // will never get here
        return false;
    }

    /**
     * Resurrects all expelled players once the level has been won by players.
     */
    private void resurrectPlayers() {
        for (Player player : this.allPlayers.values()) {
            if (player.getIsExpelled()) {
                player.setIsExpelled(false);
            }
        }
    }

    /**
     * Registers a player with a given unique name and add them to the level.
     *
     * Not called anywhere for Milestone 5 because we don't know user entry point yet.
     */
    public void registerPlayer(String name) {
        if (allPlayers.containsKey(name)) {
            System.out.println("Cannot register Player with name `" + name + "`. Name already has been taken. Please pick again.");
        }
        else if (allPlayers.size() < 4) {
            Player newPlayer = new Player(name);
            allPlayers.put(name, newPlayer);
            allCharacters.add(newPlayer);
            Position randomPos = currentLevel.pickRandomPositionForCharacter(newPlayer);
            currentLevel.addPlayer(newPlayer, randomPos);
            System.out.println("Player " + name + " has been registered.");
        }
        else {
            System.out.println("Cannot register player " + name + ". Game has reached maximum participant count. Sorry!");
        }
    }

    /**
     * Registers an adversary with a given unique name and add them to the level.
     *
     * Not called anywhere for Milestone 5 because we don't know user entry point yet.
     */
    public void registerAdversary(String name) {
        IAdversary adversary = new Ghost(name);
        this.allCharacters.add(adversary);
        System.out.println("New adversary " + name + " has been registered.");
        currentLevel.addAdversary(adversary, currentLevel.pickRandomPositionForCharacter(adversary));
    }

    public Level getCurrentLevel() {
        return this.currentLevel;
    }
}

// TO DO
// 2) update players on changes to the game state as they happen
// 3) request moves from players/users
// 4) decide where a level is coming from
// 5) registering different types of adversaries

// 3) Register players to the game
// 4) Add Javadocs to all methods