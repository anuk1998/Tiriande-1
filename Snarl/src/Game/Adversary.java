package Game;

public class Adversary {
    Position advPosition;
    Room currentRoom;
    int adversaryID;
    boolean isExpelled;

    public Adversary(int id) {
        this.isExpelled = false;
    }

    // UNUSED IN THIS MILESTONE, WILL TEST AT LATER DATE
    void expelPlayer(boolean expel) {

    }
    
    void setAdversaryPosition(Position p) {
        this.advPosition = p;
    }
    
    public Position getAdversaryPosition() {
        return this.advPosition;
    }

}