package Game;

public enum Avatars {
  // "@", "¤", "$", "~"
  PLAYER_1 ("@"),
  PLAYER_2 ("¤"),
  PLAYER_3 ("$"),
  PLAYER_4 ("~"),
  GHOST ("G"),
  ZOMBIE ("Z");

  private final String name;

  Avatars(String s) {
    name = s;
  }

  @Override
  public String toString(){
    return this.name;
  }
}
