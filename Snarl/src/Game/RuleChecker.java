package Game;

 public class RuleChecker {
    Level currentLevel;

    public RuleChecker(Level currentLevel) {
       this.currentLevel = currentLevel;
    }

    boolean isValidMove(Position destPoint) {
       if (is2CardinalTilesAway(destPoint)
       && !currentLevel.getTileInLevel(destPoint).equals("#")
       && !currentLevel.getTileInLevel(destPoint).equals("P")
       && !currentLevel.getTileInLevel(destPoint).equals(".")) {

       }
    }
    public void setMaxTilesPossible(int max) {
    }
    public void keyTileIsLandedOn(){}
    public void exitTileIsLandedOn(){}
    public boolean isExitUnlocked(){
        return true;
    }
    public void winLevel(){}
    public boolean isLastLevel(){
        return true;
    }
    public void seesAPlayer(){}
    public void seesAnAdversary(){}
    public boolean is2CardinalTilesAway(Position destPoint){
        return true;
    }
    public int maxTilesPossible(){
        return 0;
    }
}
