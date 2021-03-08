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

    private boolean isOnLevelPlane(Position destPoint) {
         int levelNumRows = this.currentLevel.getLevelNumOfRows();
         int levelNumCols = this.currentLevel.getLevelNumOfCols();

         if (destPoint.getRow() < 0 || destPoint.getRow() >= levelNumRows
                 || destPoint.getCol() < 0 || destPoint.getCol() >= levelNumCols) {
             return false;
         }
         return true;
    }

    public boolean is2CardinalTilesAway(Position destPoint) {
        boolean withinReach = false;
        ArrayList<Position> cardinalTiles = new ArrayList<Position>();
        ArrayList<Position> adjTiles = currentLevel.getAllAdjacentTiles(this.player.getPlayerPosition(), null);

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
        return GameStatus.LEVEL_WON;
      }
      return GameStatus.VALID;
    }

    private boolean isExitUnlocked() {
        if (currentLevel.getExitPositionInLevel().equals("O")) {
            return true;
        }
        return false;
    }

    private boolean isLastLevel() {
        return true;
    }

    @Override
    public GameStatus encountersOppositeCharacter() {
        // checks if the player self-eliminating is the last active player in the level, if so the game is lost
        if (currentLevel.getActivePlayers().size() == 1) {
            return GameStatus.GAME_LOST;
        }
        return GameStatus.PLAYER_SELF_ELIMINATES;
    }

}
