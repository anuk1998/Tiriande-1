package Game;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import Common.IUser;
import User.LocalUser;

public class UserTesting {
  static Level level1 = new Level();
  static Room room1 = new Room(new Position(0, 0), 8, 10);
  static Room room2 = new Room(new Position(15, 15), 10, 10);
  static Room room3 = new Room(new Position(4, 17), 4, 6);
  static Room room4 = new Room(new Position(2, 25), 7, 11);
  static Room room5 = new Room(new Position(23, 2), 14, 7);
  static Room room6 = new Room(new Position(30, 27), 7, 7);

  public static void main(String[] args) {
    createInitialGameBoard();
    getUserName();
    testGetAllAdversaryPositions();
    testGetAllPlayerPositions();
    testOutputView();
    testMakeView();
    System.out.println(level1.renderLevel());
  }

  @Test
  public static void getUserName() {
    LocalUser testUser = new LocalUser("User1");
    assertEquals("User1", testUser.getUserName());
  }

  @Test
  public static void testOutputView() {
    LocalUser testUser = new LocalUser("RenderViewCorrectly");
    String [][] testArray = new String[5][4];
    for(int i = 0; i < testArray.length; i++) {
      for(int j = 0; j < testArray[0].length; j++) {
        testArray[i][j] = ".";
      }
    }
    testArray[0][0] = "■";
    testArray[0][1] = "■";
    testArray[0][2] = "|";
    testArray[0][3] = "■";
    testArray[2][1] = "O";
    testArray[3][2] = "Z";
    assertEquals("■ ■ | ■\n" + ". . . .\n" +
            ". O . .\n" + ". . Z .\n" + ". . . .\n", testUser.outputView(testArray));
  }

  @Test
  public static void testMakeView() {
    ArrayList<Level> levels = new ArrayList<Level>();
    levels.add(level1);
    GameManager gm = new GameManager(levels, 1);
    gm.registerPlayer("Giselle", Registration.LOCAL);
    IUser user = gm.getUserByName("Giselle");
    ICharacter player = level1.getPlayerObjectFromName("Giselle");
    ((Player)player).setAvatar("$");
    level1.moveCharacter(player, new Position(5, 19));

    String[][] view = new String[5][5];
    view[0][0] = " ";
    view[0][1] = " ";
    view[0][2] = " ";
    view[0][3] = "x";
    view[0][4] = " ";
    view[1][0] = "■";
    view[1][1] = "■";
    view[1][2] = "■";
    view[1][3] = "|";
    view[1][4] = "■";
    view[2][0] = "■";
    view[2][1] = ".";
    view[2][2] = "$";
    view[2][3] = ".";
    view[2][4] = ".";
    view[3][0] = "■";
    view[3][1] = ".";
    view[3][2] = ".";
    view[3][3] = ".";
    view[3][4] = ".";
    view[4][0] = "■";
    view[4][1] = "■";
    view[4][2] = "|";
    view[4][3] = "■";
    view[4][4] = "■";
    assertArrayEquals(view, user.makeView(level1, player));
  }

  @Test
  public static void testGetAllPlayerPositions() {
    Player p1 = new Player("Giselle");
    Player p2 = new Player("Anu");
    Player p3 = new Player("Ferd");
    p1.setAvatar("P");
    p2.setAvatar("P");
    p3.setAvatar("P");
    level1.addCharacter(p1, new Position(4, 3));
    level1.addCharacter(p2, new Position(18, 17));
    level1.addCharacter(p3, new Position(2,2));
    ArrayList<Level> levels = new ArrayList<Level>();
    levels.add(level1);
    GameManager gm = new GameManager(levels, 1);
    gm.addUser("Giselle", Registration.LOCAL);
    gm.addUser("Anu", Registration.LOCAL);
    gm.addUser("Ferd", Registration.LOCAL);
    IUser user = gm.getUserByName("Giselle");

    ArrayList<Position> playerPositions = new ArrayList<Position>();
    playerPositions.add(new Position(4,3));
    playerPositions.add(new Position(2, 2));
    playerPositions.add(new Position(18,17));
    assertEquals(playerPositions, ((LocalUser)user).getAllPlayerLocations(level1));
  }

  @Test
  public static void testGetAllAdversaryPositions() {
    Zombie z = new Zombie("Giselle");
    Ghost g = new Ghost("Anu");
    level1.addCharacter(z, new Position(4, 3));
    level1.addCharacter(g, new Position(18, 17));
    ArrayList<Level> levels = new ArrayList<Level>();
    levels.add(level1);
    GameManager gm = new GameManager(levels, 1);
    gm.addUser("Giselle", Registration.LOCAL);
    gm.addUser("Anu", Registration.LOCAL);
    IUser user = gm.getUserByName("Giselle");

    ArrayList<Position> playerPositions = new ArrayList<Position>();
    playerPositions.add(new Position(4,3));
    playerPositions.add(new Position(18,17));
    assertEquals(playerPositions, ((LocalUser)user).getAllAdversaryLocations(level1));

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

    //adding key and exit
    level1.addObject(new Position(5, 27), "*");
    level1.addObject(new Position(6, 27), "●");
  }
}
