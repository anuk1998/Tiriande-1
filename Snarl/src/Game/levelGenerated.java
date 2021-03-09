package Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/*
This class if not for Milestone 2
 */

// *TODO* Add a test directory in Game directory and put levelGenerated.java in source directory

class HallwayGenerated {
  ArrayList<PositionGenerated> waypoints = new ArrayList<PositionGenerated>();
  PositionGenerated startPosition;
  PositionGenerated endPosition;

  public HallwayGenerated(PositionGenerated startPosition, PositionGenerated endPosition) {
    this.startPosition = startPosition;
    this.endPosition = endPosition;
  }

  public void addAWaypoint(PositionGenerated waypoint) {
    waypoints.add(waypoint);
  }

  public ArrayList<PositionGenerated> getWaypoints() {
    return this.waypoints;
  }

  public PositionGenerated getStartPositionOfHallway() {
    return this.startPosition;
  }

  public PositionGenerated getEndPositionOfHallway() {
    return this.endPosition;
  }




  //*TODO* Add fields here and way of making hallways/adding them to rooms once all rooms are made
}

class PositionGenerated {
  int x_pos;
  int y_pos;

  public PositionGenerated(int x_pos, int y_pos) {
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

  public boolean isSamePosition(PositionGenerated p) {
    if(p.getX() == this.getX() && p.getY() == this.getY()) {
      return true;
    }
    return false;
  }

}

class PlayerGenerated {
  PositionGenerated playerPosition;
  RoomGenerated currentRoom;
  LevelGenerated currentLevel;
  int playerID;
  boolean isExpelled;

  public PlayerGenerated() {

  }

  public void movePlayer(PositionGenerated movePosition) {
    this.playerPosition = movePosition;

  }

}

class AdversaryGenerated {
  PositionGenerated advPosition;
  RoomGenerated currentRoom;
  int adversaryID;
  boolean isExpelled;

  public AdversaryGenerated() {

  }

  void expelPlayer(boolean expel) {

  }

}


class RoomGenerated {
  String[][] room;
  PositionGenerated roomPositionInLevel;
  int roomHorizontalLength;
  int roomVerticalLength;
  Set<PlayerGenerated> playersInRoom;
  Set<AdversaryGenerated> adversariesInRoom;
  ArrayList<PositionGenerated> listOfAllPositions = new ArrayList<PositionGenerated>();
  ArrayList<PositionGenerated> listOfEdgePositions = new ArrayList<PositionGenerated>();

  public RoomGenerated(PositionGenerated roomPos, int horiz, int vertic) {
    this.roomPositionInLevel = roomPos;
    this.roomHorizontalLength = horiz;
    this.roomVerticalLength = vertic;
    room = new String[roomHorizontalLength][roomVerticalLength];
    makeRoom();
  }

  private void renderRoom() {
    for (int i=0; i<roomHorizontalLength; i++) {
      for (int j=0; j<roomVerticalLength; j++) {
        if (j == roomVerticalLength - 1) {
          System.out.print(room[i][j] + "\n");
        }
        else {
          System.out.print(room[i][j] + " ");
        }
      }
    }
  }

  //removes the given player from that room
  public void removePlayer(PlayerGenerated p) {
  }

