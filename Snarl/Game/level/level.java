package level;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

// *TODO* Add a test directory in Game directory and put levelGenerated.java in source directory

class Hallway {
  ArrayList<Position> waypoints = new ArrayList<Position>();
  Position startPosition;
  Position endPosition;

  public Hallway(Position startPosition, Position endPosition) {
    this.startPosition = startPosition;
    this.endPosition = endPosition;
  }

  public void addAWaypoint(Position waypoint) {
    waypoints.add(waypoint);
  }

  public ArrayList<Position> getWaypoints() {
    return this.waypoints;
  }

  public Position getStartPositionOfHallway() {
    return this.startPosition;
  }

  public Position getEndPositionOfHallway() {
    return this.endPosition;
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
    return x_pos;
  }

  public int getY() {
    return y_pos;
  }

  public void setX(int new_x) {
    this.x_pos = new_x;
  }

  public void setY(int new_y) {
    this.y_pos = new_y;
  }

  public boolean isSamePosition(Position p) {
    if(p.getX() == this.getX() && p.getY() == this.getY()) {
      return true;
    }
    return false;
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
    room = new String[roomHorizontalLength][roomVerticalLength];
    makeRoom();
  }

  //removes the given player from that room
  public void removePlayer(Player p) {
  }

  //checks whether the given movement is valid
  public boolean isValidMove(Position from, Position to){
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
    for(int i = 0; i < this.roomHorizontalLength; i++) {
      for(int j = 0; j < this.roomVerticalLength; j++) {
        Position tempPos = new Position(i, j);
        this.room[i][j] = "■";
        listOfAllPositions.add(tempPos);
      }
    }
  }

  public void addDoor(Position p) {
    room[p.getX()][p.getY()] = "|";
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
    for (int i=startPosX; i<startPosX+roomWidth ; i++) {
      int roomHeightIndex = 0;
      for (int j=startPosY; j<startPosY+roomHeight; j++) {
        String tile = r.getTileInRoom(new Position(roomWidthIndex, roomHeightIndex));
        levelPlane[i][j] = tile;
        roomHeightIndex++;
      }
      roomWidthIndex++;
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
    for(int i = 0; i < this.levelWidth; i++) {
      for(int j = 0; j < this.levelHeight; j++) {
        this.levelPlane[i][j] = ".";
        Position tempPos = new Position(i, j);
        listOfAllLevelPositions.add(tempPos);
      }
    }
  }

  public void placeCharacter(Position placeLocation) {

  }

  public void expelPlayer(Player p) {
    this.activePlayers.remove(p);
    // *TODO* change p isExpelled status to true
  }

  public String renderLevel() {
    StringBuilder levelASCII = new StringBuilder();
    for (int i=0; i<levelWidth; i++) {
      for (int j=0; j<levelHeight; j++) {
        if (j == levelHeight- 1) {
          levelASCII.append(levelPlane[i][j] + "\n");
        }
        else {
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
    System.out.print(level1.renderLevel());

    // keep adding rooms + doors
    // then add a key somewhere in a room
    // then add level exit somewhere in a room
    //      make addKey and addExit methods in Room class
    // add all rooms to level
    // then make hallways
    // then add hallways

    //testLevel();
  }

  /*
  @Test
  public static void testLevel() {
    assertEquals("foo", level1.renderLevel());
  }
   */
}

