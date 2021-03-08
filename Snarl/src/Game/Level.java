package Game;
import java.util.*;

public class Level {
    int levelNumOfRows;
    int levelNumOfCols;
    String[][] levelPlane;
    LinkedHashSet<Room> allRooms = new LinkedHashSet<Room>();
    Position keyLevelPosition;
    Position exitLevelPosition;
    Set<Player> players = new HashSet<Player>();
    Set<Player> activePlayers = new HashSet<Player>();
    Set<IAdversary> adversaries = new HashSet<IAdversary>();
    boolean playersWon;
    ArrayList<Position> listOfAllLevelPositions = new ArrayList<Position>();
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
          if (tile.equals("■") || tile.equals("|") || tile.equals("X")) {
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
    
    // gets the set of allRooms
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
    public Set<IAdversary> getAdversaries() {
      return this.adversaries;
    }

    // UNUSED IN THIS MILESTONE, WILL TEST AT LATER DATE
    public boolean getPlayersWon() {
      return this.playersWon;
    }

    

    // UNUSED IN THIS MILESTONE, WILL TEST AT LATER DATE
    public void setPlayersWon(boolean won) {
      this.playersWon = won;
    }

    // constructs the 2D String array of the level, populating all entries with '.'
    //    and constructs the set containing all level positions
    public void makeLevel() {
      for (int i = 0; i < this.levelNumOfRows; i++) {
        for (int j = 0; j < this.levelNumOfCols; j++) {
          this.levelPlane[i][j] = ".";
          Position tempPos = new Position(i, j);
          listOfAllLevelPositions.add(tempPos);
        }
      }
    }

    // adds the key on the levelPlane based on the given position
    public void addKey(Position p) throws ArrayIndexOutOfBoundsException {
      try {
        levelPlane[p.getRow()][p.getCol()] = "*";
        keyLevelPosition = new Position(p.getRow(), p.getCol());
      }
      catch (ArrayIndexOutOfBoundsException e) {
        throw new ArrayIndexOutOfBoundsException("Given coordinate for key is beyond bounds of the room.");
      }

    }

    // adds the exit on the levelPlane based on the given position
    public void addExit(Position p) throws ArrayIndexOutOfBoundsException{
      try{
        levelPlane[p.getRow()][p.getCol()] = "●";
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

    // places new players on the board at the given location if valid (will check validity
    //    when we implement RuleChecker interface)
    public void addPlayer(Player player, Position placeLocation) {
      if (isOccupiedByPlayer(placeLocation)) {
        // then do something
        System.out.println("Sorry, player is already in position (" + placeLocation.getRow() +
                ", " + placeLocation.getCol() + "). Try going somewhere else.");
      }
      else {
        levelPlane[placeLocation.getRow()][placeLocation.getCol()] = "P";
        player.setPlayerPosition(placeLocation);
        this.players.add(player);
        this.activePlayers.add(player);
      }
    }

    // places new adversaries on the board at the given location if valid (will check validity
    //    when we implement RuleChecker interface)
    public void addAdversary(IAdversary a, Position placeLocation) {
      if (isOccupiedByAdversary(placeLocation)) {
        // then do something
        System.out.println("Sorry, adversary is already in position (" + placeLocation.getRow() +
                ", " + placeLocation.getCol() + "). Try going somewhere else.");
      }
      levelPlane[placeLocation.getRow()][placeLocation.getCol()] = "A";
      a.setAdversaryPosition(placeLocation);
      this.adversaries.add(a);
    }

    public void moveCharacter(ICharacter character, Position movePosition) {
      if (character instanceof Player) {
        movePlayer((Player) character, movePosition);
      }
      else if (character instanceof IAdversary) {
        moveAdversary((IAdversary) character, movePosition);
      }
    }

    // Moves existing player to the given position if valid, will be checking validity in later milestone
    public void movePlayer(Player p, Position movePosition) {
      this.levelPlane[p.getPlayerPosition().getRow()][p.getPlayerPosition().getCol()] = "■";
      this.levelPlane[movePosition.getRow()][movePosition.getCol()] = "P";
      p.setPlayerPosition(movePosition);
    }

    // Moves an existing adversary to the given position if valid, will be checking validity in later milestone
    public void moveAdversary(IAdversary a, Position movePosition) {
      this.levelPlane[a.getAdversaryPosition().getRow()][a.getAdversaryPosition().getCol()] = "■";
      this.levelPlane[movePosition.getRow()][movePosition.getCol()] = "A";
      a.setAdversaryPosition(movePosition);
    }

    // checks if the given position on the levelPlane already has a player on it
    public boolean isOccupiedByPlayer(Position p) {
      if (levelPlane[p.getRow()][p.getCol()].equals("P")) {
        return true;
      }
      return false;
    }

    // checks if the given position on the levelPlane has an adversary on it
    public boolean isOccupiedByAdversary(Position p) {
      if (levelPlane[p.getRow()][p.getCol()].equals("A")) {
        return true;
      }
      return false;
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

    public boolean isTileTraversable(Position tile) {
      if (getTileInLevel(tile).equals(".") || getTileInLevel(tile).equals("#") ||
              getTileInLevel(tile).equals("P")) {
        return false;
      }
      return true;
    }
    
    public Player playerAtGivenPosition(Position p) {
      for (Player player : this.activePlayers) {
        if (player.getPlayerPosition().toString().equals(p.toString())) {
          return player;
        }
      }
      return null;
    }

    // expels the given player from the level
    public void expelPlayer(Player p) {
      this.levelPlane[p.getPlayerPosition().getRow()][p.getPlayerPosition().getCol()] = "A";
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