  //checks whether the given movement is valid
  public boolean isValidMove(PositionGenerated from, PositionGenerated to){
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

  public PositionGenerated getRoomPositionInLevel() {

    return this.roomPositionInLevel;
  }

  public String getTileInRoom(PositionGenerated tilePosition) {
    return room[tilePosition.getX()][tilePosition.getY()];
  }

  public void makeRoom() {
    for(int i = 0; i < this.roomHorizontalLength; i++) {
      for(int j = 0; j < this.roomVerticalLength; j++) {
        PositionGenerated tempPos = new PositionGenerated(i, j);
        this.room[i][j] = "■";
        listOfAllPositions.add(tempPos);
      }
    }
    collectEdges();
    //chooseDoors();
  }

  public void addDoor(PositionGenerated p) {

  }

  private void collectEdges() {
    // puts all edge positions in a list
    for (int i=0; i<this.roomHorizontalLength; i++) {
      PositionGenerated tempPosX = new PositionGenerated(i, 0);
      PositionGenerated tempPosY = new PositionGenerated(i, this.roomVerticalLength - 1);
      listOfEdgePositions.add(tempPosX);
      listOfEdgePositions.add(tempPosY);
    }
    for (int i=0; i<this.roomVerticalLength; i++) {
      PositionGenerated tempPosX = new PositionGenerated(0, i);
      PositionGenerated tempPosY = new PositionGenerated(this.roomHorizontalLength - 1, i);

      // to prevent adding corner squares twice
      if (i != 0 && i != this.roomVerticalLength - 1) {
        listOfEdgePositions.add(tempPosX);
        listOfEdgePositions.add(tempPosY);
      }
    }
  }

  public void chooseDoors() {
    Random rand = new Random();
    int numberOfDoors = (int) ((Math.random() * (4 - 2)) + 2);

    // assigns edge positions to the number of doors randomly chosen
    for (int i=0; i<numberOfDoors; i++) {
      int randomIndex = rand.nextInt(listOfEdgePositions.size());
      PositionGenerated randomPos = listOfEdgePositions.get(randomIndex);
      this.room[randomPos.getX()][randomPos.getY()] = "|";
      listOfEdgePositions.remove(randomIndex);
    }
  }

  public PositionGenerated chooseNewDoor() {
    Random rand = new Random();
    int randomIndex = rand.nextInt(listOfEdgePositions.size());
    PositionGenerated randomPos = listOfEdgePositions.get(randomIndex);
    this.room[randomPos.getX()][randomPos.getY()] = "|";
    listOfEdgePositions.remove(randomIndex);
    return new PositionGenerated(randomPos.getX() + roomPositionInLevel.getX(),
            randomPos.getY() + roomPositionInLevel.getY());
  }

  public PositionGenerated placeKey() {
    Random rand = new Random();
    int randomIndex = rand.nextInt(listOfAllPositions.size());
    PositionGenerated randomPos = listOfAllPositions.get(randomIndex);
    if (!this.room[randomPos.getX()][randomPos.getY()].equals("|")) {
      this.room[randomPos.getX()][randomPos.getY()] = "*";
      return new PositionGenerated(roomPositionInLevel.getX() + randomPos.getX(),
              roomPositionInLevel.getY() + randomPos.getY());
    }
    else {
      return placeKey();
    }
  }

  public PositionGenerated placeLevelExit() {
    Random rand = new Random();
    int randomIndex = rand.nextInt(listOfAllPositions.size());
    PositionGenerated randomPos = listOfAllPositions.get(randomIndex);
    if (!(this.room[randomPos.getX()][randomPos.getY()].equals("*") ||
            this.room[randomPos.getX()][randomPos.getY()].equals("|"))) {
      this.room[randomPos.getX()][randomPos.getY()] = "O";
      return new PositionGenerated(roomPositionInLevel.getX() + randomPos.getX(),
              roomPositionInLevel.getY() + randomPos.getY());
    }
    else {
      return placeLevelExit();
    }
  }

}

class LevelGenerated {
  int levelWidth;
  int levelHeight;
  String[][] levelPlane;
  LinkedHashSet<RoomGenerated> allRooms = new LinkedHashSet<RoomGenerated>();
  boolean isKeyFound = false;
  //players and adversaries are set to null for this milestone
  Set<PlayerGenerated> players = null;
  Set<PlayerGenerated> activePlayers = null;
  Set<AdversaryGenerated> adversaries;
  boolean playersWon;
  ArrayList<PositionGenerated> listOfAllLevelPositions = new ArrayList<PositionGenerated>();
  HashMap<PositionGenerated, RoomGenerated> listOfDoorsInLevel = new HashMap<>();

  public LevelGenerated() {
    levelWidth = 40;
    levelHeight = 40;
    levelPlane = new String[levelWidth][levelHeight];
    makeLevel();
    //createRooms();
    //validateDoorPositions();
    //placeLevelKeyInRandomRoom();
    //placeLevelExitInRandomRoom();
    //createHallways();
    // *TODO* call a `addHallways()` function here once it's made in level.Hallway class
    //System.out.print(renderLevel());
  }

