package Game;

import java.util.*;

public class Level {
  int levelNumOfRows;
  int levelNumOfCols;
  String[][] levelPlane;
  LinkedHashSet<Room> allRooms = new LinkedHashSet<Room>();
  Position keyLevelPosition;
  Position exitLevelPosition;
  boolean exitLocked = true;
  boolean keyFound = false;
  int numOfPlayers = 0;
  boolean isLastLevelOfGame = false;
  Set<Player> activePlayers = new HashSet<Player>();
  ArrayList<Player> exitedPlayers = new ArrayList<>();
  ArrayList<Player> expelledPlayers = new ArrayList<>();
  Set<IAdversary> adversaries = new HashSet<IAdversary>();
  HashMap<String, Room> listOfDoorsInLevel = new HashMap<String, Room>();
  Set<Hallway> listOfHallwaysInLevel = new HashSet<>();
  HashMap<String, ArrayList<Hallway>> roomsAndTheirHallways = new HashMap<String, ArrayList<Hallway>>();
  HashMap<String, Room> positionsAndTheirRooms = new HashMap<>();

  // Enum constants for actor and tile representations
  String VOID = TileType.VOID.toString();
  String KEY = TileType.KEY.toString();
  String HALLWAY = TileType.HALLWAY.toString();
  String ROOM = TileType.ROOM.toString();
  String UNLOCKED_EXIT = TileType.UNLOCKED_EXIT.toString();
  String LOCKED_EXIT = TileType.LOCKED_EXIT.toString();
  String DOOR = TileType.DOOR.toString();

  public Level() {
    levelNumOfRows = 40;
    levelNumOfCols = 40;
    levelPlane = new String[levelNumOfRows][levelNumOfCols];
    makeLevel();
  }

  /**
   *
   * Methods that construct the Level.
   *
   */

  // Constructs the 2D String array of the level, populating all entries with '.'
  //    and constructs the set containing all level positions
  public void makeLevel() {
    for (int i = 0; i < this.levelNumOfRows; i++) {
      for (int j = 0; j < this.levelNumOfCols; j++) {
        this.levelPlane[i][j] = VOID;
      }
    }
  }

  // Adds a Room to the levelPlane 2D array
  public void addRoom(Room r) throws ArrayIndexOutOfBoundsException {
    try {
      for (Position posInRoom : r.getListOfAllPositions()) {
        int scaledLevelRow = posInRoom.getRow() + r.getRoomOriginInLevel().getRow();
        int scaledLevelCol = posInRoom.getCol() + r.getRoomOriginInLevel().getCol();

        r.addToListOfAllPositionsLevelScale(new Position(scaledLevelRow, scaledLevelCol));
        // if position is a door, add it to map of doors in level
        if (r.getDoorPositions().contains(posInRoom)) {
          this.listOfDoorsInLevel.put(new Position(scaledLevelRow, scaledLevelCol).toString(), r);
        }
        // add tile to the level board
        this.levelPlane[scaledLevelRow][scaledLevelCol] = r.getTileInRoom(posInRoom);
        // add position to map of positions and rooms
        this.positionsAndTheirRooms.put(new Position(scaledLevelRow, scaledLevelCol).toString(), r);
      }
      this.allRooms.add(r);
      this.roomsAndTheirHallways.put(r.getRoomOriginInLevel().toString(), new ArrayList<>());
    }
    catch (ArrayIndexOutOfBoundsException e) {
      throw new ArrayIndexOutOfBoundsException("The given room dimensions are invalid.");
    }
  }

  // Adds a hallway to the levelPlane 2D array
  public void addHallway(Hallway hallway) {
    listOfHallwaysInLevel.add(hallway);
    for (Position hallwayPos : hallway.getAllHallwayPositions()) {
      this.levelPlane[hallwayPos.getRow()][hallwayPos.getCol()] = HALLWAY;
    }
    // This part only relevant for testing harness
    // adds that hallway to its room's list of connected hallways
    for (Room r : startAndEndRooms(hallway)) {
      this.roomsAndTheirHallways.get(r.getRoomOriginInLevel().toString()).add(hallway);
    }
  }

  // Adds an object to the level depending on whether it is a key or exit
  public void addObject(Position p, String symbol) {
    try {
      this.levelPlane[p.getRow()][p.getCol()] = symbol;
      if (symbol.equals(KEY)) {
        keyLevelPosition = new Position(p.getRow(), p.getCol());
      } else {
        exitLevelPosition = new Position(p.getRow(), p.getCol());
      }
    }
    catch (ArrayIndexOutOfBoundsException e) {
      throw new ArrayIndexOutOfBoundsException("Given coordinate for object is beyond bounds of the level.");
    }
  }

  // Places a new character on the board at the given location
  public void addCharacter(ICharacter character, Position placeLocation) {
    levelPlane[placeLocation.getRow()][placeLocation.getCol()] = character.getAvatar();
    character.setCharacterPosition(new Position(placeLocation.getRow(), placeLocation.getCol()));
    if (character instanceof Player) {
      this.activePlayers.add((Player) character);
      this.numOfPlayers++;
    }
    else if (character instanceof IAdversary) {
      this.adversaries.add((IAdversary) character);
      if (character instanceof Zombie) {
        Zombie zombie = (Zombie) character;
        zombie.setZombiesRoom(getBelongingRoom(placeLocation));
      }
    }
  }

