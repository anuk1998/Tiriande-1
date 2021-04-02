package Game;

public class RuleCheckerZombie implements IRuleChecker{
  Level currentLevel;
  IAdversary adversary;
  GameManager gm;

  public RuleCheckerZombie(GameManager gm, Level currentLevel, IAdversary adversary) {
      this.currentLevel = currentLevel;
      this.adversary = adversary;
      this.gm = gm;
  }

  @Override
  public GameStatus runRuleChecker(Position destination) {
    GameStatus status = GameStatus.INVALID;
    if (isValidMove(destination)) {
      status = GameStatus.VALID;
      if (landedOnPlayer(destination)) {
        status = encountersOppositeCharacter();
      }
    }
    return status;
  }

  private boolean landedOnPlayer(Position destination) {
    String symbol = this.currentLevel.getTileInLevel(destination);
    return symbol.equals("@") || symbol.equals("¤") || symbol.equals("$") || symbol.equals("~");
  }

  @Override
  public GameStatus encountersOppositeCharacter() {
    // checks if the player being expelled is the last active player in the level
    if (currentLevel.getActivePlayers().size() == 1) {
      // and everyone else is expelled
      if (gm.getExpelledPlayers().size() == gm.getAllPlayers().size() - 1) {
        return GameStatus.GAME_LOST;
      }
      // checks that at least one player has passed through the level exit
      else if (gm.getExitedPlayers().size() > 0) {
        //it is the last level
        if (isLastLevel()) {
          return GameStatus.GAME_WON;
        }
        return GameStatus.LEVEL_WON;
      }
    }
    return GameStatus.PLAYER_EXPELLED;
  }

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
  public boolean isOnLevelPlane(Position destPoint) {
    int levelNumRows = this.currentLevel.getLevelNumOfRows();
    int levelNumCols = this.currentLevel.getLevelNumOfCols();
    return destPoint.getRow() >= 0 && destPoint.getRow() < levelNumRows
            && destPoint.getCol() >= 0 && destPoint.getCol() < levelNumCols;
  }

  @Override
  public boolean isLastLevel() {
    return gm.getAllLevels().indexOf(this.currentLevel) == gm.getAllLevels().size() - 1;
  }

  @Override
  public boolean isTileTraversable(Position tile) {
    return !this.currentLevel.getTileInLevel(tile).equals(" ") &&
            !this.currentLevel.getTileInLevel(tile).equals("G") &&
            !this.currentLevel.getTileInLevel(tile).equals("Z") &&
            !this.currentLevel.getTileInLevel(tile).equals("|") &&
            !this.currentLevel.getTileInLevel(tile).equals("x") &&
            !this.currentLevel.getTileInLevel(tile).equals("■");
  }

  @Override
  public boolean isCharactersCurrentPosition(Position destPoint) {
    return destPoint.toString().equals(this.adversary.getCharacterPosition().toString());
  }

  @Override
  public boolean isNCardinalTilesAway(Position destPoint, int maxTilesAway) {
    return true;
  }

}
