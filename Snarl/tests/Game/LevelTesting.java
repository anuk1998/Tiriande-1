package Game;

import Game.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;

class LevelTesting {
  static Level level1 = new Level();
  static Room room1 = new Room(new Position(0, 0), 10, 8);
  static Room room2 = new Room(new Position(15, 15), 10, 10);
  static Room room3 = new Room(new Position(17,4),6,4);
  static Room room4 = new Room(new Position(25, 2), 11, 7);
  static Room room5 = new Room(new Position(2, 23), 7, 14);
  static Room room6 = new Room(new Position(27,30), 7,7);
  static Hallway h1 = new Hallway(new Position(9, 2), new Position(20, 4));
  static Hallway h2 = new Hallway(new Position(6, 7), new Position(15, 15));
  static Hallway h3 = new Hallway(new Position(15, 21), new Position(6, 23));
  static Hallway h4 = new Hallway(new Position(35, 4), new Position(2, 26));
  static Hallway h5 = new Hallway(new Position(29, 8), new Position(32, 30));
  static Hallway h6 = new Hallway(new Position(25, 7), new Position(19,7));
  static Hallway h7 = new Hallway(new Position(8, 28), new Position(27, 33));
  static Hallway h8 = new Hallway(new Position(24, 23), new Position(29, 30));
  static Player p1 = new Player(1);
  static Player p2 = new Player(2);
  static Player p3 = new Player(3);
  static Player p4 = new Player(4);
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
    testGetXPosition();
    testGetYPosition();
    testGetDoorPositions();
    testGetTileInRoom();
    testgetAllRoomsInLevel();
    testgetLevelHeight();
    testgetLevelWidth();
    testRoomGetHorizontalLength();
    testRoomGetVerticalLength();
    testGetRoomStartPositionInLevel();
  }

  private static void testGetIsKeyFoundBefore() {
    assertEquals(false, level1.getIsKeyFound());
  }

  private static void testGetExitTileInLevelAfterUnlocked() {
    assertEquals("O", level1.getTileInLevel(level1.getExitPositionInLevel()));
  }

  private static void createModifiedAfterObjectGameState() {
    System.out.println("MODIFIED AFTER OBJECT INTERACTION GAMESTATE:\n\n\n");
    // this moves player 2 to the exit key tile
    level1.movePlayer(p2, new Position(27, 5));
    System.out.println(level1.renderLevel());
    System.out.println("");
    // this moves player 2 off of the exit key tile, and the rendered level shows that the
    // key is no longer on the board (because it's been found & collected), and the level exit
    // once a filled-in circle, is now a hollow circle, symbolizing that it is unlocked
    level1.movePlayer(p2, new Position(29, 5));
    System.out.println(level1.renderLevel());
    System.out.println("");
  }

  private static void createIntermediateGameState() {
    System.out.println();
    System.out.println("INTERMEDIATE GAMESTATE:");
    System.out.println();
    level1.movePlayer(p1, new Position(13,2));
    level1.movePlayer(p3, new Position(20,15));
    level1.movePlayer(p4, new Position(4,30));
    //move adversaries around
    level1.moveAdversary(a1, new Position(17,21));
    level1.moveAdversary(a2, new Position(0,6));
    //moves player 2 right next to key in level
    level1.movePlayer(p2, new Position(level1.getKeyPositionInLevel().getY() - 1, level1.getKeyPositionInLevel().getX()));
    System.out.print(level1.renderLevel());
  }

  public static void createInitialGameState() {
    System.out.println();
    System.out.println("INITIAL GAMESTATE:");
    System.out.println();
    //Room 1
    room1.addDoor(new Position(9, 2));
    room1.addDoor(new Position(6,7));
    room4.addKey(new Position(2, 3));

    //Room 2
    room2.addDoor(new Position(0, 0));
    room2.addExit(new Position(3, 2));
    room2.addDoor(new Position(0,6));
    room2.addDoor(new Position(9,8));

    //Room 3
    room3.addDoor(new Position(3,0));
    room3.addDoor(new Position(2, 3));

    //Room 4
    room4.addDoor(new Position(0, 5));
    room4.addDoor(new Position(10, 2));
    room4.addDoor(new Position(4, 6));

    //Room 5
    room5.addDoor(new Position(0,3));
    room5.addDoor(new Position(4,0));
    room5.addDoor(new Position(6,5));

    //Room 6
    room6.addDoor(new Position(5,0));
    room6.addDoor(new Position(0,3));
    room6.addDoor(new Position(2,0));

    //Add rooms to level
    level1.addRoom(room1);
    level1.addRoom(room2);
    level1.addRoom(room3);
    level1.addRoom(room4);
    level1.addRoom(room5);
    level1.addRoom(room6);

    h1.addAWaypoint(new Position(20, 2));
    h1.connectHallwayWaypoints();

    h2.addAWaypoint(new Position(6, 11));
    h2.addAWaypoint(new Position(15, 11));
    h2.connectHallwayWaypoints();

    h3.addAWaypoint(new Position(6,21));
    h3.connectHallwayWaypoints();

    h4.addAWaypoint(new Position(37, 4));
    h4.addAWaypoint(new Position(37, 38));
    h4.addAWaypoint(new Position(0, 38));
    h4.addAWaypoint(new Position(0, 26));
    h4.connectHallwayWaypoints();

    h5.addAWaypoint(new Position(29,25));
    h5.addAWaypoint(new Position(32, 25));
    h5.connectHallwayWaypoints();

    h6.addAWaypoint(new Position(24,7));
    h6.addAWaypoint(new Position(24,10));
    h6.addAWaypoint(new Position(19,10));
    h6.connectHallwayWaypoints();

    h7.addAWaypoint(new Position(11, 28));
    h7.addAWaypoint(new Position(11, 33));
    h7.connectHallwayWaypoints();

    h8.addAWaypoint(new Position(27, 23));
    h8.addAWaypoint(new Position(27, 27));
    h8.addAWaypoint(new Position(29, 27));
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
    level1.addPlayer(p1, new Position(4, 2));
    level1.addPlayer(p2, new Position(1, 6));
    level1.addPlayer(p3, new Position(5, 5));
    level1.addPlayer(p4, new Position(2, 7));

    //add adversaries to the level
    level1.addAdversary(a1, new Position(29,32));
    level1.addAdversary(a2, new Position(32,34));
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
  public static void testGetXPosition() {
    Position testPosition = new Position(4,5);
    assertEquals(5, testPosition.getX());
  }

  @Test
  public static void testGetYPosition() {
    Position testPosition = new Position(6,11);
    assertEquals(6, testPosition.getY());
  }

  @Test
  public static void testGetKeyPosition() {
    assertEquals(new Position(2,3), room4.getKeyPosition());
  }

  @Test
  public static void testGetIsKeyFound() {
    assertEquals(true, level1.getIsKeyFound());
  }

  @Test
  public static void testGetDoorPositions() {
    ArrayList<Position> doorPositions = new ArrayList<Position>();
    doorPositions.add(new Position(9,2));
    doorPositions.add(new Position(6,7));
    assertEquals(doorPositions, room1.getDoorPositions());

    doorPositions.clear();
    doorPositions.add(new Position(0, 5));
    doorPositions.add(new Position(10, 2));
    doorPositions.add(new Position(4, 6));
    assertEquals(doorPositions, room4.getDoorPositions());
  }

  @Test
  public static void testGetTileInRoom() {
    assertEquals("■", room1.getTileInRoom(new Position(8, 5)));
    //assertEquals("O", room2.getTileInRoom(new Position(3, 2)));
    assertEquals("|", room3.getTileInRoom(new Position(2, 3)));
    assertEquals("*", room4.getTileInRoom(new Position(2, 3)));
  }

  @Test
  public static void testGetExitPosition() {
    Position result = new Position(3, 2);
    assertEquals(result, room2.getExitPosition());
  }

  @Test
  public static void testGetExitPositionInLevel() {
    assertEquals(new Position(18, 17), level1.getExitPositionInLevel());
  }

  @Test
  public static void testGetKeyPositionInLevel() {
    assertEquals(new Position(27, 5), level1.getKeyPositionInLevel());
  }

  @Test
  public static void testGetStartPositionHallway() {
    assertEquals(new Position(9,2), h1.getStartPositionOfHallway());
    assertEquals(new Position(6,7), h2.getStartPositionOfHallway());
    assertEquals(new Position(15,21), h3.getStartPositionOfHallway());
    assertEquals(new Position(35,4), h4.getStartPositionOfHallway());
    assertEquals(new Position(29,8),h5.getStartPositionOfHallway());
    assertEquals(new Position(25,7),h6.getStartPositionOfHallway());
  }

  @Test
  public static void testGetEndPositionHallway() {
    assertEquals(new Position(20,4), h1.getEndPositionOfHallway());
    assertEquals(new Position(15,15), h2.getEndPositionOfHallway());
    assertEquals(new Position(6,23), h3.getEndPositionOfHallway());
    assertEquals(new Position(2,26), h4.getEndPositionOfHallway());
    assertEquals(new Position(32,30), h5.getEndPositionOfHallway());
    assertEquals(new Position(19,7), h6.getEndPositionOfHallway());
  }

  @Test
  public static void testGetHallwayWaypoints() {
    ArrayList<Position> waypoints = new ArrayList<Position>();
    waypoints.add(new Position(20,2));
    assertEquals(waypoints, h1.getWaypoints());

    waypoints.clear();
    waypoints.add(new Position(6,11));
    waypoints.add(new Position(15,11));
    assertEquals(waypoints, h2.getWaypoints());

    waypoints.clear();
    waypoints.add(new Position(6,21));
    assertEquals(waypoints, h3.getWaypoints());


    waypoints.clear();
    waypoints.add(new Position(37,4));
    waypoints.add(new Position(37,38));
    waypoints.add(new Position(0,38));
    waypoints.add(new Position(0,26));
    assertEquals(waypoints, h4.getWaypoints());

    waypoints.clear();
    waypoints.add(new Position(29,25));
    waypoints.add(new Position(32,25));
    assertEquals(waypoints, h5.getWaypoints());

    waypoints.clear();
    waypoints.add(new Position(24,7));
    waypoints.add(new Position(24,10));
    waypoints.add(new Position(19,10));
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
  public static void testRoomGetHorizontalLength() {
    assertEquals(10, room1.getHorizontalLength());
    assertEquals(6, room3.getHorizontalLength());
    assertEquals(7, room5.getHorizontalLength());
  }

  @Test
  public static void testRoomGetVerticalLength() {
    assertEquals(8, room1.getVerticalLength());
    assertEquals(4, room3.getVerticalLength());
    assertEquals(14, room5.getVerticalLength());
  }

  @Test
  public static void testGetRoomStartPositionInLevel() {
    assertEquals(new Position(0,0), room1.getRoomStartPositionInLevel());
    assertEquals(new Position(15,15), room2.getRoomStartPositionInLevel());
    assertEquals(new Position(17,4), room3.getRoomStartPositionInLevel());
  }

}
