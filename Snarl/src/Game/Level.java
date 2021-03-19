package Game;
import java.util.*;

public class Level {
    int levelNumOfRows;
    int levelNumOfCols;
    String[][] levelPlane;
    LinkedHashSet<Room> allRooms = new LinkedHashSet<Room>();
    Position keyLevelPosition;
    Position exitLevelPosition;
    Set<Player> activePlayers = new HashSet<Player>();
    Set<IAdversary> adversaries = new HashSet<IAdversary>();
    HashMap<String, Room> listOfDoorsInLevel = new HashMap<String, Room>();
    Set<Hallway> listOfHallwaysInLevel = new HashSet<>();
    HashMap<String, ArrayList<Hallway>> roomsAndTheirHallways = new HashMap<String, ArrayList<Hallway>>();
    HashMap<String, Room> positionsAndTheirRooms = new HashMap<>();

    public Level() {
      levelNumOfRows = 40;
      levelNumOfCols = 40;
      levelPlane = new String[levelNumOfRows][levelNumOfCols];
      makeLevel();
    }

    // adds a Room to the levelPlane 2D array
    public void addRoom(Room r) throws ArrayIndexOutOfBoundsException {
      try {
        int roomRows = r.getNumOfRows();
        int roomCols = r.getNumOfCols();
        int startPosRow = r.getRoomOriginInLevel().getRow();
        int startPosCol = r.getRoomOriginInLevel().getCol();

        int roomRowIndex = 0;
        for (int i = startPosRow; i < startPosRow + roomRows; i++) {
          int roomColIndex = 0;
          for (int j = startPosCol; j < startPosCol + roomCols; j++) {
            String tile = r.getTileInRoom(new Position(roomRowIndex, roomColIndex));
            levelPlane[i][j] = tile;
            this.allRooms.add(r);
            this.positionsAndTheirRooms.put(new Position(i, j).toString(), r);
            roomColIndex++;
          }
          roomRowIndex++;
        }
        for (Position doorPos : r.getDoorPositions()) {
          Position scaledPos = new Position(doorPos.getRow() + r.getRoomOriginInLevel().getRow(),
                  doorPos.getCol() + r.getRoomOriginInLevel().getCol());
          this.listOfDoorsInLevel.put(scaledPos.toString(), r);
        }
        this.allRooms.add(r);
        this.roomsAndTheirHallways.put(r.getRoomOriginInLevel().toString(), new ArrayList<>());
      }
      catch (ArrayIndexOutOfBoundsException e) {
        throw new ArrayIndexOutOfBoundsException("The given room dimensions are invalid.");
      }
    }

    // adds a hallway to the levelPlane 2D array
    public void addHallway(Hallway hallway) {
      listOfHallwaysInLevel.add(hallway);
      for (Position hallwayPos : hallway.getAllHallwayPositions()) {
        this.levelPlane[hallwayPos.getRow()][hallwayPos.getCol()] = "X";
      }
      // adds that hallway to its room's list of connected hallways
      for (Room r : startAndEndRooms(hallway)) {
        this.roomsAndTheirHallways.get(r.getRoomOriginInLevel().toString()).add(hallway);
      }
    }

    public ArrayList<Hallway> getConnectedHallways(Room r) {
      return this.roomsAndTheirHallways.get(r.getRoomOriginInLevel().toString());
    }

    public Room getBelongingRoom(Position p) {
      return this.positionsAndTheirRooms.get(p.toString());
    }

    // returns the Hallway that the given point belongs to
    public Hallway getHallwayFromPoint(Position p) {
      Hallway hallway = null;
      for (Hallway h : this.listOfHallwaysInLevel) {
        for (Position pos : h.getAllHallwayPositions()) {
          if (p.equals(pos)) {
            hallway = h;
          }
        }
      }
      return hallway;
    }

