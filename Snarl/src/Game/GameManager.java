package Game;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class GameManager {
    LinkedHashMap<String, Player> allPlayers = new LinkedHashMap<>();
    LinkedHashSet<ICharacter> allCharacters = new LinkedHashSet<>();
    Level currentLevel;
    boolean notifyPlayer;

    public GameManager() {
       startGame();
    }

    public void startGame() {
        // ask for players
        // register players
        // once all players are registered then forever-loop through each of them
        //    in that loop, make an instance of RuleCheckerPlayer or RuleChecker

        for (int i=0; i<allCharacters.size())
        /*

        for (int i=0; i<LinkedSetOfAdversariesAndPlayers.size(); i++) {
            Character character = LinkedSetOfAdversariesAndPlayers.get(i);
            // ask for move
            if (character isInstanceOf (Player)) {
                RuleCheckerPlayer ruleCheckerP = new RuleCheckerPlayer(currentLevel, character);
                result = ruleCheckerP.runRuleChecker(move);
            }
            else if (character isInstanceOf (Adversary)) {
                RuleCheckerAdversary ruleCheckerA = new RuleCheckerAdversary(currentLevel, character);
                result = ruleCheckerP.runRuleChecker(move);
            }
            // check result enum
            if VALID: send move to level to be executed
            else if INVALID: send error back to user
            else if GAME_WON:
            else if GAME_LOST:

            if (i == LinkedSetOfAdversariesAndPlayers.size() - 1) {
                i = 0;
            }
        }
        */
    }

    public Level getCurrentLevel() {
        return this.currentLevel;
    }

    public void registerPlayer(String name) {
        if (allPlayers.containsKey(name)) {
            System.out.println("Cannot register Player with name `" + name + "`. Name already has been taken. Please pick again.");
        }
        else if (allPlayers.size() < 4) {
            Player newPlayer = new Player(name);
            allPlayers.put(name, newPlayer);
            allCharacters.add(newPlayer);
            System.out.println("Player " + name + " has been registered.");
        }
        else {
            System.out.println("Cannot register player " + name + ". Game has reached maximum participant count. Sorry!");
        }

    }

    public void registerAdversaries(String name, ) {
        
    }

    public void addPlayersToGame() {
        for (Player p : allPlayers.values()) {
             //currentLevel.addPlayer(p, new Position());
        }
        for(int i = 0; i < allPlayers.size(); i++) {
          //currentLevel.addPlayer(allPlayers.get(i), new Position());
        }

    }




}
