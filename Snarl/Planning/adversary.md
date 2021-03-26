## Adversary Interface

We have already constructed an `IAdversary` interface that is implemented by the `Zombie` and `Ghost` classes. 
It also extends an interface `ICharacter` that contains overarching methods for both players and adversaries. 

### Interface Components

The IAdversary interface currently contains the following methods:
  - `void setCharacterPosition(Position p)` - sets the adversary's position to the given position
  - `Position getCharacterPosition()`- returns the adversary's current position
  - `String getType()` - this method returns the type of particular adversary - whether it is a Ghost or Zombie
The IAdversary interface also has access to the methods in the ICharacter interface:
  - `String getName()` - returns the username of the character
  - `String getAvatar()` - returns the ASCII string avatar of the character, currently, adversary avatars are limited to `Z`s and `G`s
  
### Relationship with `GameManager` and `RuleChecker`

In the `GameManager` class, adversaries are in the turn rotation and make a move every round. These moves are tracked by the `GameManager` just like the player's moves.
Similarly, just as we have a `RuleCheckerPlayer` class that implements the `IRuleChecker` interface, we have a `RuleCheckerAdversary` class
that will be instantiated for every adversary move. This class will check the validity of the given adversary move and return
what type of move it is, mirroring exactly `RuleCheckerPlayer`'s functionality. 
We chose to separate `Player` and `Adversary` rule checking components because both actors will have different rules regarding
how and where they can move within the level.

### At the Beginning of the Game

In order to allow an adversary to get the full level information at the beginning of the game, we will create a method `sendUpdates(Level currentLevel)`
that will output a list of state information including a list of active players and their locations (using a `getPlayerLocations(Level currentLevel)` method described below), a list of rooms, object positions, hallway information, etc. 
In addition, the method will utilize the `renderLevel()` method in the `Level` class to render a visual representation of the level to each adversary
via their designated `User` object.

### At Every Turn

To receive an update of on all the player locations at the event of an adversary's turn, we will use a method `getPlayerLocations(Level currentLevel)` which will 
compile a list of locations of all active players in a level and send them to the adversary through their `User` instance. It will access the `getActivePlayers()` method in the `Level` class, loop
through the list of active players, and compile each of their locations into a list. 

Additionally, adversaries, like players, will probably receive an update of their immediate view in the level before being
prompted to request a move. Since we don't know what an adversary's maximum view is yet, the `renderView()` method in our
`IUser` interface will be implemented differently for an adversary versus a player. This will be elaborated on once we
receive more information about adversaries.
