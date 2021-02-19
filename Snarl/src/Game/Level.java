package Game;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

  public class Level {
    int levelWidth;
    int levelHeight;
    String[][] levelPlane;
    LinkedHashSet<Room> allRooms = new LinkedHashSet<Room>();
    boolean isKeyFound = false;
    Set<Player> players = new HashSet<Player>();
    Set<Player> activePlayers = new HashSet<Player>();
    Set<Adversary> adversaries = new HashSet<Adversary>();
    boolean playersWon;
    ArrayList<Position> listOfAllLevelPositions = new ArrayList<Position>();
    HashMap<Position, Room> listOfDoorsInLevel = new HashMap();

    public Level() {
      levelWidth = 40;
      levelHeight = 40;
      levelPlane = new String[levelWidth][levelHeight];
      makeLevel();
    }

    // adds a Room to the levelPlane 2D array
    public void addRoom(Room r) throws ArrayIndexOutOfBoundsException {
      try {
        int roomWidth = r.getHorizontalLength();
        int roomHeight = r.getVerticalLength();
        int startPosX = r.getRoomStartPositionInLevel().getX();
        int startPosY = r.getRoomStartPositionInLevel().getY();

        int roomWidthIndex = 0;
        for (int i = startPosX; i < startPosX + roomHeight; i++) {
          int roomHeightIndex = 0;
          for (int j = startPosY; j < startPosY + roomWidth; j++) {
            String tile = r.getTileInRoom(new Position(roomHeightIndex, roomWidthIndex)); // may have to switch order
            levelPlane[i][j] = tile;
            this.allRooms.add(r);
            roomHeightIndex++;
          }
          roomWidthIndex++;
        }
      }
      catch (ArrayIndexOutOfBoundsException e) {
        throw new ArrayIndexOutOfBoundsException("The given room dimensions are invalid.");
      }
    }

    // adds a hallway to the levelPlane 2D array
    public void addHallway(Hallway hallway) {
      for (Position hallwayPos : hallway.getAllHallwayPositions()) {
        this.levelPlane[hallwayPos.getX()][hallwayPos.getY()] = "X";
      }

    }

    // gets the levelWidth field
    public int getLevelWidth() {
      return this.levelWidth;
    }

    // gets the levelHeight field
    public int getLevelHeight() {
      return this.levelHeight;
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
      for (int i = 0; i < this.levelHeight; i++) {
        for (int j = 0; j < this.levelWidth; j++) {
          this.levelPlane[i][j] = ".";
          Position tempPos = new Position(j, i); // may need to revert this to (i, j)
          listOfAllLevelPositions.add(tempPos);
        }
      }
    }

    // returns the position of the key tile in the level
    public Position getKeyPositionInLevel() {
      for (int i = 0; i < levelHeight; i++) {
        for (int j = 0; j < levelWidth; j++) {
          if (levelPlane[i][j].equals("*")) {
            return new Position(j, i);
          }
        }
      }
      return null;
    }

    // returns the position of the exit tile in the level
    public Position getExitPositionInLevel() {
      for (int i = 0; i < levelHeight; i++) {
        for (int j = 0; j < levelWidth; j++) {
          if (levelPlane[i][j].equals("●") || levelPlane[i][j].equals("O")) {
            return new Position(j, i);
          }
        }
      }
      return null;
    }

    // changes a closed exit tile to an open exit tile when the key is found by a player
    public void openExitTile() {
      Position exitPos = getExitPositionInLevel();
      levelPlane[exitPos.getX()][exitPos.getY()] = ("O");
    }

    // places new players on the board at the given location if valid (will check validity
    //    when we implement RuleChecker interface)
    public void addPlayer(Player player, Position placeLocation) {
      if (isOccupiedByPlayer(placeLocation)) {
        // then do something
        System.out.println("Sorry, player is already in position (" + placeLocation.getX() +
                ", " + placeLocation.getY() + "). Try going somewhere else.");
      }
      else {
        levelPlane[placeLocation.getX()][placeLocation.getY()] = "P";
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
        System.out.println("Sorry, adversary is already in position (" + placeLocation.getX() +
                ", " + placeLocation.getY() + "). Try going somewhere else.");
      }
      levelPlane[placeLocation.getX()][placeLocation.getY()] = "A";
      a.setAdversaryPosition(placeLocation);
      this.adversaries.add(a);
    }

    // Moves existing player to the given position if valid, will be checking validity in later milestone
    public void movePlayer(Player p, Position movePosition) {
      this.levelPlane[p.getPlayerPosition().getX()][p.getPlayerPosition().getY()] = "■";
      if (movePosition.equals(getKeyPositionInLevel())) {
        this.isKeyFound = true;
        openExitTile();
      }
      this.levelPlane[movePosition.getX()][movePosition.getY()] = "P";
      p.setPlayerPosition(movePosition);
    }

    // Moves an existing adversary to the given position if valid, will be checking validity in later milestone
    public void moveAdversary(Adversary a, Position movePosition) {
      this.levelPlane[a.getAdversaryPosition().getX()][a.getAdversaryPosition().getY()] = "■";
      if (movePosition.equals(getKeyPositionInLevel())) {
        this.isKeyFound = true;
        openExitTile();
      }
      this.levelPlane[movePosition.getX()][movePosition.getY()] = "A";
      a.setAdversaryPosition(movePosition);
    }

    // checks if the given position on the levelPlane already has a player on it
    public boolean isOccupiedByPlayer(Position p) {
      for (int i = 0; i < levelHeight; i++) {
        for (int j = 0; j < levelWidth; j++) {
          if (levelPlane[p.getX()][p.getY()].equals("P")) {
            return true;
          }
        }
      }
      return false;
    }

    // checks if the given position on the levelPlane has an adversary on it
    public boolean isOccupiedByAdversary(Position p) {
      for (int i = 0; i < levelHeight; i++) {
        for (int j = 0; j < levelWidth; j++) {
          if (levelPlane[p.getX()][p.getY()].equals("A")) {
            return true;
          }
        }
      }
      return false;
    }

    public String getTileInLevel(Position tilePosition) {
      return levelPlane[tilePosition.getX()][tilePosition.getY()];
    }

    // expels the given player from the level -- unused in this milestone
    public void expelPlayer(Player p) {
      this.activePlayers.remove(p);
      p.setIsExpelled(true);
    }

    // draws the levelPlane
    public String renderLevel() {
      StringBuilder levelASCII = new StringBuilder();
      for (int i = 0; i < levelHeight; i++) {
        for (int j = 0; j < levelWidth; j++) {
          if (j == levelHeight - 1) {
            levelASCII.append(levelPlane[i][j] + "\n");
          } else {
            levelASCII.append(levelPlane[i][j] + " ");
          }
        }
      }
      return levelASCII.toString();
    }

  }


