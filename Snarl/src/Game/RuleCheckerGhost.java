package Game;

public class RuleCheckerGhost implements IRuleChecker {
    Level currentLevel;
    IAdversary adversary;
    
    public RuleCheckerGhost(Level currentLevel, IAdversary adversary) {
        this.currentLevel = currentLevel;
        this.adversary = adversary;
    }
    @Override
    public GameStatus runRuleChecker(Position destination) {
        return null;
    }

    @Override
    public GameStatus encountersOppositeCharacter() {
        return null;
    }

    //TODO ghosts cannot skip moves aka stay in current position, need to change last part of if statement
    @Override
    public boolean isValidMove(Position destPoint) {
        boolean valid = false;
        if (isOnLevelPlane(destPoint)) {
            if (!isCharactersCurrentPosition(destPoint)
                    && isTileTraversable(destPoint) && isNCardinalTilesAway(destPoint, 1)) {
                valid = true;
            }
        }
        return valid;
    }
    
    @Override
    public boolean isTileTraversable(Position tile) {
        return !this.currentLevel.getTileInLevel(tile).equals(" ");

    }

    @Override
    public boolean isCharactersCurrentPosition(Position destPoint) {
        return destPoint.toString().equals(this.adversary.getCharacterPosition().toString());
    }

    @Override
    public boolean isNCardinalTilesAway(Position destPoint, int maxTilesAway) {
      return true;
    }

    @Override
    public boolean isOnLevelPlane(Position destPoint) {
        int levelNumRows = this.currentLevel.getLevelNumOfRows();
        int levelNumCols = this.currentLevel.getLevelNumOfCols();
        return destPoint.getRow() >= 0 && destPoint.getRow() < levelNumRows
                && destPoint.getCol() >= 0 && destPoint.getCol() < levelNumCols;
    }
}