  public void addRoom(RoomGenerated r) {
    int roomWidth = r.getHorizontalLength();
    int roomHeight = r.getVerticalLength();
    int startPosX = r.getRoomPositionInLevel().getX();
    int startPosY = r.getRoomPositionInLevel().getY();

    int roomWidthIndex = 0;
    for (int i=startPosX; i<startPosX+roomWidth ; i++) {
      int roomHeightIndex = 0;
      for (int j=startPosY; j<startPosY+roomHeight; j++) {
        String tile = r.getTileInRoom(new PositionGenerated(roomWidthIndex, roomHeightIndex));
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

  public LinkedHashSet<RoomGenerated> getAllRooms() {
    return this.allRooms;
  }

  public Set<PlayerGenerated> getPlayers() {
    return this.players;
  }

  public Set<PlayerGenerated> getActivePlayers() {
    return this.activePlayers;
  }

  public Set<AdversaryGenerated> getAdversaries() {
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

  public void addPlayer(PlayerGenerated p) {
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
        PositionGenerated tempPos = new PositionGenerated(i, j);
        listOfAllLevelPositions.add(tempPos);
      }
    }
  }

  public void placeCharacter(PositionGenerated placeLocation) {

  }

  public void expelPlayer(PlayerGenerated p) {
    this.activePlayers.remove(p);
    // *TODO* change p isExpelled status to true
  }

  public void createRooms() {
    //generate random number of rooms to add
    Random rand = new Random();
    int numRooms = rand.nextInt(15 - 5) + 5;

    for (int i=0; i<numRooms; i++) {
      // generate random room width
      int randWidth = rand.nextInt(12 - 5) + 5;
      // generate random room height
      int randHeight = rand.nextInt(12 - 5) + 5;
      //generate random position
      int randomPositionIndex = rand.nextInt(listOfAllLevelPositions.size());
      PositionGenerated randPosition = listOfAllLevelPositions.get(randomPositionIndex);
      RoomGenerated newRoom = new RoomGenerated(randPosition, randWidth, randHeight);

      if (isRoomValid(newRoom)) {
        this.allRooms.add(newRoom);
        addRoomToPlane(newRoom);
      }
      else {
        i = i - 1; // if room isn't valid, repeat this iteration
      }
    }
  }

  // adds the new room layout to the level.Game.Level plane (ASCII)
  public void addRoomToPlane(RoomGenerated room) {
    int roomWidth = room.getHorizontalLength();
    int roomHeight = room.getVerticalLength();
    int startPosX = room.getRoomPositionInLevel().getX();
    int startPosY = room.getRoomPositionInLevel().getY();

    int roomWidthIndex = 0;
    for (int i=startPosX; i<startPosX+roomWidth ; i++) {
      int roomHeightIndex = 0;
      for (int j=startPosY; j<startPosY+roomHeight; j++) {
        String tile = room.getTileInRoom(new PositionGenerated(roomWidthIndex, roomHeightIndex));
        levelPlane[i][j] = tile;
        if (tile.equals("|")) {
          listOfDoorsInLevel.put(new PositionGenerated(i, j), room);
        }
        roomHeightIndex++;
      }
      roomWidthIndex++;
    }
  }

  private void validateDoorPositions() {
    Set<PositionGenerated> doorsToRemove = new HashSet<>();
    for (PositionGenerated door : listOfDoorsInLevel.keySet()) {
      //checking if all surrounding tiles are ■, then return false
      try {
        if (levelPlane[door.getX() + 1][door.getY()].equals("■") && levelPlane[door.getX() - 1][door.getY()].equals("■") &&
                levelPlane[door.getX()][door.getY() + 1].equals("■") && levelPlane[door.getX()][door.getY() - 1].equals("■")) {
          levelPlane[door.getX()][door.getY()] = "■";
          doorsToRemove.add(door);
          // *TODO* send it back to choose doors
        }
      }
      catch (ArrayIndexOutOfBoundsException e) {
        levelPlane[door.getX()][door.getY()] = "■";
      }
    }
    
    for (PositionGenerated doorToRemove : doorsToRemove) {
      listOfDoorsInLevel.remove(doorToRemove);
    }

    if ((listOfDoorsInLevel.keySet().size() % 2) != 0) {
      addDoor();
      validateDoorPositions();
    }
  }

  private void addDoor() {
    Random rand = new Random();
    int randomIndex = rand.nextInt(allRooms.size());
    ArrayList<RoomGenerated> allRoomsList = new ArrayList<>(allRooms);
    RoomGenerated randomRoom = allRoomsList.get(randomIndex);

    PositionGenerated newDoorPos = randomRoom.chooseNewDoor();
    listOfDoorsInLevel.put(newDoorPos, randomRoom);
    int newDoorXPosInLevelPlane = newDoorPos.getX();
    int newDoorYPosInLevelPlane = newDoorPos.getY();
    levelPlane[newDoorXPosInLevelPlane][newDoorYPosInLevelPlane] = "|";
  }

  // checks if the new randomly-generated level.Room is valid/a realistic level.Room
  public boolean isRoomValid(RoomGenerated newRoom) {
    int roomWidth = newRoom.getHorizontalLength();
    int roomHeight = newRoom.getVerticalLength();
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
                levelPlane[i][j].equals("O") || levelPlane[i][j].equals("X")) {
          return false;
        }
      }
    }
    return true;
  }

