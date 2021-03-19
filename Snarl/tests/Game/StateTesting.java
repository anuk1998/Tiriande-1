package Game;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class StateTesting {
  static Level level1 = new Level();
  static Room room1 = new Room(new Position(0, 0), 8, 10);
  static Room room2 = new Room(new Position(15, 15), 10, 10);
  static Room room3 = new Room(new Position(4,17),4,6);
  static Room room4 = new Room(new Position(2, 25), 7, 11);
  static Room room5 = new Room(new Position(23, 2), 14, 7);
  static Room room6 = new Room(new Position(30,27), 7,7);
  static Player p1 = new Player("Bob");
  static IAdversary ghost = new Ghost("Scary Ghost");
  static IAdversary zombie = new Ghost("Weird Zombie");
  static Level[] lev = {level1};
  static ArrayList<Level> listOfLevels = new ArrayList<Level>(Arrays.asList(lev));
  static GameManager gm = new GameManager(listOfLevels);

  public static void main(String[] args) {
    listOfLevels.add(level1);
    createInitialGameBoard();

    //testIsLastLevel();
    //testParseMoveDoAction();
    //testRegisterAdversary();
    //testRegisterPlayer();
    //testis2CardinalTilesAway();
    //testisOnLevelPlane();
    //testIsValidMove();
    //testRunRuleCheckerPlayer();
    //testCallRuleCheckerPlayer();
    //testCheckPlayerActiveStatus();
    //testIsExitUnlocked();
    //testKeyTileIsLandedOnAndExitLandedOnAfter();
    //testEncountersOppositeCharacter();
  }

  @Test
  public static void testParseMoveDoAction() {
    assertEquals(true, gm.parseMoveStatusAndDoAction(GameStatus.INVALID, new Position(2000, 2), level1.getPlayerObjectFromName("Carl") ));
  }

  @Test
  public static void testIsExitUnlocked() {
    RuleCheckerPlayer exitUnlocked = new RuleCheckerPlayer(level1, p1);
    assertEquals(false, exitUnlocked.isExitUnlocked() );
  }

  @Test
  public static void testIsLastLevel() {
    RuleCheckerPlayer rcp = new RuleCheckerPlayer(level1, level1.getPlayerObjectFromName("Rob"));
    assertEquals(true, rcp.isLastLevel());
  }

  @Test 
  public static void testCheckPlayerActiveStatus() {
    Player bob = level1.getPlayerObjectFromName("Bob");
    // checking active status before Player is expelled
    assertEquals(true, gm.checkPlayerActiveStatus(bob));
    // checking active status after Player has been expelled
    IAdversary evil = new Ghost("evil");
    level1.addAdversary(evil, new Position(bob.getCharacterPosition().getRow() - 1, bob.getCharacterPosition().getCol()));
    gm.parseMoveStatusAndDoAction(GameStatus.PLAYER_SELF_ELIMINATES, evil.getCharacterPosition(), bob);
    assertEquals(false, gm.checkPlayerActiveStatus(bob));
  }

  @Test
  public static void testEncountersOppositeCharacter() {
    Player selfEliminator = new Player("selfEliminator455");
    level1.addPlayer(selfEliminator, new Position(4,4));
    IAdversary boo = new Ghost("boo!");
    level1.addAdversary(boo, new Position(4,5));
    level1.moveCharacter(selfEliminator, boo.getCharacterPosition());
    RuleCheckerPlayer selfElimRCP = new RuleCheckerPlayer(level1, selfEliminator);
    assertEquals(GameStatus.PLAYER_SELF_ELIMINATES, selfElimRCP.encountersOppositeCharacter());
  }

  @Test
  public static void testRegisterPlayer() {
    assertEquals(3, level1.activePlayers.size());
  }

  @Test
  public static void testRegisterAdversary() {
    assertEquals(2, level1.getAdversaries().size());
  }

  @Test
  public static void testCallRuleCheckerPlayer() {
    Player bob = level1.getPlayerObjectFromName("Bob");
    bob.setCharacterPosition(new Position(4, 2));
    assertEquals(GameStatus.VALID, gm.callRuleChecker(bob, new Position(4,3)));
    assertEquals(GameStatus.INVALID, gm.callRuleChecker(level1.getPlayerObjectFromName("Santiago"), new Position(100,100)));
  }

  @Test
  public static void testRunRuleCheckerPlayer() {
    Player ruleChecker = new Player("ruleChecker444");
    RuleCheckerPlayer ruleChecker444 = new RuleCheckerPlayer(level1, ruleChecker);
    level1.addPlayer(ruleChecker, new Position (3,3));
    assertEquals(GameStatus.VALID, ruleChecker444.runRuleChecker(new Position(3,4)));
  }

  @Test public static void testis2CardinalTilesAway() {
      RuleCheckerPlayer ruleCheckerp1 = new RuleCheckerPlayer(level1, level1.getPlayerObjectFromName("Bob"));
      level1.addPlayer(level1.getPlayerObjectFromName("Bob"), new Position (5,5));
      assertEquals(false, ruleCheckerp1.is2CardinalTilesAway(new Position (0,0)));
  }

  @Test public static void testIsValidMove() {
    Player validMover = new Player("ValidMover223");
    RuleCheckerPlayer ruleCheckerValid = new RuleCheckerPlayer(level1, validMover);
    level1.addPlayer(validMover, new Position (5,7));
    assertEquals(true, ruleCheckerValid.isValidMove(new Position (5,8)));
    assertEquals(false, ruleCheckerValid.isValidMove(new Position(40,400)));
  }

  @Test public static void testisOnLevelPlane() {
    RuleCheckerPlayer ruleCheckerp3 = new RuleCheckerPlayer(level1, level1.getPlayerObjectFromName("Jane"));
    assertEquals(true, ruleCheckerp3.isOnLevelPlane(new Position(13,13)));
  }

  @Test
  public static void testKeyTileIsLandedOnAndExitLandedOnAfter() {
    Player keyFinder = new Player("KeyFinder229");
    RuleCheckerPlayer ruleCheckerKey = new RuleCheckerPlayer(level1, keyFinder);
    level1.addPlayer(keyFinder, new Position(level1.getKeyPositionInLevel().getRow() - 1, level1.getKeyPositionInLevel().getCol()));
    level1.moveCharacter(keyFinder, level1.getKeyPositionInLevel());
    assertEquals(GameStatus.KEY_FOUND, ruleCheckerKey.keyTileIsLandedOn());

    Player exitLander = new Player("exitlander777");
    level1.addPlayer(exitLander, new Position(level1.getKeyPositionInLevel().getRow() - 1, level1.getKeyPositionInLevel().getCol()));
    level1.moveCharacter(exitLander, level1.getExitPositionInLevel());
    RuleCheckerPlayer exitLanderRCP = new RuleCheckerPlayer(level1, exitLander);
    level1.openExitTile();
    assertEquals(GameStatus.PLAYER_EXITED, exitLanderRCP.exitTileIsLandedOn());
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

    //adding key and exit
    level1.addKey(new Position(5, 27));
    level1.addExit(new Position(17, 18));

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
