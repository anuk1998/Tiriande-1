package Game;

import java.util.ArrayList;

public class RuleCheckerPlayer implements IRuleChecker {
    Level currentLevel;
    Player player;

    public RuleCheckerPlayer(Level currentLevel, Player player) {
        this.currentLevel = currentLevel;
        this.player = player;
    }

    @Override
    public GameStatus runRuleChecker(Position destination) {
        GameStatus status = GameStatus.INVALID;
        if (isValidMove(destination)) {
            status = GameStatus.VALID;
            if (currentLevel.isOccupiedByAdversary(destination)) {
                status = encountersOppositeCharacter();
            } else if (currentLevel.getKeyPositionInLevel().equals(destination)) {
                status = keyTileIsLandedOn();
            } else if (currentLevel.getExitPositionInLevel().equals(destination)) {
                status = exitTileIsLandedOn();
            }
        }
        return status;
    }

    @Override
    public boolean isValidMove(Position destPoint) {
        boolean valid = false;
        if (isOnLevelPlane(destPoint)) {
            if (!currentLevel.getTileInLevel(destPoint).equals("#")
                    && !currentLevel.getTileInLevel(destPoint).equals("P")
                    && !currentLevel.getTileInLevel(destPoint).equals(".")
                    && is2CardinalTilesAway(destPoint)) {
                        valid = true;
            }
        }
        return valid;
    }

    /**
     * Checks whether the destination positino is within the bounds of the level plane
     * @param destPoint
     * @return true if the requested position is within the bounds of the levelPlane, false otherwise
     */
    public boolean isOnLevelPlane(Position destPoint) {
        int levelNumRows = this.currentLevel.getLevelNumOfRows();
        int levelNumCols = this.currentLevel.getLevelNumOfCols();
        return destPoint.getRow() >= 0 && destPoint.getRow() < levelNumRows
                && destPoint.getCol() >= 0 && destPoint.getCol() < levelNumCols;
    }

    public boolean is2CardinalTilesAway(Position destPoint) {
        boolean withinReach = false;
        ArrayList<Position> cardinalTiles = new ArrayList<Position>();
        ArrayList<Position> adjTiles = currentLevel.getAllAdjacentTiles(this.player.getCharacterPosition(), null);

        for (Position adjacent : adjTiles) {
            cardinalTiles.add(adjacent);
            for (Position adjacentOfAdjacent: currentLevel.getAllAdjacentTiles(adjacent, null)) {
                if (!cardinalTiles.contains(adjacentOfAdjacent)) {
                    cardinalTiles.add(adjacentOfAdjacent);
                }
            }
        }
        if (cardinalTiles.contains(destPoint)) {
            withinReach = true;
        }
        return withinReach;
    }

    private GameStatus keyTileIsLandedOn() {
        return GameStatus.KEY_FOUND;
    }

    private GameStatus exitTileIsLandedOn() {
      if (isExitUnlocked()) {
        if (isLastLevel()) {
            return GameStatus.GAME_WON;
        }
        //if it is the last player exiting through the exit tile
        if (currentLevel.getActivePlayers().size() == 1) {
            return GameStatus.LEVEL_WON;
        }
        return GameStatus.PLAYER_EXITED;
      }
      return GameStatus.VALID;
    }

    /**
     * Returns whether or not the exit tile is unlocked
     * @return true if the exit tile is unlocked, false if it is locked
     */
    private boolean isExitUnlocked() {
        // returns true if the exit tile in the level has been unlocked, false otherwise
        return currentLevel.getTileInLevel(currentLevel.getExitPositionInLevel()).equals("O");
    }

    /**
     * Not elaborated on in Milestone 5 because we are only dealing with one level so this will always be true
     * @return true if the game is on the last level, false if not
     */
    private boolean isLastLevel() {
        return true;
    }

    /**
     * Returns the appropriate GameStatus when a player encounters an IAdversary based on if they get expelled
     * and if they are the last player to get expelled.
     * @return GameStatus.GAME_LOST or GameStatus.PLAYER_SELF_ELIMINATES
     */
    @Override
    public GameStatus encountersOppositeCharacter() {
        // checks if the player self-eliminating is the last active player in the level, if so the game is lost
        if (currentLevel.getActivePlayers().size() == 1) {
            return GameStatus.GAME_LOST;
        }
        return GameStatus.PLAYER_SELF_ELIMINATES;
    }

}
