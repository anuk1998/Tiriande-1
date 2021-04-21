package Game;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

class LevelTesting {
  static Level level1 = new Level();
  static Room room1 = new Room(new Position(0, 0), 8, 10);
  static Room room2 = new Room(new Position(15, 15), 10, 10);
  static Room room3 = new Room(new Position(4,17),4,6);
  static Room room4 = new Room(new Position(2, 25), 7, 11);
  static Room room5 = new Room(new Position(23, 2), 14, 7);
  static Room room6 = new Room(new Position(30,27), 7,7);
  static Player p1 = new Player("1");
  static Player p2 = new Player("2");
  static Player p3 = new Player("3");
  static Player p4 = new Player("4");
  static IAdversary a1 = new Ghost("Scary Ghost");
  static IAdversary a2 = new Zombie("Weird Zombie");


  public static void main(String[] args) throws Exception {
    createInitialGameState();
    testGetIsKeyFoundBefore();

    testGetDoorPositions();
    testGetKeyPositionInLevel();
    testGetExitPositionInLevel();
    System.out.print(level1.renderLevel());
    testGetTileInRoom();
    createIntermediateGameState();
    createModifiedAfterObjectGameState();

    // will be true now that the key has been found
    testGetIsKeyFoundAfter();
    testGetExitTileInLevelAfterUnlocked();
    invalidRoom();
    invalidDoorPlacement();
    testGetRowPosition();
    testGetColPosition();
    testGetDoorPositions();

    testRoomGetNumOfRows();
    testRoomGetNumOfCols();
    testGetRoomOriginInLevel();
  }

  private static void testGetIsKeyFoundBefore() {
    assertEquals("*", level1.getTileInLevel(level1.getKeyPositionInLevel()));
  }

  private static void testGetExitTileInLevelAfterUnlocked() {
    level1.openExitTile();
    assertEquals("O", level1.getTileInLevel(level1.getExitPositionInLevel()));
  }

  private static void createModifiedAfterObjectGameState() {
    System.out.println("MODIFIED AFTER OBJECT INTERACTION GAMESTATE:\n\n\n");
    //expels player 2 from board
    level1.moveCharacter(a2, p4.getCharacterPosition());
    level1.expelPlayer(p4);
    // this moves player 2 to the key tile
    level1.moveCharacter(p2, new Position(5, 27));
    System.out.println(level1.renderLevel());
    System.out.println("");
    // this moves player 2 off of the key tile, and the rendered level shows that the
    // key is no longer on the board (because it's been found & collected), and the level exit
    // once a filled-in circle, is now a hollow circle, symbolizing that it is unlocked
    level1.moveCharacter(p2, new Position(5, 28));
    System.out.println(level1.renderLevel());
    System.out.println("");
  }

  private static void createIntermediateGameState() {
    System.out.println();
    System.out.println("INTERMEDIATE GAMESTATE:");
    System.out.println();
    level1.moveCharacter(p1, new Position(2,13));
    level1.moveCharacter(p3, new Position(15,20));
    level1.moveCharacter(p4, new Position(30,4));
    //move adversaries around
    level1.moveCharacter(a1, new Position(21,17));
    //move adversary 2 right next to a player
    level1.moveCharacter(a2, new Position(30,3));

    //moves player 2 right next to key in level
    level1.moveCharacter(p2, new Position(level1.getKeyPositionInLevel().getRow(), level1.getKeyPositionInLevel().getCol() - 1));
    System.out.print(level1.renderLevel());
  }

