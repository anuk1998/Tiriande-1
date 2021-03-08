package Game;

public interface IRuleChecker {
    public GameStatus runRuleChecker(Position destination);
    public GameStatus encountersOppositeCharacter();
    public boolean isValidMove(Position destPoint);

}
