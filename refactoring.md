# Milestone 6 - Refactoring Report

**Team members:**
Giselle Briand and Anu Kandasamy

**Github team/repo:** Tiriande


## Plan

List areas of your code/design you'd like to improve with brief descriptions,
anything that comes to mind. Tip: translate this list into Github Issues.

* Clean up code/filter through unnecessary methods in `Level` class
DONE * Move things from `Level` class into `GameManager`
DONE * Look into different ASCII symbols for players/users -- they currently are all represented as 'P's
      * @, smiley face icon, $, etc.
      * Alternatively, look at 1, 2, 3, 4
DONE * Look into different ASCII symbols for adversaries on the level board (they're all represented by 'A's)  
      * Z for Zombie
      * G for Ghost
DONE * Include a list of waypoints field in the constructor of a `Hallway` object
DONE * Change traversable room tile representation (possible a '.') in level board
* Add wall tile representation
DONE * Fix MovePlayer bug when player moves forward in hallway
DONE * Get rid of movePlayer & moveAdversary and keep MoveCharacter
DONE * Missing the key and exit bug  

## Changes

Summarize the work you have performed during this week.
 

## Future Work

Summarize work you'd still like to do if there's time. This can include features
you'd like to implement if given time.

* Create an inventory attribute for players
* Look into randomly generating hallways


## Conclusion

Any concluding remarks.