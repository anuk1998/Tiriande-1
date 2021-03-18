package Game;

import java.util.ArrayList;

public class RuleCheckerPlayer implements IRuleChecker {
    Level currentLevel;
    Player player;

    public RuleCheckerPlayer(Level currentLevel, Player player) {
        this.currentLevel = currentLevel;
        this.player = player;
    }

    /**
     * Returns a GameStatus based on the player's move to a destination Position
     * Returns GameStatus.INVALID if move is not possible, GameStatus.VALID if possible, etc.
     *
     * @param destination goal Position requested by player
     * @return GameStatus signifying what kind of move the player requested
     */
    @Override
    public GameStatus runRuleChecker(Position destination) {
        GameStatus status = GameStatus.INVALID;
        if (isValidMove(destination)) {
            status = GameStatus.VALID;
            if (isOccupiedByAdversary(destination)) {
                status = encountersOppositeCharacter();
            } else if (currentLevel.getKeyPositionInLevel().equals(destination)) {
                status = keyTileIsLandedOn();
            } else if (currentLevel.getExitPositionInLevel().equals(destination)) {
                status = exitTileIsLandedOn();
            }
        }
        return status;
        }

    /**
     * Determines whether a given destination Position is a valid move by a player.
     *
     * @param destPoint goal Position requested by player
     * @return true if the destination position is valid, false if not
     */
    @Override
    public boolean isValidMove(Position destPoint) {
        boolean valid = false;
        if (isOnLevelPlane(destPoint)) {
            if (isTileTraversable(destPoint) && is2CardinalTilesAway(destPoint)) {
                valid = true;
            }
        }
        return valid;
    }

    /**
     * Checks whether the destination position is within the bounds of the level plane.
     *
     * @param destPoint goal Position requested by player
     * @return true if the requested position is within the bounds of the levelPlane, false otherwise
     */
    public boolean isOnLevelPlane(Position destPoint) {
        int levelNumRows = this.currentLevel.getLevelNumOfRows();
        int levelNumCols = this.currentLevel.getLevelNumOfCols();
        return destPoint.getRow() >= 0 && destPoint.getRow() < levelNumRows
                && destPoint.getCol() >= 0 && destPoint.getCol() < levelNumCols;
    }

    /**
     * Returns whether a destination position is 2 cardinal units away from the player's current position.
     *
     * @param destPoint goal Position requested by player
     * @return true if destPoint is 2 units away, false if not
     */
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

    /**
     * Returns the appropriate GameStatus for when a key tile is landed on.
     *
     * @return a GameStatus that signifies that the level key has been found
     */
    public GameStatus keyTileIsLandedOn() {
        return GameStatus.KEY_FOUND;
    }

    /**
     * Returns the appropriate GameStatus for when a exit tile is landed on depending on whether
     * it is locked or unlocked.
     *
     * @return GameStatus signifying what kind of outcome the requested move would result in
     */
    public GameStatus exitTileIsLandedOn() {
      if (isExitUnlocked()) {
        if (isLastLevel() && currentLevel.getActivePlayers().size() == 1) {
            return GameStatus.GAME_WON;
        }
        //if it is the last player exiting through the exit tile
        else if (currentLevel.getActivePlayers().size() == 1) {
            return GameStatus.LEVEL_WON;
        }
        return GameStatus.PLAYER_EXITED;
      }
      return GameStatus.VALID;
    }

    /**
     * Returns whether or not the exit tile is unlocked.
     *
     * @return true if the exit tile is unlocked, false if it is locked
     */
    public boolean isExitUnlocked() {
        // returns true if the exit tile in the level has been unlocked, false otherwise
        return currentLevel.getTileInLevel(currentLevel.getExitPositionInLevel()).equals("O");
    }

    /**
     * Not elaborated on in Milestone 5 because we are only dealing with one level so this will always be true.
     * @return true if the game is on the last level, false if not
     */
    public boolean isLastLevel() {
        return true;
    }

    /**
     * Checks if the requested destination is occupied by an Adversary.
     *
     * @param destination the goal destination
     * @return a boolean representing if that tile has an adversary on it
     */
    private boolean isOccupiedByAdversary(Position destination) {
        String goalTile = this.currentLevel.getTileInLevel(destination);
        return goalTile.equals("G") || goalTile.equals("Z");
    }

    /**
     * Returns the appropriate GameStatus when a player encounters an IAdversary based on if they get expelled
     * and if they are the last player to get expelled.
     *
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
