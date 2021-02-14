package level;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;

// *TODO* Add a test directory in Game directory and put levelGenerated.java in source directory

class Hallway {
  ArrayList<Position> waypoints = new ArrayList<Position>();
  ArrayList<Position> allHallwayPositions = new ArrayList<Position>();
  Position startPosition;
  Position endPosition;

  public Hallway(Position startPosition, Position endPosition) {
    this.startPosition = startPosition;
    this.endPosition = endPosition;
  }

  public void addAWaypoint(Position waypoint) {
    this.waypoints.add(waypoint);
  }

  public ArrayList<Position> getWaypoints() {
    return this.waypoints;
  }

  public ArrayList<Position> getAllHallwayPositions() {
    return this.allHallwayPositions;
  }

  public Position getStartPositionOfHallway() {
    return this.startPosition;
  }

  public Position getEndPositionOfHallway() {
    return this.endPosition;
  }

  public void connectHallwayWaypoints() {
    ArrayList<Position> waypointsAndDoors = new ArrayList<Position>();
    //waypo
    this.waypoints.add(this.startPosition);
    this.waypoints.add(this.endPosition);

    for (int i=0; i<this.waypoints.size()-1; i++) {
      int tempX1 = this.waypoints.get(i).getX();
      int tempY1 = this.waypoints.get(i).getY();

      int tempX2 = this.waypoints.get(i+1).getX();
      int tempY2 = this.waypoints.get(i+1).getY();

      if (tempX1 == tempX2) {
        int min = (tempY1 <= tempY2) ? tempY1 : tempY2;
        int max = (tempY1 >= tempY2) ? tempY1 : tempY2;
        for (int j=min; j<max; j++) {
          this.allHallwayPositions.add(new Position(tempX1, j));
        }
      }
      else if (tempY1 == tempY2) {
        int min = (tempX1 <= tempX2) ? tempX1 : tempX2;
        int max = (tempX1 >= tempX2) ? tempX1 : tempX2;
        for (int j=min; j<max; j++) {
          this.allHallwayPositions.add(new Position(j, tempY1));
        }
      }
    }


  }

}

class Position {
  int x_pos;
  int y_pos;

  public Position(int x_pos, int y_pos) {
    this.x_pos = x_pos;
    this.y_pos = y_pos;
  }

  public int getX() {
    return this.y_pos;
  }

  public int getY() {
    return this.x_pos;
  }

  public void setX(int new_x) {
    this.x_pos = new_x;
  }

  public void setY(int new_y) {
    this.y_pos = new_y;
  }

  public boolean isSamePosition(Position p) {
    if (p.getX() == this.getX() && p.getY() == this.getY()) {
      return true;
    }
    return false;
  }


  /**
   * Override hashCode.
   *
   * @return hashcode
   */
   /*
  @Override
  public int hashCode() {
    final int hash = Objects.hash(this.x_pos) + Objects.hash(this.y_pos);
    return hash;
    //return super.hashCode();
    //return Objects.hash(this);
  }
*/


