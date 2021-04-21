package Observer;

import java.util.ArrayList;

import Common.IObserver;
import Game.*;

public class LocalObserver implements IObserver {
  String observerName;

  public LocalObserver(String name) {
    this.observerName = name;

  }

  @Override
  public void sendUpdates(ICharacter currentCharacter, Position requestedMove, GameStatus moveStatus,
                          Level currentLevel, ArrayList<Player> exitedPlayers, ArrayList<Player> expelledPlayers) {
    System.out.println(constructMoveStatusUpdate(currentCharacter, requestedMove, moveStatus));
    System.out.println(constructListOfActivePlayersUpdate(currentLevel));
    System.out.println(constructListOfExpelledPlayersUpdate(expelledPlayers));
    System.out.println(constructListOfExitedPlayersUpdate(exitedPlayers));
    System.out.println(constructListOfAdversariesUpdate(currentLevel));
    System.out.println(renderGameViewUpdate(currentLevel));
  }

  @Override
  public String renderGameViewUpdate(Level currentLevel) {
    System.out.println("The current state of the Level is:");
    return currentLevel.renderLevel();
  }

  @Override
  public String constructMoveStatusUpdate(ICharacter currentCharacter, Position move, GameStatus moveStatus) {
    return "Player " + currentCharacter.getName() + " made a move to position " + move.toString() +
            ", with a move status of " + moveStatus.name();
  }


  @Override
  public String constructListOfActivePlayersUpdate(Level currentLevel) {
    ArrayList<String> activePlayerNames = new ArrayList<>();
    for (Player p: currentLevel.getActivePlayers()) {
      activePlayerNames.add(p.getName());
    }
    return "Active Players: " + activePlayerNames;
  }

  @Override
  public String constructListOfExpelledPlayersUpdate(ArrayList<Player> expelledPlayers) {
    ArrayList<String> expelledPlayerNames = new ArrayList<>();
    for(Player p: expelledPlayers) {
      expelledPlayerNames.add(p.getName());
    }
    return "Expelled Players: " + expelledPlayerNames;
  }

  @Override
  public String constructListOfExitedPlayersUpdate(ArrayList<Player> exitedPlayers) {
    ArrayList<String> exitedPlayerNames = new ArrayList<>();
    for(Player p: exitedPlayers) {
      exitedPlayerNames.add(p.getName());
    }
    return "Exited Players: " + exitedPlayerNames;
  }

  @Override
  public String constructListOfAdversariesUpdate(Level currentLevel) {
    ArrayList<String> adversaryPlayerNames = new ArrayList<>();
    for(IAdversary a: currentLevel.getAdversaries()) {
      adversaryPlayerNames.add(a.getName());
    }
    return "Adversaries: " + adversaryPlayerNames.toString();
  }
}
