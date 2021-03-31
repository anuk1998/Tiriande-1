package Game;

public class Ghost implements IAdversary{
    Position advPosition;
    String name;

    public Ghost(String name) {
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
        return "ghost";
    }

    @Override
    public void setCharacterPosition(Position p) {
        this.advPosition = new Position(p.getRow(), p.getCol());
    }

    @Override
    public String getAvatar(){
        return "G";
    }

}
