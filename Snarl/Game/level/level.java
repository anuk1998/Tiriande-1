package level;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

// *TODO* Add a test directory in Game directory and put level.java in source directory

class Hallway {
  Position[] waypoints;

  //*TODO* Add fields here and way of making hallways/adding them to rooms once all rooms are made
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

  public void makeRoom() {
    for(int i = 0; i < this.roomHorizontalLength; i++) {
      for(int j = 0; j < this.roomVerticalLength; j++) {
        this.room[i][j] = "■";
      }
    }
    chooseDoors();
  }

  public void chooseDoors() {
    Random rand = new Random();
    int numberOfDoors = (int) ((Math.random() * (4 - 2)) + 2);
    System.out.println(numberOfDoors);
    ArrayList<Position> listOfEdgePositions = new ArrayList<Position>();

    // puts all edge positions in a list
    for (int i=0; i<this.roomHorizontalLength; i++) {
      Position tempPosX = new Position(i, 0);
      Position tempPosY = new Position(i, this.roomVerticalLength - 1);
      listOfEdgePositions.add(tempPosX);
      listOfEdgePositions.add(tempPosY);
    }
    for (int i=0; i<this.roomVerticalLength; i++) {
      Position tempPosX = new Position(0, i);
      Position tempPosY = new Position(this.roomHorizontalLength - 1, i);

      // to prevent adding corner squares twice
      if (i != 0 && i != this.roomVerticalLength - 1) {
        listOfEdgePositions.add(tempPosX);
        listOfEdgePositions.add(tempPosY);
      }

    }

    // assigns edge positions to the number of doors randomly chosen
    for (int i=0; i<numberOfDoors; i++) {
      int randomIndex = rand.nextInt(listOfEdgePositions.size());
      Position randomPos = listOfEdgePositions.get(randomIndex);
      this.room[randomPos.getX()][randomPos.getY()] = "|";
      listOfEdgePositions.remove(randomIndex);
    }

  }

  public Position placeKey() {
    Random rand = new Random();
    for (int i=0; i<this.roomHorizontalLength; i++) {
      for (int j=0; j<this.roomVerticalLength; i=j++) {
      Position tempPos = new Position(i, j);
      listOfAllPositions.add(tempPos);
      }
    }
    int randomIndex = rand.nextInt(listOfAllPositions.size());
    Position randomPos = listOfAllPositions.get(randomIndex);
    if (!this.room[randomPos.getX()][randomPos.getY()].equals("|")) {
      this.room[randomPos.getX()][randomPos.getY()] = "*";
      return new Position(randomPos.getX(), randomPos.getY());
    }
    else {
      placeKey();
    }
    return null;
  }

