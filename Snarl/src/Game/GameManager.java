package Game;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class GameManager {
    LinkedHashMap<String, Player> allPlayers = new LinkedHashMap<>();
    Level currentLevel;
    boolean notifyPlayer;

    public GameManager() {

    }
    public Level getCurrentLevel() {
        return this.currentLevel;
    }


        public void registerPlayer(String name) {
                if (allPlayers.containsKey(name)) {
                            System.out.println("Cannot register Player with name `" + name + "`. Name already has been taken. Please pick another name.");

    public void registerPlayer(String name) {
        if (allPlayers.containsKey(name)) {
            System.out.println("Cannot register Player with name `" + name + "`. Name already has been taken. Please pick again.");
        }
        else if (allPlayers.size() < 4) {
            Player newPlayer = new Player(name);
            allPlayers.put(name, newPlayer);
            System.out.println("Player " + name + " has been registered.");
        }
        else {
            System.out.println("Cannot register player " + name + ". Game has reached maximum participant count. Sorry!");
        }

    }

    public void addPlayersToGame() {
        for (Player p : allPlayers.values()) {
             currentLevel.addPlayer(p, new Position());
        }
        for(int i = 0; i < allPlayers.size(); i++) {
          currentLevel.addPlayer(allPlayers.get(i), new Position());
        }

    }




}