    public ArrayList<Position> getAllAdjacentTiles(Position pos, Room room) {
      ArrayList<Position> adjacentTiles = new ArrayList<Position>();
      int row = pos.getRow();
      int col = pos.getCol();

      if (room != null) {
        int scaledRow = row - room.getRoomOriginInLevel().getRow();
        int scaledCol = col - room.getRoomOriginInLevel().getCol();

        if (scaledRow >= room.getNumOfRows() || scaledCol >= room.getNumOfCols()) {
          return null;
        }
      }

      Position[] adjacentsToCheck = {
              new Position(row - 1, col),
              new Position(row, col - 1),
              new Position(row, col + 1),
              new Position(row + 1, col)};

      for (Position adjacentTile : adjacentsToCheck) {
        try {
          String tile = this.levelPlane[adjacentTile.getRow()][adjacentTile.getCol()];
          if (tile.equals(".") || tile.equals("|") || tile.equals("x")) {
            adjacentTiles.add(adjacentTile);
          }
        }
        catch (ArrayIndexOutOfBoundsException e) {
          continue;
        }
      }
      return adjacentTiles;
    }

    // gets the levelNumOfRows field
    public int getLevelNumOfRows() {
      return this.levelNumOfRows;
    }

    // gets the levelNumOfCols field
    public int getLevelNumOfCols() {
      return this.levelNumOfCols;
    }

    public Set<Player> getActivePlayers() {
      return this.activePlayers;
    }

    public Set<IAdversary> getAdversaries() {
      return this.adversaries;
    }

    // constructs the 2D String array of the level, populating all entries with '.'
    //    and constructs the set containing all level positions
    public void makeLevel() {
      for (int i = 0; i < this.levelNumOfRows; i++) {
        for (int j = 0; j < this.levelNumOfCols; j++) {
          this.levelPlane[i][j] = ".";
        }
      }
    }

    // adds the key on the levelPlane based on the given position
    public void addKey(Position p) throws ArrayIndexOutOfBoundsException {
      try {
        this.levelPlane[p.getRow()][p.getCol()] = "*";
        keyLevelPosition = new Position(p.getRow(), p.getCol());
      }
      catch (ArrayIndexOutOfBoundsException e) {
        throw new ArrayIndexOutOfBoundsException("Given coordinate for key is beyond bounds of the room.");
      }
    }

    // adds the exit on the levelPlane based on the given position
    public void addExit(Position p) throws ArrayIndexOutOfBoundsException{
      try {
        this.levelPlane[p.getRow()][p.getCol()] = "●";
        exitLevelPosition = new Position(p.getRow(), p.getCol());
      }
      catch (ArrayIndexOutOfBoundsException e) {
        throw new ArrayIndexOutOfBoundsException("Given coordinate for exit is beyond bounds of the room.");
      }
    }

    // returns the position of the key tile in the level
    public Position getKeyPositionInLevel() {
      return this.keyLevelPosition;
    }

    // returns the position of the exit tile in the level
    public Position getExitPositionInLevel() {
      return this.exitLevelPosition;
    }

    // changes a closed exit tile to an open exit tile when the key is found by a player
    public void openExitTile() {
      Position exitPos = getExitPositionInLevel();
      levelPlane[exitPos.getRow()][exitPos.getCol()] = ("O");
    }

    // method that handles when a player successfully passes through the exit
    public void playerPassedThroughExit(ICharacter c) {
      this.levelPlane[c.getCharacterPosition().getRow()][c.getCharacterPosition().getCol()] = "O";
      this.getActivePlayers().remove(c);
    }

    // places new players on the board at the given location if valid (will check validity
    //    when we implement RuleChecker interface)
    public void addPlayer(Player player, Position placeLocation) {
        levelPlane[placeLocation.getRow()][placeLocation.getCol()] = player.getAvatar();
        player.setCharacterPosition(new Position(placeLocation.getRow(), placeLocation.getCol()));
        this.activePlayers.add(player);
    }

    // places new adversaries on the board at the given location if valid (will check validity
    //    when we implement RuleChecker interface)
    public void addAdversary(IAdversary a, Position placeLocation) {
        levelPlane[placeLocation.getRow()][placeLocation.getCol()] = a.getAvatar();
        a.setCharacterPosition(new Position(placeLocation.getRow(), placeLocation.getCol()));
        this.adversaries.add(a);
    }

