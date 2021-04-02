package Game;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import Observer.LocalObserver;

public class ObserverTesting {

  static Level level1 = new Level();
  static Room room1 = new Room(new Position(0, 0), 8, 10);
  static Room room2 = new Room(new Position(15, 15), 10, 10);
  static Room room3 = new Room(new Position(4, 17), 4, 6);
  static Room room4 = new Room(new Position(2, 25), 7, 11);
  static Room room5 = new Room(new Position(23, 2), 14, 7);
  static Room room6 = new Room(new Position(30, 27), 7, 7);


  public static void main(String[]args) {
    createInitialGameBoard();
    //Testing the whole game with Scanner

    //ArrayList<Level> listOfLevels = new ArrayList<>();
    //listOfLevels.add(level1);
    //System.out.println(level1.renderLevel());
    //GameManager gm = new GameManager(listOfLevels);
    //gm.runGame()

    //Individual method testing

    //testGetActivePlayers();
    //testGetAdversaries();
    //testGetExpelledPlayers();
    //testGetExitedPlayers();
    //testGetMoveStatusUpdate();
  }

  @Test
  public static void testGetActivePlayers() {
    LocalObserver testObserver = new LocalObserver("foo");
    String output = "Active Players: ";
    ArrayList<String> outputArray = new ArrayList<>();
    outputArray.add("Hermione Granger");
    outputArray.add("Giselle");
    outputArray.add("Harry Potter");
    outputArray.add("Anu");
    assertEquals(output + outputArray, testObserver.constructListOfActivePlayersUpdate(level1));
  }

  @Test
  public static void testGetAdversaries() {
    LocalObserver testObserver = new LocalObserver("foo");
    String output = "Adversaries: ";
    ArrayList<String> outputArray = new ArrayList<>();
    outputArray.add("supermeanie");
    outputArray.add("meanie");
    assertEquals(output + outputArray, testObserver.constructListOfAdversariesUpdate(level1));
  }

  @Test
  public static void testGetExpelledPlayers() {
    LocalObserver testObserver = new LocalObserver("foo");

    String output = "Expelled Players: ";
    ArrayList<String> outputArray = new ArrayList<>();
    outputArray.add("Harry Potter");

    ArrayList<Player> paramArray = new ArrayList<>();
    paramArray.add(level1.getPlayerObjectFromName("Harry Potter"));

    assertEquals(output + outputArray, testObserver.constructListOfExpelledPlayersUpdate(paramArray));

  }

  @Test
  public static void testGetMoveStatusUpdate() {
    LocalObserver testObserver = new LocalObserver("foo");
    ICharacter currentCharacter = level1.getPlayerObjectFromName("Anu");
    Position move = new Position(4,5);
    GameStatus moveStatus = GameStatus.VALID;
    assertEquals("Player Anu made a move to position [4,5], with a move status of VALID", testObserver.constructMoveStatusUpdate(currentCharacter, move, moveStatus));
  }

  @Test
  public static void testGetExitedPlayers() {
    LocalObserver testObserver = new LocalObserver("foo");

    String output = "Exited Players: ";
    ArrayList<String> outputArray = new ArrayList<>();
    outputArray.add("Giselle");

    ArrayList<Player> paramArray = new ArrayList<>();
    paramArray.add(level1.getPlayerObjectFromName("Giselle"));

    assertEquals(output + outputArray, testObserver.constructListOfExitedPlayersUpdate(paramArray));

  }

