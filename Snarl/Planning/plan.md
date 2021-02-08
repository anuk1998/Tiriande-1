## Project Analysis -- Design Task

As identifiable components of our software system, the following is how our Snarl game will be constructed. There will be a `Character` interface that represents all active participants of the game (i.e. players and adversaries).
* This interface will include the following fields and methods:
    * `Tile characterLocation` -- the tile the character is currently standing on
    * `Room currentRoom` -- the room the character is currently in
    * `Level currentLevel` -- the level the character is currently playing
    * `int characterID` -- unique identifier for each `Character`
    * `void moveCharacter(Tile moveLocation)` -- moves a character to the given Tile location
    * Getters and setters

There will be a `Player` class that implements the `Character` interface. It will contain all the same `Character` interface fields and methods, in addition to unique ones specified below, pertaining to player attributes:
* `boolean isExpelled` -- indicates whether or not the player has been expelled
* `void expelPlayer(boolean expel)` -- formally expels player by evaluating its field to expelled

There will be an `Adversary` interface that extends the `Character` interface. There can be different types of adversary classes such as zombies or ghosts that implement this interface. The interface fields will include all the fields and methods from the `Character` interface. The game software will consist of tiles, rooms, and levels, all part of a 2D dungeon crawler that can be represented by a `DungeonCrawler` class.
* The `DungeonCrawler` class fields and methods include:
    * `Set<Level> allLevels` -- a set of all the levels in that game
    * `Set<Player> players` -- a set of all the players (up to four in size) playing in that game
    * `Set<Adversary> adversaries` -- a set of the adversaries in the game
    * `boolean isOver` -- indicates if the game is over
    * `boolean playerWon` -- indicates if the players are the ones who won the game (if not, the adversaries won)
    * `void placeCharacter(Tile placeLocation)` -- places a new character to the given Tile location
    * `void removePlayer(Player p)` -- removes the given player from the game if they are expelled by an adversary
    * `void endGame()` -- will end the game once the game is won by checking `isOver` value

The game software will also include `Level`, `Room` classes and a `Tile` interface. We will add a `Hallway` class once we receive more information on what a hallway is. Those will be defined/structure by the following:
* The `Level` class fields and methods will include:
    * `Set<Room> allRooms` -- a set of all the rooms in the level
    * `boolean isKeyFound` -- indicates if the key for that level has been found
    * `Set<Player> players` -- a set of all the players participating in that level
    * `Set<Adversary> adversaries` -- a set of all the adversaries participating in that level
    * `boolean playersWon` -- indicates if the level has been won (i.e. if a player has reached the exit)
    * `void removePlayer(Player p)` -- removes the given player from that level
    * Getters and setters

* The `Room` class fields and methods will include:
    * `Set<Tile> allTiles` -- a set of all the tiles in that room
    * `Set<Tile> occupiedTiles` -- a list of all the occupied tiles in the room (tiles with characters)
    * `Set<Player> playersInRoom` -- a set of all the players in that room
    * `Set<Adversary> adversariesInRoom` -- a set of all the adversaries in the room
    * `void removePlayer(Player p)` -- removes the given player from that room
    * Getters and setters

* The `Tile` interface fields and methods will include:
    * `Set<Tile> adjacentTiles` -- a set containing all tiles adjacent to that tile
    * `int tileID` -- a unique identifier for each tile, allowing us to differentiate
    * Getters and setters

Three classes will implement `Tile` interface: `GreyTile` (un-traversable tiles), `WhiteTile` (traversable tiles), and `ExitTile` (tile with the exit). All will inherit `Tile` class fields and methods. Additional, class-specific fields include:
* `boolean isAvailable` -- indicates if it is an open tile in `WhiteTile` class
* `boolean containsKey` -- indicates if that tile holds the exit key in `WhiteTile` class
* `boolean isUnlocked` -- indicates if the exit has been unlocked or not (i.e. has the key been found) in `ExitTile` class

All the classes, interfaces, and methods listed above would constitute the server side of the game. This is because it contains all the fundamental knowledge and functionality that makes up the game. The client, on the other hand, would be the side that interacts with the users seeking to play the game, acting as a middle-man between the players and the server. The client only needs to know the user’s desires and what decisions they would like to make. Those instructions will be passed into the client, who will ensure they are appropriate, and if so, they will then be sent over to the server for the server to follow those instructions. In terms of common knowledge, the server and the client will communicate through mutual TCP/IP socket connections. The main "knowledge" they will share in addition to their network communication, is what the user "wants", i.e., whichever move or play the user asks for. A diagram of this communication topology can be found in the `local.md` file.

The following is what we feel to be appropriate milestones for implementing what we discuss in Part 1:
1. Implement the skeleton for our server that we outlined in Part 1.
2. Implement basic client structure that will communicate with users..
3. Set up TCP/IP connection/communication between the client and server.
4. Implement testing of basic methods belonging to the server and the client, in addition to making sure communication between the two work.
5. All the above would result in a working demo-- debug/clean-up before showing potential client, then ask for feedback on improvements.
