package Game;

import java.util.HashSet;

public class RuleCheckerPlayer implements IRuleChecker {
    Level currentLevel;
    Player player;

    String VOID = TileType.VOID.toString();
    String WALL = TileType.WALL.toString();
    String PLAYER_1 = Avatars.PLAYER_1.toString();
    String PLAYER_2 = Avatars.PLAYER_2.toString();
    String PLAYER_3 = Avatars.PLAYER_3.toString();
    String PLAYER_4 = Avatars.PLAYER_4.toString();
    String GHOST = Avatars.GHOST.toString();
    String ZOMBIE = Avatars.ZOMBIE.toString();
    String UNLOCKED_EXIT = TileType.UNLOCKED_EXIT.toString();

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
            } else if (currentLevel.getKeyPositionInLevel().equals(destination)
                    && !currentLevel.getKeyFound()) {
                status = GameStatus.KEY_FOUND;
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
            if ((isTileTraversable(destPoint) && isNCardinalTilesAway(destPoint, 2))
                    || (isCharactersCurrentPosition(destPoint))) {
                valid = true;
            }
        }
        return valid;
    }

    /**
     * Checks if the requested destination is the current player's current destination.
     *
     * @param destPoint the player's requested move
     * @return a boolean indicating if it's the player's current position
     */
    @Override
    public boolean isCharactersCurrentPosition(Position destPoint) {
        return destPoint.toString().equals(this.player.getCharacterPosition().toString());
    }

    /**
     * Determines if the given position on the level plane is a traversable tile for a player.
     *
     * @param tile the given position
     * @return boolean indicating if the tile is traversable or not for a player
     */
    @Override
    public boolean isTileTraversable(Position tile) {
        return !this.currentLevel.getTileInLevel(tile).equals(VOID) && !this.currentLevel.getTileInLevel(tile).equals(WALL) &&
                !isOccupiedByAnotherPlayer(tile);
    }

    /**
     * Determines if the given tile is occupied by another player, a.k.a. another avatar.
     *
     * @param tile the position in question/goal destination
     * @return boolean representing whether or not the position is already occupied by another player
     */
    private boolean isOccupiedByAnotherPlayer(Position tile) {
        String symbol = this.currentLevel.getTileInLevel(tile);
        return symbol.equals(PLAYER_1) ||
                symbol.equals(PLAYER_2) ||
                symbol.equals(PLAYER_3) ||
                symbol.equals(PLAYER_4);
    }

    /**
     * Checks if the requested destination is occupied by an Adversary.
     *
     * @param destination the goal destination
     * @return a boolean representing if that tile has an adversary on it
     */
    private boolean isOccupiedByAdversary(Position destination) {
        String goalTile = this.currentLevel.getTileInLevel(destination);
        return goalTile.equals(GHOST) || goalTile.equals(ZOMBIE);
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
    @Override
    public boolean isNCardinalTilesAway(Position destPoint, int maxTilesAway) {
        boolean withinReach = false;
        HashSet<Position> cardinalTiles = new HashSet<>(
                currentLevel.getAllAdjacentTiles(this.player.getCharacterPosition()));

        while (maxTilesAway > 1) {
            HashSet<Position> tempCardinalTiles = new HashSet<>(cardinalTiles);
            for (Position adjacent : tempCardinalTiles) {
                cardinalTiles.addAll(currentLevel.getAllAdjacentTiles(adjacent));
            }
            maxTilesAway--;
        }

        for (Position pos : cardinalTiles) {
            if (pos.toString().equals(destPoint.toString())) withinReach = true;
        }

        return withinReach;
    }

    /**
     * Returns the appropriate GameStatus for when a exit tile is landed on depending on whether
     * it is locked or unlocked.
     *
     * @return GameStatus signifying what kind of outcome the requested move would result in
     */
    public GameStatus exitTileIsLandedOn() {
      if (isExitUnlocked()) {
        // if it is the last player exiting through the exit tile
        if (currentLevel.getActivePlayers().size() == 1) {
            if (this.currentLevel.getIsLastLevelOfGame()) {
                return GameStatus.GAME_WON;
            }
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
        Position exitPos = currentLevel.getExitPositionInLevel();
        return currentLevel.getTileInLevel(exitPos).equals(UNLOCKED_EXIT);
    }


    /**
     * Returns the appropriate GameStatus when a player encounters an IAdversary based on if they get expelled
     * and if they are the last player to get expelled.
     */
    @Override
    public GameStatus encountersOppositeCharacter() {
        // checks if the player self-eliminating is the last active player in the level
        if (this.currentLevel.getActivePlayers().size() == 1) {
            //and everyone else is expelled
            if (this.currentLevel.getExpelledPlayers().size() == this.currentLevel.getNumOfPlayers() - 1) {
                return GameStatus.GAME_LOST;
            }
            //at least one player has passed through the level exit
            else if (this.currentLevel.getExitedPlayers().size() > 0) {
                //it is the last level
                if (this.currentLevel.getIsLastLevelOfGame()) {
                    return GameStatus.GAME_WON;
                }
                this.player.setIsExpelled(true);
                return GameStatus.LEVEL_WON;
            }
        }
        return GameStatus.PLAYER_SELF_ELIMINATES;
    }

}
