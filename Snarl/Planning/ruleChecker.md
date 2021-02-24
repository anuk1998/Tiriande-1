We will create an interface `RuleChecker` that can be implemented by the different types of characters (`Player` class and `Adversary` interface). 
This interface will contain the methods and fields:
* `boolean isValidMove(Position destPoint)` -- determines if the given destination point is a reachable and valid move for the character.
* `void setMaxTilesPossible(int max)` -- sets how many tiles a player or adversary can move. This could be a different amount for players and adversaries.
* `void keyTileIsLandedOn()` -- determines a set of actions if the key tile has been landed on by a player or adversary.
* `void exitTileIsLandedOn()` -- determines a set of actions if the Level exit tile has been landed on by a player or adversary.
    * `boolean isExitUnlocked()` -- helper for `exitTileIsLandedOn()` to check if the exit has been unlocked by checking if the Level key has been found.
    * `void winLevel()` -- called by `exitTileIsLandedOn()` if `isExitUnlocked()` returns true and advances all players to next level, if any (calls function below to check), otherwise ends the game
    * `boolean isLastLevel()` -- called by `winsLevel()`, checks if the current won level is the last level
* `void seesAPlayer()` -- determines a response if a player or adversary sees another player
* `void seesAnAdversary()` -- determines a response if a player or adversary sees another adversary

In addition to the methods we already have in the `Player` class, we will also add these methods/fields for the purposes of rule checking:
* `boolean is2CardinalTilesAway(Position destPoint)` -- ensures that the destination tile is 2 cardinal moves away from the player's current position. 
* `int maxTilesPossible` -- decides how many tiles a player or adversary can move. This could be potentially different for players and adversaries.