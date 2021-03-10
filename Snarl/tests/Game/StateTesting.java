package Game;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;

public class StateTesting {
  static Level level1 = new Level();
  static Room room1 = new Room(new Position(0, 0), 8, 10);
  static Room room2 = new Room(new Position(15, 15), 10, 10);
  static Room room3 = new Room(new Position(4,17),4,6);
  static Room room4 = new Room(new Position(2, 25), 7, 11);
  static Room room5 = new Room(new Position(23, 2), 14, 7);
  static Room room6 = new Room(new Position(30,27), 7,7);
  static Hallway h1 = new Hallway(new Position(2, 9), new Position(4, 20));
  static Hallway h2 = new Hallway(new Position(7, 6), new Position(15, 15));
  static Hallway h3 = new Hallway(new Position(21, 15), new Position(23, 6));
  static Hallway h4 = new Hallway(new Position(4, 35), new Position(26, 2));
  static Hallway h5 = new Hallway(new Position(8, 29), new Position(30, 32));
  static Hallway h6 = new Hallway(new Position(7, 25), new Position(7,19));
  static Hallway h7 = new Hallway(new Position(28, 8), new Position(33, 27));
  static Hallway h8 = new Hallway(new Position(23, 24), new Position(30, 29));
  static Player p1 = new Player("Bob");
  static Player p2 = new Player("Rob");
  static Player p3 = new Player("Jane");
  static Player p4 = new Player("Alice");
  static Player p5 = new Player("hehe");
  static Player p6 = new Player("hoho");
  static Player p7 = new Player("lala");
  static Player p8 = new Player("booboo");
  static IAdversary ghost = new Ghost("Scary Ghost");
  static IAdversary ghosty = new Ghost("Weird Ghost");
  static ArrayList<Level> listOfLevels = new ArrayList<Level>();
 static GameManager gm;

  public static void main(String[] args) {
    createInitialGameBoard();
    System.out.println(level1.renderLevel());
    listOfLevels.add(level1);
    gm = new GameManager(listOfLevels);
    //register players and add them to the board
    gm.registerPlayer(p5.getName());
    gm.registerPlayer(p6.getName());
    gm.registerPlayer(p7.getName());
    gm.registerPlayer(p8.getName());

    //register adversaries and add them to the board
    //gm.registerAdversary(ghost.getName());
    //gm.registerAdversary(ghosty.getName());

    //start game
    //gm.startGame();

      testis2CardinalTilesAway();
    testGetGameStatusOfMove();

    testisOnLevelPlane();
    testIsValidMove();
    testRunRuleCheckerPlayer();
    testCallRuleCheckerPlayer();

  }

  @Test
  public static void testGetGameStatusOfMove() {
      System.out.println("p1's position: " + p1.getCharacterPosition());
    assertEquals(GameStatus.VALID, gm.getGameStatusOfMove(p1, new Position (5,6)));
  }

  @Test
  public static void testCallRuleCheckerPlayer() {
      assertEquals(GameStatus.VALID, gm.callRuleChecker(p1, new Position (5,6)));
  }

  @Test
  public static void testRunRuleCheckerPlayer() {
    RuleCheckerPlayer ruleCheckerp4 = new RuleCheckerPlayer(level1, p4);
    level1.addPlayer(p4, new Position (3,3));
    assertEquals(GameStatus.VALID, ruleCheckerp4.runRuleChecker((new Position(3,4))));
  }

  @Test public static void testis2CardinalTilesAway() {
      RuleCheckerPlayer ruleCheckerp1 = new RuleCheckerPlayer(level1, p1);
      level1.addPlayer(p1, new Position (5,5));
      level1.addPlayer(p1, new Position (5,5));
      assertEquals(false, ruleCheckerp1.is2CardinalTilesAway(new Position (0,0)));
  }

  @Test public static void testIsValidMove() {
    RuleCheckerPlayer ruleCheckerp2 = new RuleCheckerPlayer(level1, p2);
    level1.addPlayer(p2, new Position (5,7));
    assertEquals(true, ruleCheckerp2.isValidMove(new Position (5,8)));
  }

  @Test public static void testisOnLevelPlane() {
    RuleCheckerPlayer ruleCheckerp3 = new RuleCheckerPlayer(level1, p3);
    assertEquals(true, ruleCheckerp3.isOnLevelPlane(new Position(13,13)));
  }

  public static void createInitialGameBoard() {
    //Room 1
    room1.addDoor(new Position(2, 9));
    room1.addDoor(new Position(7, 6));

    //adding key and exit
    level1.addKey(new Position(5, 27));
    level1.addExit(new Position(17, 18));

    //Room 2
    room2.addDoor(new Position(0, 0));

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

    h1.addAWaypoint(new Position(2, 20));
    h1.connectHallwayWaypoints();

    h2.addAWaypoint(new Position(11, 6));
    h2.addAWaypoint(new Position(11, 15));
    h2.connectHallwayWaypoints();

    h3.addAWaypoint(new Position(21, 6));
    h3.connectHallwayWaypoints();

    h4.addAWaypoint(new Position(4, 37));
    h4.addAWaypoint(new Position(38, 37));
    h4.addAWaypoint(new Position(38, 0));
    h4.addAWaypoint(new Position(26, 0));
    h4.connectHallwayWaypoints();

    h5.addAWaypoint(new Position(25, 29));
    h5.addAWaypoint(new Position(25, 32));
    h5.connectHallwayWaypoints();

    h6.addAWaypoint(new Position(7, 24));
    h6.addAWaypoint(new Position(10, 24));
    h6.addAWaypoint(new Position(10, 19));
    h6.connectHallwayWaypoints();

    h7.addAWaypoint(new Position(28, 11));
    h7.addAWaypoint(new Position(33, 11));
    h7.connectHallwayWaypoints();

    h8.addAWaypoint(new Position(23, 27));
    h8.addAWaypoint(new Position(27, 27));
    h8.addAWaypoint(new Position(27, 29));
    h8.connectHallwayWaypoints();

    level1.addHallway(h1);
    level1.addHallway(h2);
    level1.addHallway(h3);
    level1.addHallway(h4);
    level1.addHallway(h5);
    level1.addHallway(h6);
    level1.addHallway(h7);
    level1.addHallway(h8);

  }

}
