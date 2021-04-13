The strategies and abilities we have given to adversaries are outlined below: 

- Zombies cannot move onto wall tiles. They are confined to the room tiles in the room they are spawned in.
- Adversaries cannot land on another adversaries.
- Adversaries are able to land on keys and exit tiles but cannot collect objects, and cannot stay on those objects for 
  the duration of the game (they will treat such tiles as regular tiles)

We have also implemented strategies for the adversaries to react to a player in the same vicinity:  

- Ghosts make their moves based on the player closest to them in the level by distance, regardless of what room/hallway they are in. 
  Based on the player closest to them, the ghost makes a move towards that player (within their cardinal four).
- Zombies make their moves based on the player closest to them by distance inside the same room as them.    
  Based on the player closest to them in the same room, the zombie makes a move towards that player (within their cardinal four).
  If there is no player in the same room as that zombie, a random move for the zombie is chosen (among its cardinal four).
  
Here are some example situations that could occur: Zombies are denoted by a `Z`, ghosts are denoted by a `G`,
and players are denoted by `P`, for simplicity. The level key is represented by `*`.

1. Before adversary's move: 
## P . P . ■
## . * . Z ■
## ■ ■ | ■ ■
##     x         ■ ■ ■ ■
##     x x x x x | . P ■
##               ■ ■ ■ ■

After adversary's move: the zombie chose the right-most player as closest to it 
in the same room and moved towards it. 
## P . P Z ■
## . * . . ■
## ■ ■ | ■ ■
##     x         ■ ■ ■ ■
##     x x x x x | . P ■
##               ■ ■ ■ ■

2. Before adversary's move: 
##  ■ ■ ■ ■       ■ ■ ■ ■
##  ■ . G | x x x | . P ■ 
##  ■ ■ ■ ■       ■ ■ | ■
##                    x       ■ ■ ■ ■
##                    x x x x | . P ■
##                            ■ ■ ■ ■

After adversary's move: the ghost sees that the player with the avatar '@' is closest to it by distance, despite
it being in a different room, and makes a move closer to it. 

##  ■ ■ ■ ■       ■ ■ ■ ■
##  ■ . . G x x x | . P ■
##  ■ ■ ■ ■       ■ ■ | ■
##                    x       ■ ■ ■ ■
##                    x x x x | . P ■
##                            ■ ■ ■ ■





