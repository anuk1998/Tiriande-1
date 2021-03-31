package Common;
import java.util.ArrayList;

import Game.*;

public interface IObserver {

    /**
     * Sends all the updates to observers.
     *
     * @param currentCharacter: the character whose move it is
     * @param moveStatus: the type of move the character requested
     * @param currentLevel: the Level being played
     */
    void sendUpdates(ICharacter currentCharacter, Position requestedMove, GameStatus moveStatus, Level currentLevel,
                     ArrayList<Player> exitedPlayers, ArrayList<Player> expelledPlayers);

    /**
     * Constructs a rendered visual view of the state of the given Level.
     *
     * @param currentLevel: the Level that needs to be rendered
     * @return a String representation of the Level 2D array to be sent
     */
    String renderGameViewUpdate(Level currentLevel);

    /**
     * Constructs the update that outlines what kind of move the given player just made.
     *
     * @param currentCharacter: the current character whose move it is
     * @param moveStatus: the type of move the charater requested
     * @return a String representation of the GameStatus update to be sent
     */
    String constructMoveStatusUpdate(ICharacter currentCharacter, Position move, GameStatus moveStatus);

    /**
     * Constructs the list of active players in the level
     *
     * @param currentLevel: the Level being played
     * @return a String representation of the list of active players in the level
     */
    String constructListOfActivePlayersUpdate(Level currentLevel);

    /**
     * Constructs the list of expelled players in the level
     *
     * @param expelledPlayers: a list of all expelled players
     * @return a String representation of the list of expelled players in the level
     */
    String constructListOfExpelledPlayersUpdate(ArrayList<Player> expelledPlayers);

    /**
     * Constructs the list of exited players in the level.
     *
     * @param exitedPlayers: a list of players who have exited the level
     * @return a String representation of the list of players who have successfully passed
     *         through the level exit
     */
    String constructListOfExitedPlayersUpdate(ArrayList<Player> exitedPlayers);

    /**
     * Constructs the list of adversaries in the level.
     *
     * @param currentLevel: the Level being played
     * @return a String representation of the list of adversaries in the level
     */
    String constructListOfAdversariesUpdate(Level currentLevel);

  }


