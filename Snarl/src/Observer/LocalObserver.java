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
    return "Active Players: " + currentLevel.getActivePlayers();
  }

  @Override
  public String constructListOfExpelledPlayersUpdate(ArrayList<Player> expelledPlayers) {
    return "Expelled Players: " + expelledPlayers;
  }

  @Override
  public String constructListOfExitedPlayersUpdate(ArrayList<Player> exitedPlayers) {
    return "Exited Players: " + exitedPlayers;
  }

  @Override
  public String constructListOfAdversariesUpdate(Level currentLevel) {
    return "Adversaries: " + currentLevel.getAdversaries();
  }
}
