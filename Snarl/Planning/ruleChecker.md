The Rule Checker needs to validate the movements and interactions from players and adversaries as well as determine the end of a level versus the end of the game. 
It should also know when to reject invalid game states. 
Other functionality might be added later in the project if needed.

The movement rules for players are listed below.

A player can move to any traversable tile up to 2 cardinal moves away from themselves. 
In other words, a player can move to a corner tile in 1 turn if it could reach that same tile in 2 moves using only cardinal moves. 
The chosen tile can be occupied by a key, exit, or an adversary, or nothing.

The player can interact with keys and objects and adversaries (for self-elimination) but not other players.

When the player moves, they interact with the object on the tile they choose and no other tile.

For details on the end of a level versus end of a game, refer to the Snarl Overview.

Your task is to design the rule checker’s interface.

When designing, consider how your design could be extended to handle different kinds of adversaries with other 
movement abilities. For instance, a ghost type adversary could potentially ignore walls entirely.

Scope: The purpose of this task is looking at the required rules and detailing how the relevant components will interact with a rule checker.

---------------------------------------------------------

We will create an interface `RuleChecker` that can be implemented by the different types of characters (`Player` class and `Adversary` interface). 
This interface will contain the methods and fields:
* `boolean isValidMove(Position destPoint)` -- determines if the given destination point is a reachable and valid move for the character.
* `void setMaxTilesPossible(int max)` -- sets how many tiles a player or adversary can move. This could be a different amount for players and adversaries.
* `void keyTileIsLandedOn()` -- determines a set of actions if the key tile has been landed on by a player or adversary.
* `void exitTileIsLandedOn()` -- determines a set of actions if the level exit tile has been landed on by a player or adversary.
    * `boolean isExitUnlocked()` -- helper for `exitTileIsLandedOn()` to check if the exit has been unlocked by checking if the level key has been found.
* `void seesAPlayer()` -- determines a response if a player or adversary sees another player
* `void seesAnAdversary()` -- determines a response if a player or adversary sees another adversary


In addition to the methods we already have in the `Player` class, we will also add these methods/fields for the purposes of rule checking:
* `boolean is2CardinalTilesAway(Position destPoint)` -- ensures that the destination tile is 2 cardinal moves away from the player's current position. 
* `int maxTilesPossible` -- decides how many tiles a player or adversary can move. This could be potentially different for players and adversaries.