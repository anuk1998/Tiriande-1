package Game;

public class Zombie implements IAdversary {
    Position advPosition;
    String name;

    public Zombie(String name) {
        this.name = name;
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
        this.advPosition = new Position(p.getRow(), p.getCol());
    }

    @Override
    public Position getAdversaryPosition() {
        return this.advPosition;
    }

    @Override
    public void setAdversaryPosition(Position p) {
      this.advPosition = p;
    }
}

