package GameTest;

import Game.Adversary;
import Game.Hallway;
import Game.Level;
import Game.Player;
import Game.Position;
import Game.Room;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;

class LevelTesting {
  static Level level1 = new Level();
  static Room room1 = new Room(new Position(0, 0), 8, 10);
  static Room room2 = new Room(new Position(15, 15), 10, 10);
  static Room room3 = new Room(new Position(4,17),4,6);
  static Room room4 = new Room(new Position(2, 25), 7, 11);
  static Room room5 = new Room(new Position(23, 2), 14, 7);
  static Room room6 = new Room(new Position(30,27), 7,7);
  static Hallway h1 = new Hallway(new Position(2, 9), new Position(4, 20));
  static Hallway h2 = new Hallway(new Position(7, 6), new Position(15, 15));
  static Hallway h3 = new Hallway(new Position(21, 15), new Position(23, 6));
  static Hallway h4 = new Hallway(new Position(4, 35), new Position(26, 2));
  static Hallway h5 = new Hallway(new Position(8, 29), new Position(30, 32));
  static Hallway h6 = new Hallway(new Position(7, 25), new Position(7,19));
  static Hallway h7 = new Hallway(new Position(28, 8), new Position(33, 27));
  static Hallway h8 = new Hallway(new Position(23, 24), new Position(30, 29));
  static Player p1 = new Player("1");
  static Player p2 = new Player("2");
  static Player p3 = new Player("3");
  static Player p4 = new Player("4");
  static Adversary a1 = new Adversary(1);
  static Adversary a2 = new Adversary(2);


  public static void main(String[] args) throws Exception {
    createInitialGameState();
    testGetIsKeyFoundBefore(); // will be false, key hasn't been found yet
    testGetKeyPosition();
    testGetDoorPositions();
    testGetExitPosition();
    testGetKeyPositionInLevel();
    testGetExitPositionInLevel();

    createIntermediateGameState();
    createModifiedAfterObjectGameState();

    // will be true now that the key has been found
    testGetIsKeyFound();
    testGetExitTileInLevelAfterUnlocked();

    testGetStartPositionHallway();
    testGetEndPositionHallway();
    testGetHallwayWaypoints();
    invalidRoom();
    invalidDoorPlacement();
    testGetRowPosition();
    testGetColPosition();
    testGetDoorPositions();
    testGetTileInRoom();
    testgetAllRoomsInLevel();
    testgetLevelHeight();
    testgetLevelWidth();
    testRoomGetNumOfRows();
    testRoomGetNumOfCols();
    testGetRoomOriginInLevel();
  }

  private static void testGetIsKeyFoundBefore() {
    assertEquals(false, level1.getIsKeyFound());
  }

  private static void testGetExitTileInLevelAfterUnlocked() {
    assertEquals("O", level1.getTileInLevel(level1.getExitPositionInLevel()));
  }

  private static void createModifiedAfterObjectGameState() {
    System.out.println("MODIFIED AFTER OBJECT INTERACTION GAMESTATE:\n\n\n");
    //expels player 2 from board
    level1.moveAdversary(a2, p4.getPlayerPosition());
    level1.expelPlayer(p4);
    // this moves player 2 to the key tile
    level1.movePlayer(p2, new Position(5, 27));
    System.out.println(level1.renderLevel());
    System.out.println("");
    // this moves player 2 off of the key tile, and the rendered level shows that the
    // key is no longer on the board (because it's been found & collected), and the level exit
    // once a filled-in circle, is now a hollow circle, symbolizing that it is unlocked
    level1.movePlayer(p2, new Position(5, 28));
    System.out.println(level1.renderLevel());
    System.out.println("");
  }

  private static void createIntermediateGameState() {
    System.out.println();
    System.out.println("INTERMEDIATE GAMESTATE:");
    System.out.println();
    level1.movePlayer(p1, new Position(2,13));
    level1.movePlayer(p3, new Position(15,20));
    level1.movePlayer(p4, new Position(30,4));
    //move adversaries around
    level1.moveAdversary(a1, new Position(21,17));
    //move adversary 2 right next to a player
    level1.moveAdversary(a2, new Position(30,3));

    //moves player 2 right next to key in level
    level1.movePlayer(p2, new Position(level1.getKeyPositionInLevel().getRow(), level1.getKeyPositionInLevel().getCol() - 1));
    System.out.print(level1.renderLevel());
  }

