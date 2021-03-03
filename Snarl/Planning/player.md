For now, an iPlayer interface will keep track of a player's position in a level, as well as all traversable points. 
The interface will contain the functionality of moving a player, collecting objects, and traversing through an exit.
All of this information will be communicated to the game manager interface. The game manager interface will communicate 
to the player component when it is time to make a move. 

iPlayer will tentatively include the following methods and fields:

* `void getPlayerLocationInLevel()` -- gets the player's location in relation to the level's origin
* `void getAllVisiblePositions()` -- shows all traversable points for a player (2 grid units away in any direction)
* `void movePlayer(Position p)` -- moves a player to a given position, updates in the `currentLevel` of the game manager
* `void collectKey()` -- collects the key from a tile if present and marks it as found in `currentLevel` of the game manager
* `void traverseExit()` -- moves player through the exit if present and ends the game in game manager (in this case, because we assume there is only 1 level)

