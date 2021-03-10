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
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Position getCharacterPosition() {
        return this.advPosition;
    }

    @Override
    public void setCharacterPosition(Position p) {
        this.advPosition = p;
    }

}
