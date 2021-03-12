package Game;

public interface IAdversary extends ICharacter {
    void setCharacterPosition(Position p);
    Position getCharacterPosition();
}