package Game;

public interface IRuleChecker {
    GameStatus runRuleChecker(Position destination);
    GameStatus encountersOppositeCharacter();
    boolean isValidMove(Position destPoint);
    boolean isTileTraversable(Position tile);
    boolean isCharactersCurrentPosition(Position destPoint);
    boolean isNCardinalTilesAway(Position destPoint, int maxTilesAway);
    boolean isOnLevelPlane(Position destPoint);

}
