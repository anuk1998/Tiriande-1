package Game;

public class Zombie implements IAdversary {
    Position zombiePosition;
    Room currentRoom;
    String name;

    public Zombie(String name) {
        this.name = name;
    }



    @Override
    public void setAdversaryPosition(Position p) {
        this.zombiePosition = p;
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
