package Game;

public class RuleCheckerAdversary implements IRuleChecker {
    @Override
    public GameStatus runRuleChecker(Position destination) {
        return null;
    }

    @Override
    public GameStatus encountersOppositeCharacter() {
        return null;
    }

    @Override
    public boolean isValidMove(Position destPoint) {
        return false;
    }
}
