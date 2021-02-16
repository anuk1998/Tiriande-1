## Snarl Game States

Snarl game states will be constructed and represented by a collection of classes and interfaces in Java. To begin, there will be a `Character` interface that represents all active participants of the game (i.e. players and adversaries).
* This interface will include the following fields and methods:
    * `Position characterPosition` -- the position the character is currently standing on
    * `Room currentRoom` -- the room the character is currently in
    * `Level currentLevel` -- the level the character is currently playing
    * `int characterID` -- unique identifier for each `Character`
    * `void moveCharacter(Position movePosition)` -- moves a character to the given position
    * Getters and setters

There will be a `Player` class and an `Adversary` interface. The `Player` class will contain these fields and methods:
* `boolean isExpelled` -- indicates whether or not the player has been expelled.

There will be an `Adversary` interface will contain this method:
* `void expelPlayer(boolean expel)` -- formally expels player by evaluating its field to expelled
There can be different types of adversary classes such as zombies or ghosts that implement this interface. For simplicity of this milestone, we have left `Adversary` to be a class. 

The game software will consist of rooms and levels, all part of a 2D dungeon crawler that can be represented by a `GameManager` class, which holds all information about the current game state and will perform operations needed to update the game state (or delegate those actions to other classes).
The `Game Manager` will only have access to an overview of the higher level elements of the game, such as all of 
the levels, the current level being played, a list of the players, etc. It will not have access to lower level elements, such as 
a specific door in a room. Conversely, players and adversaries will be able to view rooms and position availability, but will not have a full level view. Different roles (Game Manager, Player, Adversary) 
have different levels of access to the game state since they have different responsibilities.

* The `GameManager` class fields and methods include:
    * `LinkedHashSet<Level> allLevels` -- a set of all the levels in that game
    * `int levelsWon` -- keeps track of levels played and won
    * `Level currentLevel` -- determines which level the players are currently on
    * `Set<Player> allPlayers` -- a set of all the players (up to four in size) playing in that game
    * `Set<Adversary> adversaries` -- a set of the adversaries in the game
    * `boolean isOver` -- returns true if the players have lost the currentLevel or if they have won the last level
    * `void endGame()` -- will end the game once the game is won by checking `isOver` value

The game software will also include `Level`, `Room`, `Position`, and `Hallway` classes. Those will be defined/structure by the following:
* The `Level` class fields and methods will include:
    * `String[][] levelPlane` -- a 2D string array representing the entire level plane (with rooms, hallways, etc.)
    * `boolean isKeyFound` -- indicates if the key for that level has been found
    * `Set<Player> players` -- a set of all the players participating in that level
    * `Set<Player> activePlayers` -- a set of all active (i.e. non-expelled) players
    * `Set<Adversary> adversaries` -- a set of all the adversaries participating in that level
    * `boolean playersWon` -- indicates if the level has been won (i.e. if a player has reached the exit)
    * `void placeCharacter(Position placeLocation)` -- places a new character to the given position
    * `void removePlayer(Player p)` -- marks the given player as expelled
    * Getters and setters

* The `Room` class fields and methods will include:
    * `String[][] room` -- a 2D array representing the room tiles
    * `Position roomPositionInLevel` -- a Position (x and y coordinate) of where in the level plane that room begins (the upper most top left square)
    * `int roomWidth` -- how many 'tiles' wide the room is
    * `int roomHeight` -- how many 'tiles' long the room is
    * `ArrayList<Position> listOfAllPositions` -- a list of all coordinates in that room
    * `Set<Player> playersInRoom` -- a set of all the players in that room
    * `Set<Adversary> adversariesInRoom` -- a set of all the adversaries in the room
    * `void removePlayer(lPlayer p)` -- removes the given player from that room
    * `boolean isValidMove(Position from, Position to)` -- checks whether the given movement is valid
    * Getters and setters
    
* The `Position` class fields and methods will include: 
    * int `x_pos` -- This will represent the x position of a Position object.
    * int `y_pos` -- This will represent the y position of a Position object.
    * getter methods for the `x_pos` and `y_pos` fields.

* The `Hallway` class fields and methods will include:
  * `List<Position> waypoints` -- a list of waypoints that indicate changes in direction in the hallway
  * `List<Position> allPositions` -- a list of all the positions on the plane of that hallway 
  * `void connectHallwayPositions()` -- a method that will create a list of all the hallway poisitons by using the list of waypoints to construct it
  
  
  
