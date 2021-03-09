package Game;

public class Player implements ICharacter{
    Position playerPosition;
    String name;
    boolean isExpelled;

    public Player(String n) {
        this.name = n;
        boolean isExpelled = false;
    }

    public void setIsExpelled(boolean expel) {
        this.isExpelled = expel;
    }

    public boolean getIsExpelled() {
        return this.isExpelled;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Position getCharacterPosition() {
        return this.playerPosition;
    }

    @Override
    public void setCharacterPosition(Position p) {
        this.playerPosition = p;
    }
}