  public Position placeLevelExit() {
    Random rand = new Random();
    int randomIndex = rand.nextInt(listOfAllPositions.size());
    Position randomPos = listOfAllPositions.get(randomIndex);
    if (!(this.room[randomPos.getX()][randomPos.getY()].equals("*") ||
            this.room[randomPos.getX()][randomPos.getY()].equals("|"))) {
      this.room[randomPos.getX()][randomPos.getY()] = "O";
      return new Position(randomPos.getX(), randomPos.getY());
    }
    else {
      placeLevelExit();
    }
    return null;
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

  public Level() {
    levelWidth = 200;
    levelHeight = 200;
    levelPlane = new String[levelWidth][levelHeight];
    makeLevel();
    addRooms();
    placeLevelKeyInRandomRoom();
    placeLevelExitInRandomRoom();
    // *TODO* call a `addHallways()` function here once it's made in level.Hallway class
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
        this.levelPlane[i][j] = "X";
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

  public void addRooms() {
    //generate random number of rooms to add
    Random rand = new Random();
    int numRooms = rand.nextInt(10 - 5) + 5;

    for (int i=0; i<numRooms; i++) {
      System.out.println("CURRENTLY ON ROOM: " + i);

      // generate random room width
      int randWidth = rand.nextInt(20 - 5) + 5;
      // generate random room height
      int randHeight = rand.nextInt(20 - 5) + 5;
      //generate random position
      int randomPositionIndex = rand.nextInt(listOfAllLevelPositions.size());
      Position randPosition = listOfAllLevelPositions.get(randomPositionIndex);
      Room newRoom = new Room(randPosition, randWidth, randHeight);

      if (isRoomValid(newRoom)) {
        this.allRooms.add(newRoom);
        addRoomToPlane(newRoom);
      }
      else {
        i = i - 1; // if room isn't valid, repeat this iteration
      }
    }
  }

  // adds the new room layout to the level.Level plane (ASCII)
  public void addRoomToPlane(Room room) {
    int roomWidth = room.getHorizontalLength();
    int roomHeight = room.getHorizontalLength();
    int startPosX = room.getRoomPositionInLevel().getX();
    int startPosY = room.getRoomPositionInLevel().getY();

    System.out.println("Level width: " + levelWidth);
    System.out.println("Level height: " + levelHeight);
    System.out.println("Room startPosX: " + startPosX);
    System.out.println("Room startPosY: " + startPosY);
    System.out.println("Room width: " + roomWidth);
    System.out.println("Room height: " + roomHeight);
    int roomWidthIndex = 0;
    for (int i=startPosX; i<startPosX+roomWidth ; i++) {
      int roomHeightIndex = 0;
      System.out.println("roomWidthIndex: " + roomWidthIndex);
      for (int j=startPosY; j<startPosY+roomHeight; j++) {
        System.out.println("roomHeightIndex: " + roomHeightIndex);
        System.out.println("Current position is: (" + i + ", " + j + ")");
        levelPlane[i][j] = room.room[roomWidthIndex][roomHeightIndex];
        //levelPlane[i][j] = room.getRoomLayout()[roomWidthIndex][roomHeightIndex];
        roomHeightIndex++;
      }
      roomWidthIndex++;
    }
  }

  // checks if the new randomly-generated level.Room is valid/a realistic level.Room
  public boolean isRoomValid(Room newRoom) {
    int roomWidth = newRoom.getHorizontalLength();
    int roomHeight = newRoom.getHorizontalLength();
    int startPosX = newRoom.getRoomPositionInLevel().getX();
    int startPosY = newRoom.getRoomPositionInLevel().getY();

    // check if new room dimensions go beyond level plane boundaries
    if ((startPosX + roomWidth > levelWidth) || (startPosY + roomHeight > levelHeight)) {
      return false;
    }

    // check if the new room overlaps with any existing rooms/doors/key/exit/hallway in level plane
    for (int i=startPosX; i<startPosX+roomWidth; i++) {
      for (int j=startPosY; j<startPosY+roomHeight; j++) {
        if (levelPlane[i][j].equals("■") || levelPlane[i][j].equals("|") || levelPlane[i][j].equals("*") ||
                levelPlane[i][j].equals("O") || levelPlane[i][j].equals(".")) {
          return false;
        }
      }
    }
    return true;
  }

  public void placeLevelKeyInRandomRoom() {
    Random rand = new Random();
    int randomIndex = rand.nextInt(allRooms.size());

    ArrayList<Room> allRoomsList = new ArrayList<>(allRooms);
    Room randomRoom = allRoomsList.get(randomIndex);
    Position keyPosition = randomRoom.placeKey();

    levelPlane[keyPosition.getX()][keyPosition.getY()] = "*";
  }

  public void placeLevelExitInRandomRoom() {
    Random rand = new Random();
    int randomIndex = rand.nextInt(allRooms.size());

    ArrayList<Room> allRoomsList = new ArrayList<>(allRooms);
    Room randomRoom = allRoomsList.get(randomIndex);
    Position exitPosition = randomRoom.placeLevelExit();

    levelPlane[exitPosition.getX()][exitPosition.getY()] = "O";
  }

  public String renderLevel() {
    StringBuilder levelASCII = new StringBuilder();
    // Do the levelPlane[][] parsing and System.out.print each item for graphical rendering
    for (int i=0; i<levelWidth; i++) {
      for (int j=0; j<levelHeight; j++) {
        if (j == levelHeight- 1) {
          levelASCII.append(levelPlane[i][j] + "\n");
          //System.out.print(levelPlane[i][j] + "\n");
        }
        else {
          levelASCII.append(levelPlane[i][j] + " ");
          //System.out.print(levelPlane[i][j] + " ");
        }
      }
    }
    return levelASCII.toString();
  }

}

// X X X X X X X X X X X X X X X
// X X X X X X X X X X X X X X X
// X X X ■ ■ ■ ■ ■ ■ X X X X X X
// X X X ■ ■ ■ ■ ■ | X X X X X X
// X X X ■ ■ | ■ ■ ■ X X X X X X
// X X X X X X ■ ■ ■ ■ ■ | ■ ■ X
// X X X X X X | ■ ■ ■ ■ ■ ■ ■ X

// int roomwidth = randomnumber(1, 20)
// int roomlength = randomnumebr(1,20)
// int[][] generatedRoom;
//
// for (int i=0, i<roomwidth; i++) {
//   for (int j=0; j<roomlength; j++) {
//     generatedRoom[i][j] = "O";
//   }
// }

//ROOM 1 level.Room(below[][], (0,0), 8, 6, List(...), List(...));
//ROOM 2 level.Room(below[][], (12,2), 9, 6, List(...), List(...));
//ROOM 3 level.Room(below[][], (27,1), List(...), List(...));
// O O O O O O O O
// O O O O O O O O                                       O O O O O R
// O O O O O O O O         O O O O O O O O O             O O O O O O
// R O O O O O O R H H H H R O O O O O O O O             O O O O O O
// O O O O O O O O         O O O O O O O O O     H H H H R O O O O O
// O O O O O O O O         O O O O O O O O O     H       O O K O O O
//                         O E O O O O O O R H H H
//                         O O O O O O O O O
//
//
//
// O O O O O O O O O
// D O O O O O O O R
// O O O O O O O O O
// O O O O O O O O O
// O O O O O O O O O

class LevelTesting {
  static Level level1 = new Level();
  public static void main(String[] args) {
  testLevel();
  }


  @Test
  public static void testLevel() {
    assertEquals(" ", level1.renderLevel());
  }
}
