package Game;

public enum TileType {
  VOID (" "),
  ROOM ("."),
  WALL("■"),
  KEY("*"),
  LOCKED_EXIT ("●"),
  UNLOCKED_EXIT("O"),
  DOOR("|"),
  HALLWAY("x");

  private final String name;

  private TileType(String s) {
    name = s;
  }

  @Override
  public String toString(){
    return this.name;
  }
}
