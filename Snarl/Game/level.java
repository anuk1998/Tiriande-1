public class Hallway {
  Position [] waypoints;

}

public class Position {
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

  public setX(int new_x) {
    x_pos = new_x;
  }

  public setY(int new_y) {
    y_pos = new_y;
  }

}

public class Room {
  int[][] room;
  Position roomPositionInLevel;
  int roomHorizontalLength;
  int roomVerticalLength;
  Set<Player> playersInRoom;
  Set<Adversary> adversariesInRoom;

  //removes the given player from that room
  public void removePlayer(Player p) {

  }

  //checks whether the given movement is valid
 public boolean isValidMove(Position from, Position to){

 }

 public int getHorizontalLength() {
   return this.roomHorizontalLength;
 }

 public int getVerticalLength() {
   return this.roomVerticalLength;
 }

 public getRoomPositionInLevel() {
   return getRoomPositionInLevel;
 }







}

public class Level {
  int[][] levelPlane;
  LinkedHashSet<Room> allRooms;
  boolean isKeyFound;
  Set<Player> players;
  Set<Player> activePlayers;
  Set<Adversary> adversaries;
  boolean playersWon` -- indicates if the level has been won (i.e. if a player has reached the exit)
  void placeCharacter(Position placeLocation)` -- places a new character to the given position
  void removePlayer(Player p)` -- marks the given player as expelled
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
O O O O O O O O
O O O O O O O O                                       O O O O O R
O O O O O O O O         O O O O O O O O O             O O O O O O
R O O O O O O R H H H H R O O O O O O O O             O O O O O O
O O O O O O O O         O O O O O O O O O     H H H H R O O O O O
O O O O O O O O         O O O O O O O O O     H       O O K O O O
                        O E O O O O O O R H H H
                        O O O O O O O O O



O O O O O O O O O
D O O O O O O O R
O O O O O O O O O
O O O O O O O O O
O O O O O O O O O
