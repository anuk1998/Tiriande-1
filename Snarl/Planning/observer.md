## Observing Component

### Observer

To incorporate an observing element to our Snarl game, we will use an `Observer` interface that will be
implemented by the `GameManager` class. This interface will contain various methods, with a primary one
that `GameManager` will call in order to update the observers of the game when moves take place.

The idea with this interface is that for each turn (both adversary and player), whether or not a valid move
was given, a series of updates are sent to observers. These updates include (so far):

1) A rendered view of the current state of the Level.
2) The status of the most recent move (what kind of move it was) and which character made it
3) A list of active players.
4) A list of expelled players.
5) A list of players who passed through the level (via open exit).
6) A list of active adversaries (this would include information about their type).

These different types of updates to inform the observers of will all be created with their own 
functions, and the primary `sendUpdates(...)` function will call each of them and send their resulting
updates to all the observers. Consider the following skeleton:

```
interface Observer {
    
    /**
    * Adds an observer to the list of observers.
    *
    * @param observer: the observer to add
    */
    void addObserver(Observer observer);

    /**
    * Sends all the updates to observers.
    * 
    * @param currentCharacter: the character whose move it is
    * @param moveStatus: the type of move the character requested
    * @param currentLevel: the Level being played
    */
    void sendUpdates(ICharacter currentCharacter, GameStatus moveStatus, Level currentLevel);
    
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
    String constructMoveStausUpdate(ICharacter currentCharacter, GameStatus moveStatus);
    
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
    * @param currentLevel: the Level being played
    * @return a String representation of the list of expelled players in the level
    */
    String constructListOfExpelledPlayesUpdate(Level currentLevel);
    
    /**
    * Constructs the list of exited players in the level.
    *
    * @param currentLevel: the Level being played
    * @return a String representation of the list of players who have successfully passed
    *         through the level exit
    */
    String constructListOfExitedPlayersUpdate(Level currentLevel);
    
    /**
    * Constructs the list of adversaries in the level.
    *
    * @param currentLevel: the Level being played
    * @return a String representation of the list of adversaries in the level
    */
    String constructListOfAdversariesUpdate(Level currentLevel);

}
```

More update methods will be added if necessary and as we see fit in future Milestones. As 
we learn more information about the functionality of this Observer interface (i.e., incorporating
networking components), further expansion is inevitable. We believe this foundational structure 
is sufficient by the standards of our current understanding of the Observer's purpose. 
This structure is subject to change as our understanding does as well.