  // Generates a random valid position for characters to start on when registered to the game
  public Position pickRandomPositionForCharacterInLevel() {
    Random rand = new Random();
    int randomIndexRow = rand.nextInt(this.levelNumOfRows);
    int randomIndexCol = rand.nextInt(this.levelNumOfCols);
    String randomTile = this.levelPlane[randomIndexRow][randomIndexCol];

    while (!randomTile.equals(ROOM)) {
      randomIndexRow = rand.nextInt(this.levelNumOfRows);
      randomIndexCol = rand.nextInt(this.levelNumOfCols);
      randomTile = this.levelPlane[randomIndexRow][randomIndexCol];
    }
    return new Position(randomIndexRow, randomIndexCol);
  }

  // Draws the levelPlane
  public String renderLevel() {
    StringBuilder levelASCII = new StringBuilder();
    for (int i = 0; i < levelNumOfRows; i++) {
      for (int j = 0; j < levelNumOfCols; j++) {
        if (j == levelNumOfCols - 1) {
          levelASCII.append(levelPlane[i][j]).append("\n");
        } else {
          levelASCII.append(levelPlane[i][j]).append(VOID);
        }
      }
    }
    return levelASCII.toString();
  }

  /**
   *
   * Methods that change the game state.
   *
   */

  // Moves the given character to the given destination on the level plane
  public void moveCharacter(ICharacter character, Position movePosition) {
    restoreCharacterTile(character);
    this.levelPlane[movePosition.getRow()][movePosition.getCol()] = character.getAvatar();
    character.setCharacterPosition(new Position(movePosition.getRow(), movePosition.getCol()));
  }

  // When moving or eliminating a character, this method converts the character's current position back
  //  to the tile type that it was before the character moved onto it
  public void restoreCharacterTile(ICharacter character) {
    Position charPos = character.getCharacterPosition();
    int charRow = charPos.getRow();
    int charCol = charPos.getCol();
    if (charPos.toString().equals(keyLevelPosition.toString())) {
      if (character instanceof IAdversary && !keyFound) {
        this.levelPlane[charRow][charCol] = KEY;
      }
      else {
        this.levelPlane[charRow][charCol] = ROOM;
      }
    }
    else if (isInHallway(character)) {
      this.levelPlane[charRow][charCol] = HALLWAY;
    }
    else if (isOnADoor(character)) {
      this.levelPlane[charRow][charCol] = DOOR;
    }
    else if (charPos.toString().equals(exitLevelPosition.toString())) {
      if (exitLocked) this.levelPlane[charRow][charCol] = LOCKED_EXIT;
      else this.levelPlane[charRow][charCol] = UNLOCKED_EXIT;
    }
    else {
      this.levelPlane[charRow][charCol] = ROOM;
    }
  }

  // Helper method for moveCharacter, checks if character is in a hallway
  private boolean isInHallway(ICharacter character) {
    Position charPos = character.getCharacterPosition();
    for (Hallway hallway : listOfHallwaysInLevel) {
      if (hallway.getAllHallwayPositions().contains(charPos)) {
        return true;
      }
    }
    return false;
  }

  // Helper method for moveCharacter, checks if character is on a door
  private boolean isOnADoor(ICharacter character) {
    String charPosStr = character.getCharacterPosition().toString();
    for (String doorPos : listOfDoorsInLevel.keySet()) {
      if (charPosStr.equals(doorPos)) {
        return true;
      }
    }
    return false;
  }

  // Expels the given player from the level
  public void expelPlayer(Player p) {
    System.out.println("TO REMOVE: we are in expel player");
    this.activePlayers.remove(p);
    this.expelledPlayers.add(p);
    System.out.println("TO REMOVE: expelled players so far: " + this.expelledPlayers.toString());
    p.setIsExpelled(true);
  }

  // Helper method for expelPlayer when called in GameManager, returns the player at a given position
  // for the case when a player is expelled by an adversary on an adversary move
  public Player playerAtGivenPosition(Position p) {
    for (Player player : this.activePlayers) {
      if (player.getCharacterPosition().toString().equals(p.toString())) {
        return player;
      }
    }
    return null;
  }

  // Returns an adversary at the given position
  public IAdversary adversaryAtGivenPosition(Position p) {
    for (IAdversary adversary : adversaries) {
      if (adversary.getCharacterPosition().toString().equals(p.toString())) {
        return adversary;
      }
    }
    return null;
  }

  // Changes a closed exit tile to an open exit tile when the key is found by a player
  public void openExitTile() {
    keyFound = true;
    exitLocked = false;
    int exitRowPos = exitLevelPosition.getRow();
    int exitColPos = exitLevelPosition.getCol();
    if (playerAtGivenPosition(exitLevelPosition) == null) {
      levelPlane[exitRowPos][exitColPos] = UNLOCKED_EXIT;
    }
  }

