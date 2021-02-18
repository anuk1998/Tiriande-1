package level;

public class Player {
    Position playerPosition;
    Room currentRoom;
    Level currentLevel;
    int playerID;
    boolean isExpelled;

    public Player(int id) {
        this.playerID = id;
        boolean isExpelled = false;
    }


    public void setPlayerPosition(Position p) {
        this.playerPosition = p;
    }

    public Position getPlayerPosition() {
        return this.playerPosition;
    }



}

