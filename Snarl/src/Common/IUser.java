package Common;

import java.util.Scanner;

import Game.ICharacter;
import Game.Position;
import Game.Level;

public interface IUser {
  /**
   * Retrieves the user's name.
   *
   * @return A string representation of that user's name
   */
  String getUserName();


  /**
   * Broadcasts to the character that it's their turn and then renders their specific level view
   * (i.e., for a Player, show maximum two tiles away for all directions)
   *
   * @param character the current character whose turn it is
   */
   void broadcastUpdate(Level currentLevel, ICharacter character, boolean isPlayerActive);

  /**
   * Renders the view for the given character before they request a move.
   * Currently only accounts for players, whose maximum tile view is 2 on all sides.
   * Not implemented for Milestone 5, as per Piazza post @684.
   *
   * @param character the character whose turn it is
   * @param currentLevel the level the game is currently on
   */
   String callRenderView(Level currentLevel, ICharacter character);

  /**
   * The purpose of this function is to collect the user's desired move position.
   * This function will be implemented at a later milestone.
   *
   * @return the resulting Position in level where the user would like to move to
   */
   Position getUserMove(Scanner scanner, ICharacter character);


}
