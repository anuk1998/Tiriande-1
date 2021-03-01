## Game Manager Interface
The iGame interface will be receiving information from the iPlayer interface to create and run a single level. 
It will include the following fields and methods:
* `Set<Player> allPlayers` -- this field will always be set to 4 players, but this can be changed to accomodate more. It will be initialized once all players register for the game.
* `Level currentLevel` -- represents the single level that is part of this game. This level will be manipulated by the methods in the iPlayer interface.
* `boolean notifyPlayer()` -- notifies the player component when it is the players' turn, and the player will then make a move. 
                              Returns true when it is the players' turn, and a player needs to move, or false when it is not the players' turn. 
