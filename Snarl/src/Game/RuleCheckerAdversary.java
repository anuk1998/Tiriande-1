package Game;

public class RuleCheckerAdversary implements IRuleChecker {
    
    public RuleCheckerAdversary(Level currentLevel, IAdversary adversary) {
        
    }
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
    
    @Override
    public boolean isTileTraversable(Position tile) { return false;}
}
