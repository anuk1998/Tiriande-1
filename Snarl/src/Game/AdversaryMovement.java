package Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import Common.IUser;
import User.LocalUser;

public class AdversaryMovement {
  Level level;

  public AdversaryMovement(Level level) {
    this.level = level;
  }

  /**
   * Chooses the given adversary's next move and then sends it to RuleChecker before being executed.
   *
   * @param currentUser current adversary's user instance
   * @param character   adversary whose move it is
   * @return a chosen position for the given adversary
   */
  public Position chooseAdversaryMove(IUser currentUser, IAdversary character) {
    LocalUser user = (LocalUser) currentUser;
    ArrayList<Position> playerPositions = user.getAllPlayerLocations(this.level);
    Position chosenPosition;
    if (character instanceof Zombie) {
      chosenPosition = chooseZombieMove(character, playerPositions);
    }
    else {
      chosenPosition = chooseGhostMove(character, playerPositions);
    }
    return chosenPosition;
  }

  /**
   * Chooses the given Zombie's next move based on what other players are in its room. If there
   * are no players in its room, a random cardinal position is chosen. If there are multiple players
   * in the room, the zombie will move towards the closest one.
   *
   * @param character       the Zombie
   * @param playerPositions list of all the players' positions
   * @return a chosen position for the zombie to move to
   */
  public Position chooseZombieMove(IAdversary character, ArrayList<Position> playerPositions) {
    Position chosenMove;
    Zombie zom = (Zombie) character;
    Position zomPos = zom.getCharacterPosition();
    Room zombiesRoom = zom.getZombiesRoom();

    // determines which players are in the same room as the zombie and adds their positions to a list
    ArrayList<Position> playersInRoomWithZombie = positionsInSameRoom(zombiesRoom, playerPositions);
    // Based on how many players are in the room with the Zombie, choose which player to attack
    // and then subsequently which cardinal move is closest to that chosen player
    ArrayList<Position> cardinalPositions = this.level.getAllAdjacentTiles(zomPos);
    if (playersInRoomWithZombie.size() == 0) {
      Random rand = new Random();
      int cardinalIndex = rand.nextInt(cardinalPositions.size() - 1);
      chosenMove = cardinalPositions.get(cardinalIndex);
    } else if (playersInRoomWithZombie.size() == 1) {
      chosenMove = getClosestPositionTo(cardinalPositions, playersInRoomWithZombie.get(0));
    } else {
      Position closestPlayerPos = getClosestPositionTo(playersInRoomWithZombie, zomPos);
      chosenMove = getClosestPositionTo(cardinalPositions, closestPlayerPos);
    }
    return chosenMove;
  }

  /**
   * Helper method for chooseZombieMove. Determines which positions from a given list of positions
   * are in the same room as a given room and adds their position to a list.
   */
  public ArrayList<Position> positionsInSameRoom(Room compareRoom, ArrayList<Position> comparePositions) {
    ArrayList<Position> sameRoomPositions = new ArrayList<>();
    for (Position playerPos : comparePositions) {
      for (Position roomPos : compareRoom.getListOfAllPositionsLevelScale()) {
        if (playerPos.toString().equals(roomPos.toString())) {
          sameRoomPositions.add(playerPos);
        }
      }
    }
    return sameRoomPositions;
  }
  /**
   * Chooses the given Ghost's next move based on what other players are closest to it by distance,
   * regardless of what room they are in.
   *
   * @param character       the Ghost
   * @param playerPositions list of all the players' positions
   * @return a chosen position for the ghost to move to
   */
  public Position chooseGhostMove(IAdversary character, ArrayList<Position> playerPositions) {
    Position playerPositionToAttack = getClosestPositionTo(playerPositions, character.getCharacterPosition());
    ArrayList<Position> ghostAdjacentTiles = level.getAllAdjacentTiles(character.getCharacterPosition());
    return getClosestPositionTo(ghostAdjacentTiles, playerPositionToAttack);
  }

  /**
   * Determines which position in a given list that is closest to the given source position.
   * Helper for chooseGhostMove and chooseZombieMove.
   *
   * @param positionsToCompare list of positions
   * @param source             position to compare distance to
   * @return the closest position in distance
   */
  public Position getClosestPositionTo(ArrayList<Position> positionsToCompare, Position source) {
    HashMap<Position, Double> positionsAndDistances = new HashMap<>();
    int sourceRow = source.getRow();
    int sourceCol = source.getCol();

    for (Position pos : positionsToCompare) {
      double distance = Math.sqrt(Math.pow(sourceRow - pos.getRow(), 2) + Math.pow(sourceCol - pos.getCol(), 2));
      positionsAndDistances.put(pos, distance);
    }

    Position closestPos = null;
    double lowestDistance = 1000.0;
    for (Map.Entry entry : positionsAndDistances.entrySet()) {
      if ((double) entry.getValue() < lowestDistance) {
        lowestDistance = (double) entry.getValue();
        closestPos = (Position) entry.getKey();
      }
    }
    return closestPos;
  }
}
