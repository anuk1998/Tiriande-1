package level;

public class Player {
    Position playerPosition;
    Room currentRoom;
    Level currentLevel;
    int playerID;
    boolean isExpelled;

    public Player() {

    }

    public void movePlayer(Position movePosition) {
        this.playerPosition = movePosition;

    }

}

