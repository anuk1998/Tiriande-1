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

interface Character {
  /*
  Position characterPosition;
  Room currentRoom;
  Level currentLevel;
  int characterID;
   */

  void moveCharacter(Position movePosition);
}

class Player implements Character{
  boolean isExpelled;

  public Player() {

  }

  @Override
  public void moveCharacter(Position movePosition) {

  }
}

class Adversary implements Character{

  void expelPlayer(boolean expel) {

  }

  @Override
  public void moveCharacter(Position movePosition) {

  }
}


class Room {
  String[][] room;
  Position roomPositionInLevel;
  int roomHorizontalLength;
  int roomVerticalLength;
  Set<Player> playersInRoom;
  Set<Adversary> adversariesInRoom;

  public Room(Position roomPos, int horiz, int vertic, Set<Player> players, Set<Adversary> adversaries) {
    this.roomPositionInLevel = roomPos;
    this.roomHorizontalLength = horiz;
    this.roomVerticalLength = vertic;
    this.room = makeRoom();
    this.playersInRoom = players;
    this.adversariesInRoom = adversaries;
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

}

class Level {
  String[][] levelPlane;
  int levelWidth;
  int levelHeight;
  LinkedHashSet<Room> allRooms;
  boolean isKeyFound;
  Set<Player> players;
  Set<Player> activePlayers;
  Set<Adversary> adversaries;
  boolean playersWon;

  public Level(int width, int height, LinkedHashSet<Room> rooms, boolean keyFound,
               Set<Player> players, Set<Player> activePlayers, Set<Adversary>
                       adversaries, boolean playersWon) {
    this.levelWidth = width;
    this.levelHeight = height;
    this.levelPlane = makeLevel();
    this.allRooms = rooms;
    this.isKeyFound = keyFound;
    this.players = players;
    this.activePlayers = activePlayers;
    this.adversaries = adversaries;
    this.playersWon = playersWon;
  }

  /*
    ADD GETTERS AND SETTERS!!!!!!!
  */

  public String[][] makeLevel() {
    for(int i = 0; i < this.levelWidth; i++) {
      for(int j = 0; j < this.levelHeight; j++) {
        this.levelPlane[i][j] = "X";
      }
    }
    return this.levelPlane;
  }

  public void placeCharacter(Position placeLocation){

  }
  public void expelPlayer(Player p) {
    this.activePlayers.remove(p);
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
