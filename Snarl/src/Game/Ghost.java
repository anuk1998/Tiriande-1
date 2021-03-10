package Game;

public class Ghost implements IAdversary{
    Position advPosition;
    String name;

    public Ghost(String name) {
        this.name = name;
    }

    @Override
    public void setAdversaryPosition(Position p) {
        this.advPosition = p;
    }

    @Override
    public Position getAdversaryPosition() {
        return this.advPosition;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Position getCharacterPosition() {
        return null;
    }

    @Override
    public void setCharacterPosition(Position p) {

    }

}
