package Game;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class GameManager {
    LinkedHashMap<String, Player> allPlayers = new LinkedHashMap<>();
    LinkedHashSet<ICharacter> allCharacters = new LinkedHashSet<>();
    ArrayList<Level> allLevels = new ArrayList<Level>();
    Level currentLevel;

    public GameManager() {
        Controller controller = new Controller();
        startGame();
    }

    /**
     * This method kick-starts the Snarl game.
     */
    public void startGame() {
        for (int i=0; i<allCharacters.toArray().length; i++) {
            ICharacter character = (ICharacter)allCharacters.toArray()[i];

            Position requestedMove = getUserMove();
            GameStatus moveStatus = GameStatus.DEFAULT;
            if (character instanceof Player) {
                RuleCheckerPlayer rcPlayer = new RuleCheckerPlayer(currentLevel, (Player)character);
                moveStatus = rcPlayer.runRuleChecker(requestedMove);
            }
            else if (character instanceof IAdversary) {
                RuleCheckerAdversary rcAdversary = new RuleCheckerAdversary(currentLevel, (IAdversary)character);
                moveStatus = rcAdversary.runRuleChecker(requestedMove);
            }

            parseMoveStatusAndDoAction(moveStatus, requestedMove, character);

            // check if we're on the last character in the list and if so, loop back to the beginning
            if (i == allCharacters.size() - 1) {
                i = 0;
            }
        }
    }

    /**
     * The purpose of this function is to collect the user's desired move position.
     * This function will be implemented at a later milestone.
     * @return the resulting Position in level where the user would like to move to
     */
    private Position getUserMove() {
        return new Position(0,0);
    }

    /**
     * Parses the given GameStatus type and applies specific actions based on which type of move it is.
     *
     * @param moveStatus: the returned enum GameStatus that signifies what type the requested move is
     * @param destination: the requested destination from the user
     * @param c: the current character whose move it is
     */
    private void parseMoveStatusAndDoAction(GameStatus moveStatus, Position destination, ICharacter c) {
        switch (moveStatus) {
            case VALID:
                currentLevel.moveCharacter(c, destination);
            case INVALID:
                System.out.print("The move you requested was invalid, please make another move.");
            case KEY_FOUND:
                currentLevel.moveCharacter(c, destination);
                currentLevel.openExitTile();
            case PLAYER_SELF_ELIMINATES:
                currentLevel.moveCharacter(c, destination);
                currentLevel.expelPlayer((Player) c);
            case PLAYER_EXPELLED:
                currentLevel.moveCharacter(c, destination);
                currentLevel.expelPlayer(currentLevel.playerAtGivenPosition(destination));
            case LEVEL_WON:
                currentLevel.moveCharacter(c, destination);
                resurrectPlayers();
                System.out.print("Congrats!! Players have won the level!");
            case GAME_WON:
                System.out.print("Congrats!! Players have won the game!");
            case GAME_LOST:
                System.out.print("Sorry :( Players have lost the game! Play again?");
            default:
                System.out.print("Default case.Should never get here.");
        }
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
     * Registers a player with a given unique name.
     *
     * Not called anywhere for Milestone 5 because we still don't know whe
     */
    public void registerPlayer(String name) {
        if (allPlayers.containsKey(name)) {
            System.out.println("Cannot register Player with name `" + name + "`. Name already has been taken. Please pick again.");
        }
        else if (allPlayers.size() < 4) {
            Player newPlayer = new Player(name);
            allPlayers.put(name, newPlayer);
            allCharacters.add(newPlayer);
            currentLevel.addPlayer(newPlayer, new Position(some_random_position_in_level));
            System.out.println("Player " + name + " has been registered.");
        }
        else {
            System.out.println("Cannot register player " + name + ". Game has reached maximum participant count. Sorry!");
        }
    }

    /**
     * Registers an adversary with a given unique ID.
     */
    public void registerAdversaries(int id) {
        IAdversary adversary = new Ghost(id);
        this.allCharacters.add(adversary);
        System.out.println("New adversary has been registered.");
        //currentLevel.addAdversary(adversary, new Position);
    }
}

// TO DO
// 1) Figure out what position to add characters in initially
// 2) update players on changes to the game state as they happen
// 3) request moves from players/users
// 4) decide where a level is coming from
