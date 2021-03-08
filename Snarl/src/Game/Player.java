package Game;

public class Player implements ICharacter{
    Position playerPosition;
    Room currentRoom;
    Level currentLevel;
    String name;
    boolean isExpelled;

    public Player(String n) {
        this.name = n;
        boolean isExpelled = false;
    }


    public void setPlayerPosition(Position p) {
        this.playerPosition = p;
    }

    public Position getPlayerPosition() {
        return this.playerPosition;
    }

    public void setIsExpelled(boolean expel) {
        this.isExpelled = expel;
    }

    public boolean getIsExpelled() {
        return this.isExpelled;
    }



}