  // Method that handles when a player successfully passes through the exit by removing them from active players list
  public void playerLeavesTheLevel(ICharacter c) {
    System.out.println("TO REMOVE: we are in expel player");
    this.activePlayers.remove(c);
    this.exitedPlayers.add((Player)c);
    System.out.println("TO REMOVE: exited players so far: " + this.exitedPlayers.toString());
  }

  /**
   *
   * Getter methods for Level fields.
   *
   */

  // Returns the number of rows there are in the level board
  public int getLevelNumOfRows() {
    return this.levelNumOfRows;
  }

  // Returns the number of columns there are in the level board
  public int getLevelNumOfCols() {
    return this.levelNumOfCols;
  }

  // Returns a list of all currently active players in this Level
  public Set<Player> getActivePlayers() {
    return this.activePlayers;
  }

  // Returns the list of adversaries in this Level
  public Set<IAdversary> getAdversaries() {
    return this.adversaries;
  }

  // Returns the position of the key tile in the level
  public Position getKeyPositionInLevel() {
    return this.keyLevelPosition;
  }

  // Returns the position of the exit tile in the level
  public Position getExitPositionInLevel() {
    return this.exitLevelPosition;
  }

  // Returns if the level exit is locked or not
  public boolean getExitLocked() {
    return this.exitLocked;
  }

  public boolean getKeyFound() {
    return this.keyFound;
  }

  public ArrayList<String> getListOfDoorPositions() {
    return new ArrayList<String>(this.listOfDoorsInLevel.keySet());
  }

  public boolean getIsLastLevelOfGame() {
    return this.isLastLevelOfGame;
  }

  public void setIsLastLevelOfGame(boolean isLastLevel) {
    this.isLastLevelOfGame = isLastLevel;
  }

  public int getNumOfPlayers() {
    return this.numOfPlayers;
  }

  public ArrayList<Player> getExpelledPlayers() {
    return this.expelledPlayers;
  }

  public ArrayList<Player> getExitedPlayers() {
    return this.exitedPlayers;
  }



  /**
   *
   * Helpful methods to retrieve information about the Level and its components.
   *
   */

  // Returns what kind of tile is at the given position
  public String getTileInLevel(Position tilePosition) {
    return levelPlane[tilePosition.getRow()][tilePosition.getCol()];
  }

  // Returns a list of tiles that are immediately adjacent to the given position (left, right, up, and down)
  //  (A room object is given optionally for the testRoom testing harness, otherwise, no room is given)
  public ArrayList<Position> getAllAdjacentTiles(Position pos) {
    int row = pos.getRow();
    int col = pos.getCol();

    ArrayList<Position> adjacentTiles = new ArrayList<>(
            Arrays.asList(
                    new Position(row - 1, col), new Position(row, col - 1),
                    new Position(row + 1, col), new Position(row, col + 1)));

    ArrayList<Position> validAdjacentTiles = new ArrayList<>();
    for (Position adjacentTile : adjacentTiles) {
      try {
        // 'tile' is dummy value that is just here to ensure that the adjacent tile is within the bounds of the level
        String tile = this.levelPlane[adjacentTile.getRow()][adjacentTile.getCol()];
        validAdjacentTiles.add(adjacentTile);
      }
      catch (ArrayIndexOutOfBoundsException ignored) {
      }
    }

    return validAdjacentTiles;
  }

  /**
   *
   * Testing harness-specific methods.
   *
   */

  // helper method for testing harness (and used in addHallway), returns the start and end room objects of a hallway
  public ArrayList<Room> startAndEndRooms(Hallway h) {
    return new ArrayList<>(
            Arrays.asList(listOfDoorsInLevel.get(h.getStartPositionOfHallway().toString()),
                    listOfDoorsInLevel.get(h.getEndPositionOfHallway().toString())));
  }

  // Returns the Room that the given point belongs to, if it's not in a room, returns null
  public Room getBelongingRoom(Position p) {
    try {
      return this.positionsAndTheirRooms.get(p.toString());
    }
    catch (Exception e) {
      return null;
    }
  }

  // Returns a list of Hallways connected to the given room
  public ArrayList<Hallway> getConnectedHallways(Room r) {
    return this.roomsAndTheirHallways.get(r.getRoomOriginInLevel().toString());
  }

  // returns the Hallway that the given point belongs to
  public Hallway getHallwayFromPoint(Position p) {
    for (Hallway h : this.listOfHallwaysInLevel) {
      if (h.getAllHallwayPositions().contains(p)) {
        return h;
      }
    }
    return null;
  }

  // Returns the Player object based on the given unique name
  public Player getPlayerObjectFromName(String name) {
    for (Player p: getActivePlayers()) {
      if (p.getName().equals(name)) {
        return p;
      }
    }
    return null;
  }

  // Returns the Adversary object based on the given unique name
  public IAdversary getAdversaryObjectFromName(String name) {
    for (IAdversary a: getAdversaries()) {
      if (a.getName().equals(name)) {
        return a;
      }
    }
    return null;
  }
}


