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
  //static IAdversary ghost = new Ghost("Scary Ghost");
  //static IAdversary zombie = new Ghost("Weird Zombie");
  static Level[] lev = {level1};
  static ArrayList<Level> listOfLevels = new ArrayList<Level>(Arrays.asList(lev));
  static GameManager gm = new GameManager(listOfLevels, 0);

  public static void main(String[] args) {
    listOfLevels.add(level1);
    createInitialGameBoard();

    //testIsLastLevel();
    //testParseMoveDoAction();
    //testRegisterAdversary();
    //testRegisterPlayer();
    testisNCardinalTilesAway();
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
    assertEquals(true, gm.parseMoveStatusAndDoAction(GameStatus.INVALID.name(), new Position(2000, 2), level1.getPlayerObjectFromName("Carl")));
    Position ghostPos = level1.getAdversaryObjectFromName("scary").getCharacterPosition();
    gm.parseMoveStatusAndDoAction(GameStatus.PLAYER_SELF_ELIMINATES.name(), ghostPos, level1.getPlayerObjectFromName("Carl"), null);
    assertEquals(null, level1.getPlayerObjectFromName("Carl"));
  }

  @Test
  public static void testIsExitUnlocked() {
    RuleCheckerPlayer exitUnlocked = new RuleCheckerPlayer(gm,level1, p1);
    assertEquals(false, exitUnlocked.isExitUnlocked() );
  }

  @Test 
  public static void testCheckPlayerActiveStatus() {
    Player bob = level1.getPlayerObjectFromName("Bob");
    // checking active status before Player is expelled
    assertEquals(true, gm.checkPlayerActiveStatus(bob));
    // checking active status after Player has been expelled
    IAdversary evil = new Ghost("evil");
    level1.addCharacter(evil, new Position(bob.getCharacterPosition().getRow() - 1, bob.getCharacterPosition().getCol()));
    gm.parseMoveStatusAndDoAction(GameStatus.PLAYER_SELF_ELIMINATES.name(), evil.getCharacterPosition(), bob, null);
    assertEquals(false, gm.checkPlayerActiveStatus(bob));
  }

  @Test
  public static void testEncountersOppositeCharacter() {
    Player selfEliminator = new Player("selfEliminator455");
    level1.addCharacter(selfEliminator, new Position(4,4));
    IAdversary boo = new Ghost("boo!");
    level1.addCharacter(boo, new Position(4,5));
    level1.moveCharacter(selfEliminator, boo.getCharacterPosition());
    RuleCheckerPlayer selfElimRCP = new RuleCheckerPlayer(gm, level1, selfEliminator);
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
    RuleCheckerPlayer ruleChecker444 = new RuleCheckerPlayer(gm, level1, ruleChecker);
    level1.addCharacter(ruleChecker, new Position (3,3));
    assertEquals(GameStatus.VALID, ruleChecker444.runRuleChecker(new Position(3,4)));
  }

  @Test public static void testisNCardinalTilesAway() {
    Player bob = level1.getPlayerObjectFromName("Bob");
    RuleCheckerPlayer ruleCheckerp1 = new RuleCheckerPlayer(gm,level1, bob);
    Position bobsPos = bob.getCharacterPosition();
    System.out.println("Bob's position: " + bobsPos);
    assertEquals(false, ruleCheckerp1.isNCardinalTilesAway(new Position (100,100), 1));
    assertEquals(true, ruleCheckerp1.isNCardinalTilesAway(new Position (bobsPos.getRow() - 1, bobsPos.getCol()), 1));
    assertEquals(true, ruleCheckerp1.isNCardinalTilesAway(new Position (bobsPos.getRow(),bobsPos.getCol() + 2), 2));
    assertEquals(true, ruleCheckerp1.isNCardinalTilesAway(new Position (bobsPos.getRow() + 2,bobsPos.getCol() + 2), 4));

  }

  @Test public static void testIsValidMove() {
    Player validMover = new Player("ValidMover223");
    RuleCheckerPlayer ruleCheckerValid = new RuleCheckerPlayer(gm, level1, validMover);
    level1.addCharacter(validMover, new Position (5,7));
    assertEquals(true, ruleCheckerValid.isValidMove(new Position (5,8)));
    assertEquals(false, ruleCheckerValid.isValidMove(new Position(40,400)));
  }

  @Test public static void testisOnLevelPlane() {
    RuleCheckerPlayer ruleCheckerp3 = new RuleCheckerPlayer(gm, level1, level1.getPlayerObjectFromName("Jane"));
    assertEquals(true, ruleCheckerp3.isOnLevelPlane(new Position(13,13)));
  }

  @Test
  public static void testKeyTileIsLandedOnAndExitLandedOnAfter() {
    Player keyFinder = new Player("KeyFinder229");
    RuleCheckerPlayer ruleCheckerKey = new RuleCheckerPlayer(gm, level1, keyFinder);
    level1.addCharacter(keyFinder, new Position(level1.getKeyPositionInLevel().getRow() - 1, level1.getKeyPositionInLevel().getCol()));
    level1.moveCharacter(keyFinder, level1.getKeyPositionInLevel());
    //assertEquals(GameStatus.KEY_FOUND, ruleCheckerKey.);

    Player exitLander = new Player("exitlander777");
    level1.addCharacter(exitLander, new Position(level1.getKeyPositionInLevel().getRow() - 1, level1.getKeyPositionInLevel().getCol()));
    level1.moveCharacter(exitLander, level1.getExitPositionInLevel());
    RuleCheckerPlayer exitLanderRCP = new RuleCheckerPlayer(gm, level1, exitLander);
    level1.openExitTile();
    assertEquals(GameStatus.PLAYER_EXITED, exitLanderRCP.exitTileIsLandedOn());
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

    //adding key and exit
    level1.addObject(new Position(5, 27), "*");
    level1.addObject(new Position(17, 18), "●");

    ArrayList<Position> h1Waypoints = new ArrayList<>();
    h1Waypoints.add(new Position(2, 20));
    Hallway h1 = new Hallway(new Position(4, 20), new Position(2, 9), h1Waypoints);

    ArrayList<Position> h2Waypoints = new ArrayList<>();
    h2Waypoints.add(new Position(14, 17));
    h2Waypoints.add(new Position(14, 15));
    h2Waypoints.add(new Position(11, 15));
    h2Waypoints.add(new Position(11, 6));
    Hallway h2 = new Hallway(new Position(15, 17), new Position(7, 6), h2Waypoints);

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
    h5Waypoints.add(new Position(25,29));
    h5Waypoints.add(new Position(25, 32));
    Hallway h5 = new Hallway(new Position(8, 29), new Position(30, 32), h5Waypoints);

    ArrayList<Position> h6Waypoints = new ArrayList<>();
    h6Waypoints.add(new Position(7,24));
    h6Waypoints.add(new Position(10,24));
    h6Waypoints.add(new Position(10,19));
    Hallway h6 = new Hallway(new Position(7, 25), new Position(7,19), h6Waypoints);

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

    //register players and add them to the board
    gm.registerPlayer("Bob", Registration.LOCAL);
    gm.registerPlayer("Carl", Registration.LOCAL);
    gm.registerPlayer("Santiago", Registration.LOCAL);
    gm.registerPlayer("Anu", Registration.LOCAL);

    //register adversaries and add them to the board
    gm.registerAdversary("scary", "ghost");
    gm.registerAdversary("bloody", "zombie");

    System.out.println(level1.renderLevel());
  }

}
