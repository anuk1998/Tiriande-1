import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

class Hallway {
  Position[] waypoints;

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
    this.room = makeRoom();
  }

  //removes the given player from that room
  public void removePlayer(Player p) {
  }

  //checks whether the given movement is valid
  public boolean isValidMove(Position from, Position to){
    return true;
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

  public String[][] makeRoom() {
    for(int i = 0; i < this.roomHorizontalLength; i++) {
      for(int j = 0; j < this.roomVerticalLength; j++) {
        this.room[i][j] = "■";
      }
    }
    chooseDoors();
    return this.room;
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

  public void placeKey() {
    Random rand = new Random();
    for (int i=0; i<this.roomHorizontalLength; i++) {
      for (int j=0; j<this.roomVerticalLength; i=j++) {
      Position tempPos = new Position(i, j);
      listOfAllPositions.add(tempPos);
      }
    }
    int randomIndex = rand.nextInt(listOfAllPositions.size());
    Position randomPos = listOfAllPositions.get(randomIndex);
    this.room[randomPos.getX()][randomPos.getY()] = "*";
  }

  public void placeLevelExit() {
    Random rand = new Random();
    int randomIndex = rand.nextInt(listOfAllPositions.size());
    Position randomPos = listOfAllPositions.get(randomIndex);
    if (!this.room[randomPos.getX()][randomPos.getY()].equals("*")) {
      this.room[randomPos.getX()][randomPos.getY()] = "O";
    }
    else {
      placeLevelExit();
    }
  }

}

class Level {
  String[][] levelPlane;
  int levelWidth = 100;
  int levelHeight = 100;
  LinkedHashSet<Room> allRooms;
  boolean isKeyFound;
  Set<Player> players;
  Set<Player> activePlayers;
  Set<Adversary> adversaries;
  boolean playersWon;
  ArrayList<Position> listOfAllLevelPositions;

  public Level(int width, int height, LinkedHashSet<Room> rooms, boolean keyFound,
               Set<Player> players, Set<Player> activePlayers, Set<Adversary>
                       adversaries, boolean playersWon) {
    this.levelWidth = 200;
    this.levelHeight = 200;
    this.levelPlane = makeLevel();
    this.allRooms = rooms;
    this.isKeyFound = keyFound;
    this.players = players;
    this.activePlayers = activePlayers;
    this.adversaries = adversaries;
    this.playersWon = playersWon;
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

  public String[][] makeLevel() {
    for(int i = 0; i < this.levelWidth; i++) {
      for(int j = 0; j < this.levelHeight; j++) {
        this.levelPlane[i][j] = "X";
        Position tempPos = new Position(i, j);
        listOfAllLevelPositions.add(tempPos);
      }
    }
    return this.levelPlane;
  }

  public void placeCharacter(Position placeLocation){

  }
  public void expelPlayer(Player p) {
    this.activePlayers.remove(p);
  }

  public void addRooms() {
    //generate random number of rooms to add
    Random rand = new Random();
    int numRooms = rand.nextInt(10 - 5) + 5;

    for (int i=0; i<numRooms; i++) {
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
      }
      else {
        i = i - 1; // if room isn't valid, repeat this iteration
      }
    }

  }

  public boolean isRoomValid(Room newRoom) {
    boolean isValid = true;
    //int roomWithBufferWidth = width + 2;
    //int roomWithBufferHeight = height + 2;




    // X X X X X X X X X X X X X X X
    // X X X X X X X X X X X X X X X
    // X X X ■ ■ ■ ■ ■ ■ X X X X X X
    // X X X ■ ■ ■ ■ ■ | X X X X X X
    // X X X ■ ■ | ■ ■ ■ X X X X X X
    // X X X X X X ■ ■ ■ ■ ■ | ■ ■ X
    // X X X X X X | ■ ■ ■ ■ ■ ■ ■ X







      return isValid;
  }

  public void randomRoomForKey() {
    Random rand = new Random();
    int randomIndex = rand.nextInt(allRooms.size());

    ArrayList<Room> allRoomsList = new ArrayList(allRooms);
    Room randomRoom = allRoomsList.get(randomIndex);
    randomRoom.placeKey();

  }

  public void randomRoomForExit() {
    Random rand = new Random();
    int randomIndex = rand.nextInt(allRooms.size());

    ArrayList<Room> allRoomsList = new ArrayList(allRooms);
    Room randomRoom = allRoomsList.get(randomIndex);
    randomRoom.placeLevelExit();
  }

}

// int roomwidth = randomnumber(1, 20)
// int roomlength = randomnumebr(1,20)
// int[][] generatedRoom;
//
// for (int i=0, i<roomwidth; i++) {
//   for (int j=0; j<roomlength; j++) {
//     generatedRoom[i][j] = "O";
//   }
// }

//ROOM 1 Room(below[][], (0,0), 8, 6, List(...), List(...));
//ROOM 2 Room(below[][], (12,2), 9, 6, List(...), List(...));
//ROOM 3 Room(below[][], (27,1), List(...), List(...));
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
