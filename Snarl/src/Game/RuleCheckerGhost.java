package Game;

public class RuleCheckerGhost implements IRuleChecker {
    Level currentLevel;
    IAdversary adversary;
    
    public RuleCheckerGhost(Level currentLevel, IAdversary adversary) {
        this.currentLevel = currentLevel;
        this.adversary = adversary;
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

    @Override
    public boolean isCharactersCurrentPosition(Position destPoint) {
        return destPoint.toString().equals(this.adversary.getCharacterPosition().toString());
    }
}