  public static void createInitialGameState() {
    System.out.println();
    System.out.println("INITIAL GAMESTATE:");
    System.out.println();
    //Room 1
    room1.addDoor(new Position(2, 9));
    room1.addDoor(new Position(7,6));

    room4.addKey(new Position(3, 2));

    //Room 2
    room2.addDoor(new Position(0, 0));
    room2.addExit(new Position(2, 3));
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

    h1.addAWaypoint(new Position(2, 20));
    h1.connectHallwayWaypoints();

    h2.addAWaypoint(new Position(11, 6));
    h2.addAWaypoint(new Position(11, 15));
    h2.connectHallwayWaypoints();

    h3.addAWaypoint(new Position(21,6));
    h3.connectHallwayWaypoints();

    h4.addAWaypoint(new Position(4, 37));
    h4.addAWaypoint(new Position(38, 37));
    h4.addAWaypoint(new Position(38, 0));
    h4.addAWaypoint(new Position(26, 0));
    h4.connectHallwayWaypoints();

    h5.addAWaypoint(new Position(25,29));
    h5.addAWaypoint(new Position(25, 32));
    h5.connectHallwayWaypoints();

    h6.addAWaypoint(new Position(7,24));
    h6.addAWaypoint(new Position(10,24));
    h6.addAWaypoint(new Position(10,19));
    h6.connectHallwayWaypoints();

    h7.addAWaypoint(new Position(28, 11));
    h7.addAWaypoint(new Position(33, 11));
    h7.connectHallwayWaypoints();

    h8.addAWaypoint(new Position(23, 27));
    h8.addAWaypoint(new Position(27, 27));
    h8.addAWaypoint(new Position(27, 29));
    h8.connectHallwayWaypoints();

    level1.addHallway(h1);
    level1.addHallway(h2);
    level1.addHallway(h3);
    level1.addHallway(h4);
    level1.addHallway(h5);
    level1.addHallway(h6);
    level1.addHallway(h7);
    level1.addHallway(h8);

    //add players to level
    level1.addPlayer(p1, new Position(2, 4));
    level1.addPlayer(p2, new Position(6, 1));
    level1.addPlayer(p3, new Position(5, 5));
    level1.addPlayer(p4, new Position(7, 2));

    //add adversaries to the level
    level1.addAdversary(a1, new Position(32,29));
    level1.addAdversary(a2, new Position(34,32));
    System.out.print(level1.renderLevel());
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
  public static void testGetKeyPosition() {
    assertEquals(new Position(3,2), room4.getKeyPosition());
  }

  @Test
  public static void testGetIsKeyFound() {
    assertEquals(true, level1.getIsKeyFound());
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
    assertEquals("■", room1.getTileInRoom(new Position(5, 8)));
    //assertEquals("O", room2.getTileInRoom(new Position(3, 2)));
    assertEquals("|", room3.getTileInRoom(new Position(3, 2)));
    assertEquals("*", room4.getTileInRoom(new Position(3, 2)));
  }

  @Test
  public static void testGetExitPosition() {
    Position result = new Position(2, 3);
    assertEquals(result, room2.getExitPosition());
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
  public static void testGetStartPositionHallway() {
    assertEquals(new Position(2,9), h1.getStartPositionOfHallway());
    assertEquals(new Position(7,6), h2.getStartPositionOfHallway());
    assertEquals(new Position(21,15), h3.getStartPositionOfHallway());
    assertEquals(new Position(4,35), h4.getStartPositionOfHallway());
    assertEquals(new Position(8,29),h5.getStartPositionOfHallway());
    assertEquals(new Position(7,25),h6.getStartPositionOfHallway());
  }

  @Test
  public static void testGetEndPositionHallway() {
    assertEquals(new Position(4,20), h1.getEndPositionOfHallway());
    assertEquals(new Position(15,15), h2.getEndPositionOfHallway());
    assertEquals(new Position(23,6), h3.getEndPositionOfHallway());
    assertEquals(new Position(26,2), h4.getEndPositionOfHallway());
    assertEquals(new Position(30,32), h5.getEndPositionOfHallway());
    assertEquals(new Position(7,19), h6.getEndPositionOfHallway());
  }

  @Test
  public static void testGetHallwayWaypoints() {
    ArrayList<Position> waypoints = new ArrayList<Position>();
    waypoints.add(new Position(2,20));
    assertEquals(waypoints, h1.getWaypoints());

    waypoints.clear();
    waypoints.add(new Position(11,6));
    waypoints.add(new Position(11,15));
    assertEquals(waypoints, h2.getWaypoints());

    waypoints.clear();
    waypoints.add(new Position(21,6));
    assertEquals(waypoints, h3.getWaypoints());


    waypoints.clear();
    waypoints.add(new Position(4,37));
    waypoints.add(new Position(38,37));
    waypoints.add(new Position(38,0));
    waypoints.add(new Position(26,0));
    assertEquals(waypoints, h4.getWaypoints());

    waypoints.clear();
    waypoints.add(new Position(25,29));
    waypoints.add(new Position(25,32));
    assertEquals(waypoints, h5.getWaypoints());

    waypoints.clear();
    waypoints.add(new Position(7,24));
    waypoints.add(new Position(10,24));
    waypoints.add(new Position(10,19));
    assertEquals(waypoints, h6.getWaypoints());

  }

  @Test
  public static void testgetAllRoomsInLevel() {
    LinkedHashSet<Room> allRooms = new LinkedHashSet<Room>();
    allRooms.add(room1);
    allRooms.add(room2);
    allRooms.add(room3);
    allRooms.add(room4);
    allRooms.add(room5);
    allRooms.add(room6);
    assertEquals(allRooms, level1.getAllRooms());
  }

  @Test
  public static void testgetLevelWidth() {
    assertEquals(40, level1.getLevelWidth());
  }

  @Test
  public static void testgetLevelHeight() {
    assertEquals(40, level1.getLevelHeight());
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
