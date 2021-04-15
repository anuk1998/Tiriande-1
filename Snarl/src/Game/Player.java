package Game;

public class Player implements ICharacter{
    Position playerPosition;
    String name;
    String avatar;
    boolean isExpelled;
    int numOfTimesExited = 0;
    int numOfKeysFound = 0;
    int numOfTimesExpelled = 0;

    public Player(String n) {
        this.name = n;
        isExpelled = false;
    }

    public int getNumOfTimesExited() {
        return this.numOfTimesExited;
    }

    public int getNumOfKeysFound() {
        return this.numOfKeysFound;
    }

    public void increaseNumOfKeysFound() {
        this.numOfKeysFound++;
    }

    public void increaseNumOfTimesExited() {
        this.numOfTimesExited++;
    }

    public void increaseNumOfTimesExpelled() {
        this.numOfTimesExpelled++;
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
        this.playerPosition = new Position(p.getRow(), p.getCol());
    }

    @Override
    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String newAvatar) {
        this.avatar = newAvatar;
    }
}

