package Game;

public class Ghost implements IAdversary{
    Position ghostPosition;
    Room currentRoom;
    String name;


    public Ghost(String name) {
        this.name = name;
    }

    @Override
    public void setAdversaryPosition(Position p) {
        this.ghostPosition = p;
    }

    @Override
    public Position getAdversaryPosition() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Position getCharacterPosition() {
        return null;
    }

    @Override
    public void setCharacterPosition(Position p) {

    }
}
