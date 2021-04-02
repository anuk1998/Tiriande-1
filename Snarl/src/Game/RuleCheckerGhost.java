package Game;

import java.util.HashSet;

public class RuleCheckerGhost implements IRuleChecker {
    Level currentLevel;
    IAdversary adversary;
    GameManager gm;
    
    public RuleCheckerGhost(GameManager gm, Level currentLevel, IAdversary adversary) {
        this.currentLevel = currentLevel;
        this.adversary = adversary;
        this.gm = gm;
    }

    @Override
    public GameStatus runRuleChecker(Position destination) {
        GameStatus status = GameStatus.INVALID;
        if (isValidMove(destination)) {
            status = GameStatus.VALID;
            if (landedOnPlayer(destination)) {
                status = encountersOppositeCharacter();
            }
            if (landedOnAWallTile(destination)) {
                status = GameStatus.GHOST_TRANSPORTS;
            }
        }
        return status;
    }

    private boolean landedOnPlayer(Position destination) {
        String symbol = this.currentLevel.getTileInLevel(destination);
        return symbol.equals("@") || symbol.equals("¤") || symbol.equals("$") || symbol.equals("~");
    }

    @Override
    public GameStatus encountersOppositeCharacter() {
        // checks if the player being expelled is the last active player in the level
        if (currentLevel.getActivePlayers().size() == 1) {
            // and everyone else is expelled
            if (gm.getExpelledPlayers().size() == gm.getAllPlayers().size() - 1) {
                return GameStatus.GAME_LOST;
            }
            // checks that at least one player has passed through the level exit
            else if (gm.getExitedPlayers().size() > 0) {
                //it is the last level
                if (isLastLevel()) {
                    return GameStatus.GAME_WON;
                }
                return GameStatus.LEVEL_WON;
            }
        }
        return GameStatus.PLAYER_EXPELLED;
    }

    @Override
    public boolean isValidMove(Position destPoint) {
        boolean valid = false;
        if (isOnLevelPlane(destPoint)) {
            if (isTileTraversable(destPoint) && isNCardinalTilesAway(destPoint, 1)) {
                valid = true;
            }
        }
        return valid;
    }

    public boolean landedOnAWallTile(Position destination) {
        return this.currentLevel.getTileInLevel(destination).equals("■");
    }
    
    @Override
    public boolean isTileTraversable(Position tile) {
        return !this.currentLevel.getTileInLevel(tile).equals(" ") &&
                !this.currentLevel.getTileInLevel(tile).equals("G") &&
                !this.currentLevel.getTileInLevel(tile).equals("Z");
    }

    @Override
    public boolean isCharactersCurrentPosition(Position destPoint) {
        return destPoint.toString().equals(this.adversary.getCharacterPosition().toString());
    }

    @Override
    public boolean isNCardinalTilesAway(Position destPoint, int maxTilesAway) {
        boolean withinReach = false;
        HashSet<Position> cardinalTiles = new HashSet<>(currentLevel.getAllAdjacentTiles(this.adversary.getCharacterPosition()));

        while (maxTilesAway > 1) {
            HashSet<Position> tempCardinalTiles = new HashSet<>(cardinalTiles);
            for (Position adjacent : tempCardinalTiles) {
                cardinalTiles.addAll(currentLevel.getAllAdjacentTiles(adjacent));
            }
            maxTilesAway--;
        }

        for (Position pos : cardinalTiles) {
            if (pos.toString().equals(destPoint.toString())) withinReach = true;
        }

        return withinReach;
    }

    @Override
    public boolean isOnLevelPlane(Position destPoint) {
        int levelNumRows = this.currentLevel.getLevelNumOfRows();
        int levelNumCols = this.currentLevel.getLevelNumOfCols();
        return destPoint.getRow() >= 0 && destPoint.getRow() < levelNumRows
                && destPoint.getCol() >= 0 && destPoint.getCol() < levelNumCols;
    }

    @Override
    public boolean isLastLevel() {
        return gm.getAllLevels().indexOf(this.currentLevel) == gm.getAllLevels().size() - 1;
    }
}
