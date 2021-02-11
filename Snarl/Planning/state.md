## Snarl Game States

Snarl game states will be constructed and represented by a collection of classes and interfaces in Java. To begin, there will be a `Character` interface that represents all active participants of the game (i.e. players and adversaries).
* This interface will include the following fields and methods:
    * `Position characterPosition` -- the position the character is currently standing on
    * `Room currentRoom` -- the room the character is currently in
    * `Level currentLevel` -- the level the character is currently playing
    * `int characterID` -- unique identifier for each `Character`
    * `void moveCharacter(Position movePosition)` -- moves a character to the given position
    * Getters and setters

There will be a `Player` class that implements the `Character` interface. It will contain all the same `Character` interface fields and methods, in addition to unique ones specified below, pertaining to player attributes:
* `boolean isExpelled` -- indicates whether or not the player has been expelled
* `void expelPlayer(boolean expel)` -- formally expels player by evaluating its field to expelled

There will be an `Adversary` interface that extends the `Character` interface. There can be different types of adversary classes such as zombies or ghosts that implement this interface. The interface fields will include all the fields and methods from the `Character` interface. The game software will consist of rooms and levels, all part of a 2D dungeon crawler that can be represented by a `GameManager` class, which holds all information about the current game state and will perform operations needed to update the game state (or delegate those actions to other classes).
* The `GameManager` class fields and methods include:
    * `LinkedHashSet<Level> allLevels` -- a set of all the levels in that game
    * `int levelsWon` -- keeps track of levels played and won
    * `Level currentLevel` -- determines which level the players are currently on
    * `Set<Player> allPlayers` -- a set of all the players (up to four in size) playing in that game
    * `Set<Adversary> adversaries` -- a set of the adversaries in the game
    * `boolean isOver` -- returns true if the players have lost the currentLevel or if they have won the last level
    * `void endGame()` -- will end the game once the game is won by checking `isOver` value

The game software will also include `Level`, `Room`, and `Hallway` classes. Those will be defined/structure by the following:
* The `Level` class fields and methods will include:
    * `LinkedHashSet<Room> allRooms` -- a set of all the rooms in the level
    * `boolean isKeyFound` -- indicates if the key for that level has been found
    * `Set<Player> players` -- a set of all the players participating in that level
    * `Set<Player> activePlayers` -- a set of all active (i.e. non-expelled) players
    * `Set<Adversary> adversaries` -- a set of all the adversaries participating in that level
    * `boolean playersWon` -- indicates if the level has been won (i.e. if a player has reached the exit)
    * `void placeCharacter(Position placeLocation)` -- places a new character to the given position
    * `void removePlayer(Player p)` -- marks the given player as expelled
    * Getters and setters

* The `Room` class fields and methods will include:
    * `Set<Player> playersInRoom` -- a set of all the players in that room
    * `Set<Adversary> adversariesInRoom` -- a set of all the adversaries in the room
    * `void removePlayer(Player p)` -- removes the given player from that room
    * `boolean isValidMove(Position from, Position to)` -- checks whether the given movement is valid
    * Getters and setters

* The `Hallway` class fields and methods will include:
  * `List<Position> waypoints` -- a list of waypoints that indicate changes in direction in the hallway

An implementation of Snarl demands a data representation for game states. A Snarl state should contain information necessary to check validity of moves and progress the game. The full state will be private to the Game Manager, while other components (players, AIs) might be provided a restricted view of the state.

Add a description of an interface with operations that other components may need to perform on the game state, or to interact with it. This might look like a wishlist with function signatures and purpose statements.

Scope: The purpose of this task is to think about what information is relevant for the game manager to discharge its responsibilities of running a dungeon, managing actors and progressing the game; and how this information should be represented. We are looking for a careful analysis of the information available to you (including any clarifications), not a perfect spec set in stone.
