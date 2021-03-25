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
        return this.advPosition;
    }

    @Override
    public String getType() {
        return "zombie";
    }

    @Override
    public void setCharacterPosition(Position p) {
        this.advPosition = new Position(p.getRow(), p.getCol());
    }

    @Override
    public String getAvatar(){
        return "Z";
    }

}

