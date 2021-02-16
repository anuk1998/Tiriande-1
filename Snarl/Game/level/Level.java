package level;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

// *TODO* Add a test directory in Game directory and put levelGenerated.java in source directory

  public class Level {
    int levelWidth;
    int levelHeight;
    String[][] levelPlane;
    LinkedHashSet<Room> allRooms = new LinkedHashSet<Room>();
    boolean isKeyFound = false;
    //players and adversaries are set to null for this milestone
    Set<Player> players = null;
    Set<Player> activePlayers = null;
    Set<Adversary> adversaries;
    boolean playersWon;
    ArrayList<Position> listOfAllLevelPositions = new ArrayList<Position>();
    HashMap<Position, Room> listOfDoorsInLevel = new HashMap();

    public Level() {
      levelWidth = 40;
      levelHeight = 40;
      levelPlane = new String[levelWidth][levelHeight];
      makeLevel();
    }

    public void addRoom(Room r) throws ArrayIndexOutOfBoundsException {
      try {
        int roomWidth = r.getHorizontalLength();
        int roomHeight = r.getVerticalLength();
        int startPosX = r.getRoomStartPositionInLevel().getX();
        int startPosY = r.getRoomStartPositionInLevel().getY();

        int roomWidthIndex = 0;
        for (int i = startPosX; i < startPosX + roomHeight; i++) {
          int roomHeightIndex = 0;
          for (int j = startPosY; j < startPosY + roomWidth; j++) {
            String tile = r.getTileInRoom(new Position(roomHeightIndex, roomWidthIndex)); // may have to switch order
            levelPlane[i][j] = tile;
            this.allRooms.add(r);
            roomHeightIndex++;
          }
          roomWidthIndex++;
        }
      }
      catch (ArrayIndexOutOfBoundsException e) {
        throw new ArrayIndexOutOfBoundsException("The given room dimensions are invalid.");
      }
    }

    public void addHallway(Hallway hallway) {
      for (Position hallwayPos : hallway.getAllHallwayPositions()) {
        this.levelPlane[hallwayPos.getX()][hallwayPos.getY()] = "X";
      }

    }

    public int getLevelWidth() {
      return this.levelWidth;
    }

    public int getLevelHeight() {
      return this.levelHeight;
    }

    // UNUSED IN THIS MILESTONE, WILL TEST AT LATER DATE
    public boolean getIsKeyFound() {
      return this.isKeyFound;
    }

    public LinkedHashSet<Room> getAllRooms() {
      return this.allRooms;
    }

    // UNUSED IN THIS MILESTONE, WILL TEST AT LATER DATE
    public Set<Player> getPlayers() {
      return this.players;
    }

    // UNUSED IN THIS MILESTONE, WILL TEST AT LATER DATE
    public Set<Player> getActivePlayers() {
      return this.activePlayers;
    }

    // UNUSED IN THIS MILESTONE, WILL TEST AT LATER DATE
    public Set<Adversary> getAdversaries() {
      return this.adversaries;
    }

    // UNUSED IN THIS MILESTONE, WILL TEST AT LATER DATE
    public boolean getPlayersWon() {
      return this.playersWon;
    }

    public void setLevelWidth(int newWidth) {
      this.levelWidth = newWidth;
    }

    public void setLevelHeight(int newHeight) {
      this.levelHeight = newHeight;
    }

    // UNUSED IN THIS MILESTONE, WILL TEST AT LATER DATE
    public void setIsKeyFound(boolean keyFound) {
      this.isKeyFound = keyFound;
    }

    // UNUSED IN THIS MILESTONE, WILL TEST AT LATER DATE
    public void addPlayer(Player p) {
      this.players.add(p);
      this.activePlayers.add(p);
    }

    // UNUSED IN THIS MILESTONE, WILL TEST AT LATER DATE
    public void setPlayersWon(boolean won) {
      this.playersWon = won;
    }

    public void makeLevel() {
      for (int i = 0; i < this.levelHeight; i++) {
        for (int j = 0; j < this.levelWidth; j++) {
          this.levelPlane[i][j] = ".";
          Position tempPos = new Position(j, i); // may need to revert this to (i, j)
          listOfAllLevelPositions.add(tempPos);
        }
      }
    }

    public Position getKeyPositionInLevel() {
      for (int i = 0; i < levelHeight; i++) {
        for (int j = 0; j < levelWidth; j++) {
          if (levelPlane[i][j].equals("*")) {
            return new Position(j, i);
          }
        }
      }
      return null;
    }

    public Position getExitPositionInLevel() {
      for (int i = 0; i < levelHeight; i++) {
        for (int j = 0; j < levelWidth; j++) {
          if (levelPlane[i][j].equals("O")) {
            return new Position(j, i);
          }
        }
      }
      return null;
    }

    public void placeCharacter(Position placeLocation) {

    }

    public void expelPlayer(Player p) {
      this.activePlayers.remove(p);
      // *TODO* change p isExpelled status to true
    }

    public String renderLevel() {
      StringBuilder levelASCII = new StringBuilder();
      for (int i = 0; i < levelHeight; i++) {
        for (int j = 0; j < levelWidth; j++) {
          if (j == levelHeight - 1) {
            levelASCII.append(levelPlane[i][j] + "\n");
          } else {
            levelASCII.append(levelPlane[i][j] + " ");
          }
        }
      }
      return levelASCII.toString();
    }

  }

  class LevelTesting {
    static Level level1 = new Level();
    static Room room1 = new Room(new Position(0, 0), 10, 8);
    static Room room2 = new Room(new Position(15, 15), 10, 10);
    static Room room3 = new Room(new Position(17,4),6,4);
    static Room room4 = new Room(new Position(25, 2), 11, 7);
    static Room room5 = new Room(new Position(2, 23), 7, 14);
    static Hallway h1 = new Hallway(new Position(9, 2), new Position(20, 4));
    static Hallway h2 = new Hallway(new Position(6, 7), new Position(15, 15));
    static Hallway h3 = new Hallway(new Position(15, 21), new Position(6, 23));
    static Hallway h4 = new Hallway(new Position(35, 4), new Position(2, 26));
    static Hallway h5 = new Hallway(new Position(29, 8), new Position(8, 28));
    static Hallway h6 = new Hallway(new Position(25, 7), new Position(19,7));

    public static void main(String[] args) throws Exception {
      createLevelsAndRoomsAndHallways();
      //testGetKeyPosition();
      //testGetDoorPosition();
      //testGetExitPosition();
      //testGetKeyPositionInLevel();
      //testGetExitPositionInLevel();
      //testGetStartPositionHallway();
      //testGetEndPositionHallway();
      //testGetHallwayWaypoints();
      //invalidRoom();
      //invalidDoorPlacement();
      //testGetXPosition();
      //testGetYPosition();
      //testGetDoorPositions();
      //testGetTileInRoom();
      //testgetAllRoomsInLevel();
      //testgetLevelHeight();
      //testgetLevelWidth();
      testRoomGetHorizontalLength();
      testRoomGetVerticalLength();
      testGetRoomStartPositionInLevel();
    }

    public static void createLevelsAndRoomsAndHallways(){
      //Room 1
      room1.addDoor(new Position(9, 2));
      room1.addDoor(new Position(6,7));
      room4.addKey(new Position(2, 3));

      //Room 2
      room2.addDoor(new Position(0, 0));
      room2.addExit(new Position(3, 2));
      room2.addDoor(new Position(0,6));

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

      //Add rooms to level
      level1.addRoom(room1);
      level1.addRoom(room2);
      level1.addRoom(room3);
      level1.addRoom(room4);
      level1.addRoom(room5);

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

      h5.addAWaypoint(new Position(29,28));
      h5.connectHallwayWaypoints();

      h6.addAWaypoint(new Position(24,7));
      h6.addAWaypoint(new Position(24,10));
      h6.addAWaypoint(new Position(19,10));
      h6.connectHallwayWaypoints();

      level1.addHallway(h1);
      level1.addHallway(h2);
      level1.addHallway(h3);
      level1.addHallway(h4);
      level1.addHallway(h5);
      level1.addHallway(h6);
      System.out.print(level1.renderLevel());

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public static void invalidRoom() {
      Room r1 = new Room(new Position(100, 100), 10, 10);
    }


    @Test(expected = IllegalArgumentException.class)
    public static void  invalidDoorPlacement() throws Exception{
      Position pos = new Position(100,100);
      room4.addDoor(pos);
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
      assertEquals("O", room2.getTileInRoom(new Position(3, 2)));
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
      assertEquals(new Position(8,28), h5.getEndPositionOfHallway());
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
      waypoints.add(new Position(29,28));
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


