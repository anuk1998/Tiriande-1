package Game;

 public interface RuleChecker {
     boolean isValidMove(Position destPoint);
     public void setMaxTilesPossible(int max);
    public void keyTileIsLandedOn();
    public void exitTileIsLandedOn();
    public boolean isExitUnlocked();
    public void winLevel();
    public boolean isLastLevel();
    public void seesAPlayer();
    public void seesAnAdversary();
    public boolean is2CardinalTilesAway(Position destPoint);
    public int maxTilesPossible();
}
