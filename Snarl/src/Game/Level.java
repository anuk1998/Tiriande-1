package Game;
import java.util.*;

  public class Level {
    int levelNumOfRows;
    int levelNumOfCols;
    String[][] levelPlane;
    LinkedHashSet<Room> allRooms = new LinkedHashSet<Room>();
    boolean isKeyFound = false;
    Set<Player> players = new HashSet<Player>();
    Set<Player> activePlayers = new HashSet<Player>();
    Set<Adversary> adversaries = new HashSet<Adversary>();
    boolean playersWon;
    ArrayList<Position> listOfAllLevelPositions = new ArrayList<Position>();
    HashMap<Position, Room> listOfDoorsInLevel = new HashMap<Position, Room>();

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
            String tile = r.getTileInRoom(new Position(roomRowIndex, roomColIndex)); // may have to switch order
            levelPlane[i][j] = tile;
            this.allRooms.add(r);
            roomColIndex++;
          }
          roomRowIndex++;
        }
      }
      catch (ArrayIndexOutOfBoundsException e) {
        throw new ArrayIndexOutOfBoundsException("The given room dimensions are invalid.");
      }
    }

    // adds a hallway to the levelPlane 2D array
    public void addHallway(Hallway hallway) {
      for (Position hallwayPos : hallway.getAllHallwayPositions()) {
        this.levelPlane[hallwayPos.getRow()][hallwayPos.getCol()] = "X";
      }
    }

    public ArrayList<Position> getAllAdjacentTiles(Position pos, Room room) {
      ArrayList<Position> adjacentTiles = new ArrayList<Position>();
      int row = pos.getRow();
      int col = pos.getCol();
      int scaledRow = row - room.getRoomOriginInLevel().getRow();
      int scaledCol = col - room.getRoomOriginInLevel().getCol();

      if (scaledRow >= room.getNumOfRows() || scaledCol >= room.getNumOfCols()) {
        return null;
      }

      Position[] adjacentsToCheck = {
              new Position(row - 1, col),
              new Position(row, col - 1),
              new Position(row, col + 1),
              new Position(row + 1, col)};

      for (Position adjacentTile : adjacentsToCheck) {
        try {
          String tile = this.levelPlane[adjacentTile.getRow()][adjacentTile.getCol()];
          if (tile.equals("■") || tile.equals("|")) {
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
    public int getLevelWidth() {
      return this.levelNumOfRows;
    }

    // gets the levelNumOfCols field
    public int getLevelHeight() {
      return this.levelNumOfCols;
    }

    // gets the isKeyFound field
    public boolean getIsKeyFound() {
      return this.isKeyFound;
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
    public Set<Adversary> getAdversaries() {
      return this.adversaries;
    }

    // UNUSED IN THIS MILESTONE, WILL TEST AT LATER DATE
    public boolean getPlayersWon() {
      return this.playersWon;
    }

    // sets the isKeyFound field to whatever given boolean
    public void setIsKeyFound(boolean keyFound) {
      this.isKeyFound = keyFound;
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
          Position tempPos = new Position(i, j); // may need to revert this to (i, j)
          listOfAllLevelPositions.add(tempPos);
        }
      }
    }

    // returns the position of the key tile in the level
    public Position getKeyPositionInLevel() {
      for (int i = 0; i < levelNumOfRows; i++) {
        for (int j = 0; j < levelNumOfCols; j++) {
          if (levelPlane[i][j].equals("*")) {
            return new Position(i, j);
          }
        }
      }
      return null;
    }

    // returns the position of the exit tile in the level
    public Position getExitPositionInLevel() {
      for (int i = 0; i < levelNumOfRows; i++) {
        for (int j = 0; j < levelNumOfCols; j++) {
          if (levelPlane[i][j].equals("●") || levelPlane[i][j].equals("O")) {
            return new Position(i, j);
          }
        }
      }
      return null;
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
    public void addAdversary(Adversary a, Position placeLocation) {
      if (isOccupiedByAdversary(placeLocation)) {
        // then do something
        System.out.println("Sorry, adversary is already in position (" + placeLocation.getRow() +
                ", " + placeLocation.getCol() + "). Try going somewhere else.");
      }
      levelPlane[placeLocation.getRow()][placeLocation.getCol()] = "A";
      a.setAdversaryPosition(placeLocation);
      this.adversaries.add(a);
    }

    // Moves existing player to the given position if valid, will be checking validity in later milestone
    public void movePlayer(Player p, Position movePosition) {
      this.levelPlane[p.getPlayerPosition().getRow()][p.getPlayerPosition().getCol()] = "■";
      if (movePosition.equals(getKeyPositionInLevel())) {
        this.isKeyFound = true;
        openExitTile();
      }
      this.levelPlane[movePosition.getRow()][movePosition.getCol()] = "P";
      p.setPlayerPosition(movePosition);
    }

    // Moves an existing adversary to the given position if valid, will be checking validity in later milestone
    public void moveAdversary(Adversary a, Position movePosition) {
      this.levelPlane[a.getAdversaryPosition().getRow()][a.getAdversaryPosition().getCol()] = "■";
      if (movePosition.equals(getKeyPositionInLevel())) {
        this.isKeyFound = true;
        openExitTile();
      }
      this.levelPlane[movePosition.getRow()][movePosition.getCol()] = "A";
      a.setAdversaryPosition(movePosition);
    }

    // checks if the given position on the levelPlane already has a player on it
    public boolean isOccupiedByPlayer(Position p) {
      for (int i = 0; i < levelNumOfRows; i++) {
        for (int j = 0; j < levelNumOfCols; j++) {
          if (levelPlane[p.getRow()][p.getCol()].equals("P")) {
            return true;
          }
        }
      }
      return false;
    }

    // checks if the given position on the levelPlane has an adversary on it
    public boolean isOccupiedByAdversary(Position p) {
      for (int i = 0; i < levelNumOfRows; i++) {
        for (int j = 0; j < levelNumOfCols; j++) {
          if (levelPlane[p.getRow()][p.getCol()].equals("A")) {
            return true;
          }
        }
      }
      return false;
    }

    // returns what kind of tile is at the given position
    public String getTileInLevel(Position tilePosition) {
      return levelPlane[tilePosition.getRow()][tilePosition.getCol()];
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


