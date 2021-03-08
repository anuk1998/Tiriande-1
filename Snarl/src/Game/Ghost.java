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

    @Override
    public void setAdversaryPosition(Position p) {
        this.advPosition = p;
    }

    @Override
    public Position getAdversaryPosition() {
        return this.advPosition;
    }
}