  public void placeLevelKeyInRandomRoom() {
    Random rand = new Random();
    int randomIndex = rand.nextInt(allRooms.size());

    ArrayList<RoomGenerated> allRoomsList = new ArrayList<>(allRooms);
    RoomGenerated randomRoom = allRoomsList.get(randomIndex);
    PositionGenerated keyPosition = randomRoom.placeKey();

    levelPlane[keyPosition.getX()][keyPosition.getY()] = "*";
  }

  public void placeLevelExitInRandomRoom() {
    Random rand = new Random();
    int randomIndex = rand.nextInt(allRooms.size());

    ArrayList<RoomGenerated> allRoomsList = new ArrayList<>(allRooms);
    RoomGenerated randomRoom = allRoomsList.get(randomIndex);
    PositionGenerated exitPosition = randomRoom.placeLevelExit();

    levelPlane[exitPosition.getX()][exitPosition.getY()] = "O";
  }

  private void createHallways() {
    chooseDoorsToConnect();
  }

  public void chooseDoorsToConnect() {
    Random rand = new Random();
    ArrayList<PositionGenerated> doorsKeySetList = new ArrayList<PositionGenerated>(listOfDoorsInLevel.keySet());
    int randomDoor1Index = rand.nextInt(doorsKeySetList.size());
    PositionGenerated randomDoor1 = doorsKeySetList.get(randomDoor1Index);

    int randomDoor2Index = rand.nextInt(doorsKeySetList.size());
    PositionGenerated randomDoor2 = doorsKeySetList.get(randomDoor2Index);

    RoomGenerated door1Room = listOfDoorsInLevel.get(randomDoor1);
    RoomGenerated door2Room = listOfDoorsInLevel.get(randomDoor2);

    PositionGenerated room1Pos = door1Room.getRoomPositionInLevel();
    PositionGenerated room2Pos = door2Room.getRoomPositionInLevel();

    while (randomDoor2.isSamePosition(randomDoor1) || room1Pos.isSamePosition(room2Pos)) {
      randomDoor2Index = rand.nextInt(doorsKeySetList.size());
      randomDoor2 = doorsKeySetList.get(randomDoor2Index);
    }

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

class LevelGeneratedTesting {

  public static void main(String[] args) {
    LevelGenerated level1 = new LevelGenerated();
    level1.addRoom(new RoomGenerated(new PositionGenerated(0,0), 10, 8));
    System.out.print(level1.renderLevel());
  //testLevel();
  }

  /*
  @Test
  public static void testLevel() {
    assertEquals("foo", level1.renderLevel());
  }
   */
}
