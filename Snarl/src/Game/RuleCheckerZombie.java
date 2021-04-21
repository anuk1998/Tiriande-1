package Game;

import java.util.HashSet;

public class RuleCheckerZombie implements IRuleChecker{
  Level currentLevel;
  IAdversary adversary;

  // Tile/Actor representation constants
  String PLAYER_1 = Avatars.PLAYER_1.toString();
  String PLAYER_2 = Avatars.PLAYER_2.toString();
  String PLAYER_3 = Avatars.PLAYER_3.toString();
  String PLAYER_4 = Avatars.PLAYER_4.toString();
  String GHOST = Avatars.GHOST.toString();
  String ZOMBIE = Avatars.ZOMBIE.toString();
  String VOID = TileType.VOID.toString();
  String DOOR = TileType.DOOR.toString();
  String HALLWAY = TileType.HALLWAY.toString();
  String WALL = TileType.WALL.toString();

  public RuleCheckerZombie(Level currentLevel, IAdversary adversary) {
      this.currentLevel = currentLevel;
      this.adversary = adversary;
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

  public boolean landedOnPlayer(Position destination) {
    String symbol = this.currentLevel.getTileInLevel(destination);
    return symbol.equals(PLAYER_1) || symbol.equals(PLAYER_2) ||
            symbol.equals(PLAYER_3) || symbol.equals(PLAYER_4);
  }

  @Override
  public GameStatus encountersOppositeCharacter() {
    // checks if the player being expelled is the last active player in the level
    if (currentLevel.getActivePlayers().size() == 1) {
      // and everyone else is expelled
      if (this.currentLevel.getExpelledPlayers().size() == this.currentLevel.getNumOfPlayers() - 1) {
        return GameStatus.GAME_LOST;
      }
      // checks that at least one player has passed through the level exit
      else if (this.currentLevel.getExitedPlayers().size() > 0) {
        //it is the last level
        if (this.currentLevel.getIsLastLevelOfGame()) {
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
      if (isNotOnDoor(destPoint)
              && isTileTraversable(destPoint)
              && isNCardinalTilesAway(destPoint, 1)) {
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

  private boolean isNotOnDoor(Position destPoint) {
    boolean isNotOnDoor = true;

    for (String doorPos : this.currentLevel.getListOfDoorPositions()) {
      if (destPoint.toString().equals(doorPos)) {
        isNotOnDoor = false;
      }
    }

    return isNotOnDoor;
  }

  @Override
  public boolean isTileTraversable(Position tile) {
    return !this.currentLevel.getTileInLevel(tile).equals(VOID) &&
            !this.currentLevel.getTileInLevel(tile).equals(GHOST) &&
            !this.currentLevel.getTileInLevel(tile).equals(ZOMBIE) &&
            !this.currentLevel.getTileInLevel(tile).equals(DOOR) &&
            !this.currentLevel.getTileInLevel(tile).equals(HALLWAY) &&
            !this.currentLevel.getTileInLevel(tile).equals(WALL);
  }

  @Override
  public boolean isCharactersCurrentPosition(Position destPoint) {
    return destPoint.toString().equals(this.adversary.getCharacterPosition().toString());
  }

  @Override
  public boolean isNCardinalTilesAway(Position destPoint, int maxTilesAway) {
    boolean withinReach = false;
    HashSet<Position> cardinalTiles = new HashSet<>(
            currentLevel.getAllAdjacentTiles(this.adversary.getCharacterPosition()));

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

}