  @Override
  public boolean equals(Object other) {
    if (other instanceof Position) {
      Position pos = (Position) other;
      if (this.getX() == pos.getX() && this.getY() == pos.getY()) {
        return true;
      }
    }
    return false;
    /*
    if (this == other) {
      return true;
    }
    if (!(other instanceof Position)) {
      return false;
    }

    Position that = (Position) other; // safe cast
    return this.x_pos == that.getX() && this.y_pos == that.getY();
    */
  }

}


  class Player {
    Position playerPosition;
    Room currentRoom;
    Level currentLevel;
    int playerID;
    boolean isExpelled;

    public Player() {

    }

    public void movePlayer(Position movePosition) {
      this.playerPosition = movePosition;

    }

  }

  class Adversary {
    Position advPosition;
    Room currentRoom;
    int adversaryID;
    boolean isExpelled;

    public Adversary() {

    }

    void expelPlayer(boolean expel) {

    }

  }

  class Room {
    String[][] room;
    Position roomPositionInLevel;
    int roomHorizontalLength;
    int roomVerticalLength;
    Set<Player> playersInRoom;
    Set<Adversary> adversariesInRoom;
    ArrayList<Position> listOfAllPositions = new ArrayList<Position>();
    ArrayList<Position> listOfEdgePositions = new ArrayList<Position>();

    public Room(Position roomPos, int horiz, int vertic) {
      this.roomPositionInLevel = roomPos;
      this.roomHorizontalLength = horiz;
      this.roomVerticalLength = vertic;
      room = new String[roomVerticalLength][roomHorizontalLength];
      makeRoom();
    }

    //removes the given player from that room
    public void removePlayer(Player p) {
    }

    //checks whether the given movement is valid
    public boolean isValidMove(Position from, Position to) {
      return true;
    }

    public String[][] getRoomLayout() {
      return room;
    }

    public int getHorizontalLength() {
      return this.roomHorizontalLength;
    }

    public int getVerticalLength() {
      return this.roomVerticalLength;
    }

    public Position getRoomPositionInLevel() {

      return this.roomPositionInLevel;
    }

    public String getTileInRoom(Position tilePosition) {
      return room[tilePosition.getX()][tilePosition.getY()];
    }

    public void makeRoom() {
      for (int i = 0; i < this.roomVerticalLength; i++) {
        for (int j = 0; j < this.roomHorizontalLength ; j++) {
          Position tempPos = new Position(i, j);
          this.room[i][j] = "■";
          listOfAllPositions.add(tempPos);
        }
      }
    }


    public void addKey(Position p) {
      room[p.getX()][p.getY()] = "*";
    }

    public void addExit(Position p) {
      room[p.getX()][p.getY()] = "O";
    }

    public void addDoor(Position p) {
      room[p.getX()][p.getY()] = "|";
    }

    public Position getKeyPosition() {
      for (int i = 0; i < this.roomVerticalLength; i++) {
        for (int j = 0; j < this.roomHorizontalLength; j++) {
          if (room[i][j].equals("*")) {
            System.out.println("Got into if statement");
            System.out.println("i: " + i + " j: " + j);
            return new Position(i, j);

          }
        }
      }

      return null;
    }

    public ArrayList<Position> getTilePosition(int x, int y) {
      ArrayList<Position> tilePositions = new ArrayList<Position>();

      for (int i = 0; i < this.roomVerticalLength; i++) {
        for (int j = 0; j < this.roomHorizontalLength; j++) {
          if (room[i][j].equals("■")) {
            tilePositions.add(new Position(i, j));

          }
        }
      }
      return tilePositions;
    }

    public ArrayList<Position> getDoorPositions() {
      ArrayList<Position> doorPositions = new ArrayList<Position>();

      for (int i = 0; i < this.roomVerticalLength; i++) {
        for (int j = 0; j < this.roomHorizontalLength; j++) {
          if (room[i][j].equals("|")) {
            doorPositions.add(new Position(i, j));

          }
        }
      }
      return doorPositions;
    }

    public Position getExitPosition() {

      for (int i = 0; i < this.roomVerticalLength; i++) {
        for (int j = 0; j < this.roomHorizontalLength; j++) {
          if (room[i][j].equals("|")) {
            return new Position(i, j);

          }
        }
      }
      return null;
    }


    public ArrayList<Position> getHallwayWaypoints() {
      return null;
    }

    public Position getHallwayStartPosition() {
      return null;
    }

    public Position getHallwayEndPosition() {
      return null;
    }

    /**
     * Override hashCode.
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
      return Objects.hash(this.roomPositionInLevel, this.roomHorizontalLength, this.roomVerticalLength);
    }
  }

  class Level {
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

    public void addRoom(Room r) {
      int roomWidth = r.getHorizontalLength();
      int roomHeight = r.getVerticalLength();
      int startPosX = r.getRoomPositionInLevel().getX();
      int startPosY = r.getRoomPositionInLevel().getY();

      int roomWidthIndex = 0;
      for (int i = startPosX; i < startPosX + roomHeight; i++) {
        int roomHeightIndex = 0;
        for (int j = startPosY; j < startPosY + roomWidth; j++) {
          String tile = r.getTileInRoom(new Position(roomHeightIndex, roomWidthIndex));
          levelPlane[i][j] = tile;
          roomHeightIndex++;
        }
        roomWidthIndex++;
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

    public boolean getIsKeyFound() {
      return this.isKeyFound;
    }

    public LinkedHashSet<Room> getAllRooms() {
      return this.allRooms;
    }

    public Set<Player> getPlayers() {
      return this.players;
    }

    public Set<Player> getActivePlayers() {
      return this.activePlayers;
    }

    public Set<Adversary> getAdversaries() {
      return this.adversaries;
    }

    public boolean getPlayersWon() {
      return this.playersWon;
    }

    public void setLevelWidth(int newWidth) {
      this.levelWidth = newWidth;
    }

    public void setLevelHeight(int newHeight) {
      this.levelHeight = newHeight;
    }

    public void setIsKeyFound(boolean keyFound) {
      this.isKeyFound = keyFound;
    }

    public void addPlayer(Player p) {
      this.players.add(p);
      this.activePlayers.add(p);
    }

    public void setPlayersWon(boolean won) {
      this.playersWon = won;
    }

    public void makeLevel() {
      for (int i = 0; i < this.levelHeight; i++) {
        for (int j = 0; j < this.levelWidth; j++) {
          this.levelPlane[i][j] = ".";
          Position tempPos = new Position(i, j);
          listOfAllLevelPositions.add(tempPos);
        }
      }
    }

    public Position getKeyPositionInLevel() {
      for (int i = 0; i < levelHeight; i++) {
        for (int j = 0; j < levelWidth; j++) {
          if (levelPlane[i][j].equals("*")) {
            return new Position(i, j);
          }
        }
      }
      return null;
    }

    public Position getExitPositionInLevel() {
      for (int i = 0; i < levelHeight; i++) {
        for (int j = 0; j < levelWidth; j++) {
          if (levelPlane[i][j].equals("O")) {
            return new Position(i, j);
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

    public static void main(String[] args) {
      Level level1 = new Level();
      Room room1 = new Room(new Position(0,0), 10, 8);
      room1.addDoor(new Position(9, 2));
      level1.addRoom(room1);
      room1.addKey(new Position(4,4));

    // add another room to level
      Room room2 = new Room(new Position(15,15),10,10);
      room2.addDoor(new Position(0,0));
      room2.addExit(new Position(3,7));
      level1.addRoom(room2);

      //add another room to level
      Room room3 = new Room(new Position(17,4),6,4);
      room3.addDoor(new Position(3,0));
      level1.addRoom(room3);
      System.out.print(level1.renderLevel());


      // keep adding rooms + doors
      // then add a key somewhere in a room
      // then add level exit somewhere in a room
      //      make addKey and addExit methods in Room class
      // add all rooms to level
      // then make hallways
      // then add hallways

      //testGetKeyPosition();
      //testGetDoorPosition();
      //testGetExitPosition();
      //testGetExitPositionInLevel();
    }


    @Test
    public static void testLevel() {
      Level level1 = new Level();
      Room room1 = new Room(new Position(0, 0), 10, 8);
      room1.addDoor(new Position(9, 2));
      level1.addRoom(room1);
      room1.addKey(new Position(4, 4));
      assertEquals("foo", level1.renderLevel());
    }

    @Test
    public static void testGetKeyPosition() {
      Level level1 = new Level();
      Room room1 = new Room(new Position(0, 0), 10, 8);
      room1.addDoor(new Position(9, 2));
      level1.addRoom(room1);
      room1.addKey(new Position(4, 4));
      assertEquals(4, room1.getKeyPosition().getX());
      assertEquals(4, room1.getKeyPosition().getY());
    }

    @Test
    public static void testGetDoorPosition() {
      Level level1 = new Level();
      Room room1 = new Room(new Position(0, 0), 10, 8);
      room1.addDoor(new Position(9, 2));
      level1.addRoom(room1);
      room1.addKey(new Position(4, 4));
      //assertEquals(9, room1.getDoorPositions().get(0).getX());

    }

    @Test
    public static void testGetExitPosition() {
      Level level1 = new Level();
      Room room1 = new Room(new Position(0, 0), 10, 8);
      room1.addDoor(new Position(9, 2));
      level1.addRoom(room1);
      room1.addKey(new Position(4, 4));

      Room room2 = new Room(new Position(15, 15), 10, 10);
      room2.addDoor(new Position(0, 0));
      room2.addExit(new Position(3, 7));
      level1.addRoom(room2);

      Position result = new Position(3, 7);
      assertEquals(result, room2.getExitPosition());
    }

    @Test
    public static void testGetExitPositionInLevel() {
      Level level1 = new Level();
      Room room1 = new Room(new Position(0, 0), 10, 8);
      room1.addDoor(new Position(9, 2));
      level1.addRoom(room1);
      room1.addKey(new Position(4, 4));

      Room room2 = new Room(new Position(15, 15), 10, 10);
      room2.addDoor(new Position(0, 0));
      room2.addExit(new Position(3, 7));
      level1.addRoom(room2);

      assertEquals(new Position(3, 55), level1.getExitPositionInLevel());

    }

    @Test
    public static void testGetKeyPositionInLevel() {
      Level level1 = new Level();
      Room room1 = new Room(new Position(0, 0), 10, 8);
      room1.addDoor(new Position(9, 2));
      level1.addRoom(room1);
      room1.addKey(new Position(4, 4));

      Room room2 = new Room(new Position(15, 15), 10, 10);
      room2.addDoor(new Position(0, 0));
      room2.addExit(new Position(3, 7));
      level1.addRoom(room2);

      assertEquals(new Position(3, 55), level1.getKeyPositionInLevel());
    }
  }


