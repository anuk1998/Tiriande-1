## Project Analysis -- Design Task

As identifiable components of our software system, the following is how our Snarl game will be constructed. There will be a `Character` interface that represents all active participants of the game (i.e. players and adversaries). This interface will include the following fields and methods:

* Fields:
    * `Tile characterLocation` -- the tile the character is currently standing on
    * `Room currentRoom` -- the room the character is currently in
    * `Level currentLevel` -- the level the character is currently playing
    * `int characterID` -- unique identifier for each `Character`
* Methods :
    * `void moveCharacter(Tile moveLocation)` -- moves a character to the given Tile location
    * In addition, getters and setters will also be included for the object fields
    
There will be a `Player` class that implements the `Character` interface. 

* It will contain all the same `Character` interface fields and methods, in addition to unique ones specified below, pertaining to player attributes:
    * `boolean isExpelled` -- indicates whether or not the player has been expelled
* The class methods will include: 
    * `void expelPlayer(boolean expel)` -- formally expels player by evaluating its field to expelled
    * Getters and setters will be included
    
Even though code for the adversaries will be supplied to us in a tournament, we must be able to provide a framework for supporting these adversaries. There will be an `Adversary` interface that extends the `Character` interface. There can be different types of adversary classes such as zombies or ghosts that implement this interface.
* The interface fields will include all the fields from the `Character` interface.
* The interface methods will include:
    * Getters and setters will be included

The game software will consist of tiles, rooms, hallways, and levels, all part of a 2D dungeon crawler. That can be represented by the following structure using a `DungeonCrawler` class.
* The `DungeonCrawler` class fields include:
    *` Set<Level> allLevels` -- a set of all the levels in that game
    * `Set<Player> players` -- a set of all the players (up to four in size) playing in that game
    * `Set<Adversary> adversaries` -- a set of the adversaries in the game
    * `boolean isOver` -- indicates if the game is over
    * `boolean playerWon` -- indicates if the players are the ones who won the game (if not, the adversaries won)
* The class methods will include:
    * `void placeCharacter(Tile placeLocation)` -- places a new character to the given Tile location
    * `void removePlayer(Player p) -- removes the given player from the game if they are expelled by an adversary
    * `void endGame()` -- will end the game once the game is won by checking `isOver` value
    * Getters and setters
    
The game software will also include `Level`, `Room`, `Hallway` classes and `Tile` interface. Those will be defined/structure by the following:
* The `Level` class fields will include:
    * `Set<Hallway> allHallways` -- a set of all hallways in that level
    * `Set<Room> allRooms` -- a set of all the rooms in the level
    * `boolean isKeyFound` -- indicates if the key for that level has been found
    * `Set<Player> players` -- a set of all the players participating in that level
    * `Set<Adversary> adversaries` -- a set of all the adversaries participating in that level
    * `boolean playersWon` -- indicates if the level has been won (i.e. if a player has reached the exit)
* The `Level` class methods will include:
    * `void removePlayer(Player p)` -- removes the given player from that level
    * Getters and setters
    
* The `Room` class fields will include:
    * `Set<Tile> allTiles` -- a set of all the tiles in that room
    * `Set<Tile> occupiedTiles` -- a list of all the occupied tiles in the room (tiles with characters)
    * `Set<Player> playersInRoom` -- a set of all the players in that room
    * `Set<Adversary> adversariesInRoom` -- a set of all the adversaries in the room
* The `Room` class methods will include:
    * `void removePlayer(Player p)` -- removes the given player from that room
    * Getters and setters
    
* The `Hallway` class fields will include:
    * `Room fromRoom` -- indicates the starting room of the hallway
    * `Room toRoom` -- indicates the ending room of the hallway
    * `Set<Character> charsTravelling` -- indicates whether players are travelling through the hallway
* The `Hallway` class methods will include:
    * Getters and setters

* The `Tile` interface fields will include:
    * `Set<Tile> adjacentTiles` -- a set containing all tiles adjacent to that tile
    *`int tileID` -- a unique identifier for each tile, allowing us to differentiate
* The `Tile` interface methods will include:
    * Getters and setters

The `WallTile` class implements the `Tile` interface and will include all the fields from that interface. This class represents the grey tiles in a room (i.e. tiles that cannot be traversed or accessed because they are “walls”) The `WallTile` fields and methods will include the same methods in the `Tile` interface.

The `WhiteTile` class represents the white tiles in a room that are open to the characters and can be traversed. The `WhiteTile` class implements the `Tile` interface and will include all the fields from that interface along with the following:
    * `boolean isAvailable` -- indicates if it is an open tile
    * `boolean containsKey` -- indicates if that tile holds the exit key
* The `WhiteTile` class methods will include the same methods in the `Tile` interface along with these additional methods:
    * Getters and setters
    
The `ExitTile` class represents a tile in a room in a level that is the exit to that level. This class implements the `Tile` interface and will include all the fields from that interface along with the following:
    * `boolean isUnlocked` -- indicates if the exit has been unlocked or not (i.e. has the key been found)
* The class methods will include the same methods in the `Tile` interface along with these additional methods:
    * `boolean setUnlocked` -- sets `boolean isUnlocked` to true
    
All the classes, interfaces, and methods listed above would constitute the server side of the game. This is because it contains all the fundamental knowledge and functionality that makes up the game. The client, on the other hand, would be the side that interacts with the users seeking to play the game, acting as a middle-man between the players and the server. The client only needs to know the user’s desires and what decisions they would like to make. Those instructions will be passed into the client, who will ensure they are appropriate, and if so, they will then be sent over to the server for the server to follow those instructions. The server and the client will communicate through TCP/IP socket connections. A diagram of this communication topology can be found in the `local.md` file. 

The following is what we feel to be appropriate milestones for implementing what we discuss in Part 1:
1. Implement the skeleton for our server that we outlined in Part 1.
2. Implement basic client structure that will communicate with users..
3. Set up TCP/IP connection/communication between the client and server.
4. Implement testing of basic methods belonging to the server and the client, in addition to making sure communication between the two work.



