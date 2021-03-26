# Milestone 6 - Refactoring Report

**Team members:**
Giselle Briand and Anu Kandasamy

**Github team/repo:** Tiriande


## Plan

The following is a list of things we believe we can realistically achieve refactoring-wise in the next week:

* Clean up code/filter through unnecessary methods in `Level` class
* Add wall tile representation
* Move things from `Level` class into `GameManager`
* Look into different ASCII symbols for players/users -- they currently are all represented as `P`s
  * `@`, `:)`, `&`, etc.
  * Alternatively, look at `1`, `2`, `3`, `4`
* Look into different ASCII symbols for adversaries on the level board (they're all represented by `A`s)  
  * `Z` for Zombie
  * `G` for Ghost
* Include a list of waypoints field in the constructor of a `Hallway` object
* Change traversable room tile representation (possible a `.`) in level board


## Changes

During this refactoring week, we completed several tasks to improve the usability and efficiency of our code. We 
moved several methods from the `Level` class into more appropriate classes and combined methods to abstract the code.
A lot of methods in Level were used almost exclusively for test harnesses in previous Milestones, so a few were removed
from the `Level` class and into their respective testing harness files, while others remained in `Level`. 
We also created 5 sections in our `Level` class to further separate and organize our methods. The sections are:

1. Methods that construct the Level.
2. Methods that change the game state.
3. Getter methods for Level fields. 
4. Helpful methods to retrieve information about the Level and its components.
5. Testing harness-specific methods.

We also focused on condensing code in select functions in `Level` that were either unnecessarily verbose or simply
inefficient, reducing our total code line count in the file by over 100 lines. Some methods we condensed or abstracted
include (but are not limited to):
* Getting rid of `movePlayer()` & `moveAdversary()` and combined them into `moveCharacter()`
* Abstracting `addKey()` and `addExit()` into one `addObject()`
* Reducing the `placeCharacterAtRandomPositionInLevel()` to just the `Level` class rather than half in `Level` and half
  in `Room` class
* Optimizing `getAllAdjacentTiles()` and `addRoom()` to fewer lines
* & other minor changes to other methods

We also created different ASCII symbols for players/users (`@`, `~`, `&`, `¤`, in which we randomly assign a new player
one of these ASCII avatars upon registration in the GameManager. Similarly, we also changed the ASCII avatar representation
for adversaries on the level board (`Z` for Zombie, `G` for Ghost). We also changed traversable room tile representation from a square to `.`,
made level void tiles ` ` (blank), and changed the hallway tile representation to a lowercase `x`. We also added a wall tile representation signified 
by a `■`. In addition to adding a wall symbol, we also made it so that our room representation automatically added wall tiles around the edge of the room
upon creation, rather than us manually adding wall tiles from the testing harnesses.


As a result of these ASCII and wall representation changes, an example of a randomly-generated level from our
representation looks like this:

```
■ ■ ■ ■ ■ ■ ■ ■ ■ ■                                                            
■ . . . . . . . . ■                                                            
■ . . . . . . . . | x x x x x x x x x x x         ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■        
■ . . . . . . . . ■                     x         ■ . . . . . . . . . ■        
■ . . . . . . . . ■               ■ ■ ■ | ■ ■     ■ . . . . . . . . . | x x    
■ . . . . . . . . ■               ■ . . . ~ ■     ■ . * . . . . . . . ■   x    
■ . . . . . . . . ■               ■ . . . . ■     ■ . . ¤ . . . . . . ■   x    
■ ■ ■ ■ ■ ■ | ■ ■ ■               ■ ■ | ■ ■ ■   x | . . . . . . . . . ■   x    
            x                         x         x ■ ■ ■ ■ | ■ ■ ■ ■ ■ ■   x    
            x                         x         x         x               x    
            x                         x x x x x x         x               x    
            x x x x x x x x x x                           x               x    
                              x                           x               x    
                              x                           x               x    
                              x x x                       x               x    
                              ■ ■ | ■ ■ ■ ■ ■ ■ ■         x               x    
                              ■ . . . . . . . . ■         x               x    
                              ■ . . ● . . . . . ■         x               x    
                              ■ . . . . . . . . ■         x               x    
                              ■ . . . G . . . . ■         x               x    
                              ■ . . . . . . . . ■         x               x    
            x x x x x x x x x | . . . . . . . . ■         x               x    
            x                 ■ . . . . . . . . ■         x               x    
    ■ ■ ■ ■ | ■ ■             ■ . . . . . . . . | x x x   x               x    
    ■ . . . . & ■             ■ ■ ■ ■ ■ ■ ■ ■ ■ ■     x   x               x    
    ■ . . . . . ■                                     x   x x x x         x    
x x | . . . . . ■                                     x         x         x    
x   ■ . . . . . ■                                     x x x     x         x    
x   ■ Z . . . . | x x x                                   x     x         x    
x   ■ . . . . . ■     x                                   x     x         x    
x   ■ . . . . . ■     x                               ■ ■ | ■ ■ | ■       x    
x   ■ . . . . . ■     x                               ■ . . . . . ■       x    
x   ■ . . . . . ■     x                               ■ . . . . . ■       x    
x   ■ . @ . . . ■     x x x x x x x x x x x x x x x x | . . . . . ■       x    
x   ■ . . . . . ■                                     ■ . . . . . ■       x    
x   ■ . . . . . ■                                     ■ . . . . . ■       x    
x   ■ ■ ■ ■ ■ ■ ■                                     ■ ■ ■ ■ ■ ■ ■       x    
x                                                                         x    
x x x x x x x x x x x x x x x x x x x x x x x x x x x x x x x x x x x x x x   
```
We feel as though these ASCII character additions improves the readability and representation of all aspects of the level.

We also included a "list of waypoints" field in the constructor of a `Hallway` object to make it easier to initialize a hallway. 
We found and fixed several bugs within methods in `Level` class including:
* `movePlayer()` was not correctly changing the previous tile when a player moved forward. It always assumed the player
  was on a room tile. It now takes into account whether the player is in a hallway, a room, or on a closed exit and as
  mentioned above, has been abstracted into `moveCharacter()`
* `expelPlayer()` had duplicate code as other methods and wasn't properly removing a player from the board once they had
  self-eliminated
* `getAllAdjacentTiles()` didn't actually get *all* adjacent tiles from the given position-- it only retrieved the tiles
  that were equal to certain tiles. Now that we have implemented `RuleCheckerPlayer`, we adjusted `getAllAdjacentTiles()`
  to return *all* adjacent tiles, regardless of their type
* `addRoom()` was unintentionally adding the same room object for every tile of the room that was being iterated over,
  causing the `allRooms` list in `Level` to be an absurdly large size

In other files, other than `Level`, we also cleaned up. In `Room`, we removed several unused fields and methods
such as `renderRoom()`, `placeCharacterRandomlyInRoom()`, and `getAllRoomPosns()`. 

## Future Work

In the future, if time permits, we plan to implement additional features to improve the efficiency of our code. Some of these features include:
* Creating an inventory attribute for players
* Randomly generating hallways (we currently rely on given Hallway objects)
* Randomly generating wall tiles within the room 
* Potentially creating a sub-class for Level that focuses exclusively on building the Level

## Conclusion

Thank you for giving us this week to improve our code design and organization! It helped us take a deep-dive into our code,
specifically older code, and find bugs we didn't notice prior, as well as set up our code better for
future milestones.