  public static void createInitialGameState() {
    System.out.println();
    System.out.println("INITIAL GAMESTATE:");
    System.out.println();
    //Room 1
    room1.addDoor(new Position(2, 9));
    room1.addDoor(new Position(7,6));

    //Room 2
    room2.addDoor(new Position(0, 0));

    room2.addDoor(new Position(6,0));
    room2.addDoor(new Position(8,9));

    //Room 3
    room3.addDoor(new Position(0,3));
    room3.addDoor(new Position(3, 2));

    //Room 4
    room4.addDoor(new Position(5, 0));
    room4.addDoor(new Position(2, 10));
    room4.addDoor(new Position(6, 4));

    //Room 5
    room5.addDoor(new Position(3,0));
    room5.addDoor(new Position(0,4));
    room5.addDoor(new Position(5,6));

    //Room 6
    room6.addDoor(new Position(0,5));
    room6.addDoor(new Position(3,0));
    room6.addDoor(new Position(0,2));

    //Add rooms to level
    level1.addRoom(room1);
    level1.addRoom(room2);
    level1.addRoom(room3);
    level1.addRoom(room4);
    level1.addRoom(room5);
    level1.addRoom(room6);


  //adding hallways
    ArrayList<Position> h1Waypoints = new ArrayList<>();
    h1Waypoints.add(new Position(2, 20));
    Hallway h1 = new Hallway(new Position(2, 9), new Position(4, 20),
            h1Waypoints);

    ArrayList<Position> h2Waypoints = new ArrayList<>();
    h2Waypoints.add(new Position(11, 6));
    h2Waypoints.add(new Position(11, 15));
    Hallway h2 = new Hallway(new Position(7, 6), new Position(15, 15), h2Waypoints);

    ArrayList<Position> h3Waypoints = new ArrayList<>();
    h3Waypoints.add(new Position(21,6));
    Hallway h3 = new Hallway(new Position(21, 15), new Position(23, 6), h3Waypoints);

    ArrayList<Position> h4Waypoints = new ArrayList<>();
    h4Waypoints.add(new Position(4, 37));
    h4Waypoints.add(new Position(38, 37));
    h4Waypoints.add(new Position(38, 0));
    h4Waypoints.add(new Position(26, 0));
    Hallway h4 = new Hallway(new Position(4, 35), new Position(26, 2), h4Waypoints);

    ArrayList<Position> h5Waypoints = new ArrayList<>();
    h5Waypoints.add(new Position(25,29));
    h5Waypoints.add(new Position(25, 32));
    Hallway h5 = new Hallway(new Position(8, 29), new Position(30, 32), h5Waypoints);

    ArrayList<Position> h6Waypoints = new ArrayList<>();
    h6Waypoints.add(new Position(7,24));
    h6Waypoints.add(new Position(10,24));
    h6Waypoints.add(new Position(10,19));
    Hallway h6 = new Hallway(new Position(7, 25), new Position(7,19), h6Waypoints);

    ArrayList<Position> h7Waypoints = new ArrayList<>();
    h7Waypoints.add(new Position(28, 11));
    h7Waypoints.add(new Position(33, 11));
    Hallway h7 = new Hallway(new Position(28, 8), new Position(33, 27), h7Waypoints);

    ArrayList<Position> h8Waypoints = new ArrayList<>();
    h8Waypoints.add(new Position(23, 27));
    h8Waypoints.add(new Position(27, 27));
    h8Waypoints.add(new Position(27, 29));
    Hallway h8 = new Hallway(new Position(23, 24), new Position(30, 29), h8Waypoints);

    level1.addHallway(h1);
    level1.addHallway(h2);
    level1.addHallway(h3);
    level1.addHallway(h4);
    level1.addHallway(h5);
    level1.addHallway(h6);
    level1.addHallway(h7);
    level1.addHallway(h8);

    p1.setAvatar("P");
    p2.setAvatar("P");
    p3.setAvatar("P");
    p4.setAvatar("P");

    //add players to level
    level1.addCharacter(p1, new Position(2, 4));
    level1.addCharacter(p2, new Position(6, 1));
    level1.addCharacter(p3, new Position(5, 5));
    level1.addCharacter(p4, new Position(6, 2));

    //add adversaries to the level
    level1.addCharacter(a1, new Position(32,29));
    level1.addCharacter(a2, new Position(34,32));

    level1.addObject(new Position(17, 18), "●");
    level1.addObject(new Position(5, 27), "*");

  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public static void invalidRoom() {
    Room r1 = new Room(new Position(100, 100), 10, 10);
  }

  @Test(expected = IllegalArgumentException.class)
  public static void  invalidDoorPlacement() throws Exception {
    try {
      Position pos = new Position(100,100);
      room4.addDoor(pos);
    }
    catch (IllegalArgumentException e) {
      new IllegalArgumentException("Given coordinate for a door is not a room coordinate on the room's boundary.");
    }
  }

  @Test
  public static void testGetRowPosition() {
    Position testPosition = new Position(5,4);
    assertEquals(5, testPosition.getRow());
  }

  @Test
  public static void testGetColPosition() {
    Position testPosition = new Position(11,6);
    assertEquals(6, testPosition.getCol());
  }

  @Test
  public static void testGetIsKeyFoundAfter() {
    assertEquals(".", level1.getTileInLevel(level1.getKeyPositionInLevel()));
  }

  @Test
  public static void testGetDoorPositions() {
    ArrayList<Position> doorPositions = new ArrayList<Position>();
    doorPositions.add(new Position(2,9));
    doorPositions.add(new Position(7,6));
    assertEquals(doorPositions, room1.getDoorPositions());

    doorPositions.clear();
    doorPositions.add(new Position(5, 0));
    doorPositions.add(new Position(2, 10));
    doorPositions.add(new Position(6, 4));
    assertEquals(doorPositions, room4.getDoorPositions());
  }

  @Test
  public static void testGetTileInRoom() {
    assertEquals(".", level1.getTileInLevel(new Position(5, 8)));
    assertEquals("|", level1.getTileInLevel(new Position(2, 9)));
    assertEquals("■", level1.getTileInLevel(new Position(7, 0)));
  }

  @Test
  public static void testGetExitPositionInLevel() {
    assertEquals(new Position(17, 18), level1.getExitPositionInLevel());
  }

  @Test
  public static void testGetKeyPositionInLevel() {
    assertEquals(new Position(5, 27), level1.getKeyPositionInLevel());
  }


  @Test
  public static void testRoomGetNumOfCols() {
    assertEquals(10, room1.getNumOfCols());
    assertEquals(6, room3.getNumOfCols());
    assertEquals(7, room5.getNumOfCols());
  }


  @Test
  public static void testRoomGetNumOfRows() {
    assertEquals(8, room1.getNumOfRows());
    assertEquals(4, room3.getNumOfRows());
    assertEquals(14, room5.getNumOfRows());
  }

  @Test
  public static void testGetRoomOriginInLevel() {
    assertEquals(new Position(0,0), room1.getRoomOriginInLevel());
    assertEquals(new Position(15,15), room2.getRoomOriginInLevel());
    assertEquals(new Position(4,17), room3.getRoomOriginInLevel());
  }

}
