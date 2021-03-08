package Game;

public interface IAdversary extends ICharacter {

    // UNUSED IN THIS MILESTONE, WILL TEST AT LATER DATE
    void expelPlayer(boolean expel);
    void setAdversaryPosition(Position p);
    public Position getAdversaryPosition();

}