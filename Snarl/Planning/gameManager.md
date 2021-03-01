## Game Manager Interface


For now, the main task of the Game Manager is to accept players to 
the game and start a game with a single level, which will be 
provided. 

The purpose of this task is looking at how the Game Manager and Players should interact to start and run a single level. 
We are looking for data definitions, signatures and purpose statements à la Fundies, or definitions and 
interface specifications approximating your chosen language (if it has such constructs). 
Feel free to use examples and diagrams.

The iGame interface will include the following fields and methods:
* `Set<Player> allPlayers` -- this field will always be set to 4 players, but this can be changed to accomodate more. It will be initialized once all players register for the game.
* `Level currentLevel` -- represents the single level that is part of this game 

