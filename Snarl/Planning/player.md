The iPlayer interface will contain several fields and methods:
* `void getPlayerLocationInLevel()` -- gets the player's location in relation to the level's origin
* `void getAllVisiblePositions()` -- shows all traversable points for a player (2 grid units away in any direction)
* `void movePlayer(Position p)` -- moves a player to a given position, updates in the `currentLevel` of the game manager
* `void collectKey` -- collects the key from a tile if present and marks it as found in `currentLevel` of the game manager
* `void traverseExit` -- moves player through the exit if present and ends the game in game manager (in this case, because we assume there is only 1 level)