    public Position pickRandomPositionForCharacterInLevel() {
      Random rand = new Random();
      int randomIndexRow = rand.nextInt(this.levelNumOfRows);
      int randomIndexCol = rand.nextInt(this.levelNumOfCols);
      String randomTile = this.levelPlane[randomIndexRow][randomIndexCol];

      while (randomTile.equals(" ") || randomTile.equals("G") || randomTile.equals("Z") || randomTile.equals("#")
              || randomTile.equals("*") || randomTile.equals("O") || randomTile.equals("@") || randomTile.equals("$")
              || randomTile.equals("¤") || randomTile.equals("~") || randomTile.equals("x")) {
        randomIndexRow = rand.nextInt(this.levelNumOfRows);
        randomIndexCol = rand.nextInt(this.levelNumOfCols);
        randomTile = this.levelPlane[randomIndexRow][randomIndexCol];
      }

      return new Position(randomIndexRow, randomIndexCol);
    }

    public void moveCharacter(ICharacter character, Position movePosition) {
      if (isInHallway(character)) {
        this.levelPlane[character.getCharacterPosition().getRow()][character.getCharacterPosition().getCol()] = "x";
      }
      else if (isOnADoor(character)) {
        this.levelPlane[character.getCharacterPosition().getRow()][character.getCharacterPosition().getCol()] = "|";
      }
      else if (character.getCharacterPosition().toString().equals(exitLevelPosition.toString())) {
        this.levelPlane[character.getCharacterPosition().getRow()][character.getCharacterPosition().getCol()] = "●";
      }
      else {
        this.levelPlane[character.getCharacterPosition().getRow()][character.getCharacterPosition().getCol()] = ".";
      }
      this.levelPlane[movePosition.getRow()][movePosition.getCol()] = character.getAvatar();
      character.setCharacterPosition(new Position(movePosition.getRow(), movePosition.getCol()));
    }

    private boolean isInHallway(ICharacter character) {
      Position charPos = character.getCharacterPosition();
      for (Hallway hallway : listOfHallwaysInLevel) {
        for (Position pos : hallway.getAllHallwayPositions()) {
          if (charPos.toString().equals(pos.toString())) {
            return true;
          }
        }
      }
      return false;
    }

    private boolean isOnADoor(ICharacter character) {
      String charPosStr = character.getCharacterPosition().toString();
      for (String doorPos : listOfDoorsInLevel.keySet()) {
        if (charPosStr.equals(doorPos)) {
          return true;
        }
      }
      return false;
    }

    public Player getPlayerObjectFromName(String name) {
      for (Player p: getActivePlayers()) {
        if (p.getName().equals(name)) {
          return p;
        }
      }
      return null;
    }

    public ArrayList<Room> startAndEndRooms(Hallway h) {
      ArrayList<Room> startAndEndRoomList = new ArrayList<Room>();
      Room startRoom = listOfDoorsInLevel.get(h.getStartPositionOfHallway().toString());
      Room endRoom = listOfDoorsInLevel.get(h.getEndPositionOfHallway().toString());
      startAndEndRoomList.add(startRoom);
      startAndEndRoomList.add(endRoom);
      return startAndEndRoomList;
    }

    // returns what kind of tile is at the given position
    public String getTileInLevel(Position tilePosition) {
      return levelPlane[tilePosition.getRow()][tilePosition.getCol()];
    }

    public Player playerAtGivenPosition(Position p) {
      for (Player player : this.activePlayers) {
        if (player.getCharacterPosition().toString().equals(p.toString())) {
          return player;
        }
      }
      return null;
    }

    public IAdversary adversaryAtGivenPosition(Position p) {
      for (IAdversary adversary : this.adversaries) {
        if (adversary.getCharacterPosition().toString().equals(p.toString())) {
          return adversary;
        }
      }
      return null;
    }

    // expels the given player from the level
    public void expelPlayer(Player p) {
      this.levelPlane[p.getCharacterPosition().getRow()][p.getCharacterPosition().getCol()] = adversaryAtGivenPosition(p.getCharacterPosition()).getAvatar();
      this.activePlayers.remove(p);
      p.setIsExpelled(true);
    }

    // draws the levelPlane
    public String renderLevel() {
      StringBuilder levelASCII = new StringBuilder();
      for (int i = 0; i < levelNumOfRows; i++) {
        for (int j = 0; j < levelNumOfCols; j++) {
          if (j == levelNumOfCols - 1) {
            levelASCII.append(levelPlane[i][j] + "\n");
          } else {
            levelASCII.append(levelPlane[i][j] + " ");
          }
        }
      }
      return levelASCII.toString();
    }

}