  public static void createInitialGameBoard() {
    //Room 1
    room1.addDoor(new Position(2, 9));
    room1.addDoor(new Position(7, 6));

    //Room 2
    room2.addDoor(new Position(0, 2));
    room2.addDoor(new Position(6, 0));
    room2.addDoor(new Position(8, 9));

    //Room 3
    room3.addDoor(new Position(0, 3));
    room3.addDoor(new Position(3, 2));

    //Room 4
    room4.addDoor(new Position(5, 0));
    room4.addDoor(new Position(2, 10));
    room4.addDoor(new Position(6, 4));

    //Room 5
    room5.addDoor(new Position(3, 0));
    room5.addDoor(new Position(0, 4));
    room5.addDoor(new Position(5, 6));

    //Room 6
    room6.addDoor(new Position(0, 5));
    room6.addDoor(new Position(3, 0));
    room6.addDoor(new Position(0, 2));

    //Add rooms to level
    level1.addRoom(room1);
    level1.addRoom(room2);
    level1.addRoom(room3);
    level1.addRoom(room4);
    level1.addRoom(room5);
    level1.addRoom(room6);


    ArrayList<Position> h1Waypoints = new ArrayList<>();
    h1Waypoints.add(new Position(2, 20));
    Hallway h1 = new Hallway(new Position(2, 9), new Position(4, 20), h1Waypoints);

    ArrayList<Position> h2Waypoints = new ArrayList<>();
    h2Waypoints.add(new Position(11, 6));
    h2Waypoints.add(new Position(11, 15));
    h2Waypoints.add(new Position(14, 15));
    h2Waypoints.add(new Position(14, 17));
    Hallway h2 = new Hallway(new Position(7, 6), new Position(15, 17), h2Waypoints);

    ArrayList<Position> h3Waypoints = new ArrayList<>();
    h3Waypoints.add(new Position(21, 6));
    Hallway h3 = new Hallway(new Position(21, 15), new Position(23, 6), h3Waypoints);

    ArrayList<Position> h4Waypoints = new ArrayList<>();
    h4Waypoints.add(new Position(4, 37));
    h4Waypoints.add(new Position(38, 37));
    h4Waypoints.add(new Position(38, 0));
    h4Waypoints.add(new Position(26, 0));

    Hallway h4 = new Hallway(new Position(4, 35), new Position(26, 2), h4Waypoints);

    ArrayList<Position> h5Waypoints = new ArrayList<>();
    h5Waypoints.add(new Position(25, 29));
    h5Waypoints.add(new Position(25, 32));
    Hallway h5 = new Hallway(new Position(8, 29), new Position(30, 32), h5Waypoints);

    ArrayList<Position> h6Waypoints = new ArrayList<>();
    h6Waypoints.add(new Position(7, 24));
    h6Waypoints.add(new Position(10, 24));
    h6Waypoints.add(new Position(10, 19));
    Hallway h6 = new Hallway(new Position(7, 25), new Position(7, 19), h6Waypoints);

    ArrayList<Position> h7Waypoints = new ArrayList<>();
    h7Waypoints.add(new Position(28, 11));
    h7Waypoints.add(new Position(33, 11));
    Hallway h7 = new Hallway(new Position(28, 8), new Position(33, 27), h7Waypoints);

    ArrayList<Position> h8Waypoints = new ArrayList<>();
    h8Waypoints.add(new Position(23, 27));
    h8Waypoints.add(new Position(27, 27));
    h8Waypoints.add(new Position(27, 29));
    Hallway h8 = new Hallway(new Position(23, 24), new Position(30, 29), h8Waypoints);

    level1.addHallway(h1);
    level1.addHallway(h2);
    level1.addHallway(h3);
    level1.addHallway(h4);
    level1.addHallway(h5);
    level1.addHallway(h6);
    level1.addHallway(h7);
    level1.addHallway(h8);

    //adding key and exit// joo
    level1.addObject(new Position(5, 27), "*");
    level1.addObject(new Position(6, 27), "●");

    //initialize gameManager
    ArrayList<Level> listOfLevels = new ArrayList<>();
    listOfLevels.add(level1);
    GameManager gm = new GameManager(listOfLevels, 0);

    //register players for observer testing purposes
    gm.registerPlayer("Anu");
    gm.registerPlayer("Giselle");
    gm.registerPlayer("Harry Potter");
    gm.registerPlayer("Hermione Granger");

    //register adversaries for observer testing purposes
    gm.registerAdversary("meanie", "ghost");
    gm.registerAdversary("supermeanie", "zombie");

  }


}
