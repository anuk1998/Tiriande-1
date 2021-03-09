package Game;

public class Ghost implements IAdversary{
    Position advPosition;
    Room currentRoom;
    int adversaryID;
    boolean isExpelled;

    public Ghost(int id) {
        this.isExpelled = false;
    }

    @Override
    public void expelPlayer(boolean expel) {
        
    }

}
