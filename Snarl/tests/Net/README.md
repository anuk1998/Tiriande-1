## Snarl Instructions

#### Note: 
For this milestone, we were not able to test on the Khoury servers due to Java Memory issues on the server, which the Professor is aware of and deemed to be out of our control. 

To play our Snarl game, you must first connect to the server via `./snarlServer`. We added information
statements on the server-side to update the person launching the server about what's going on. 
For example, when you first launch the program, it will tell you when it's configuring the server,
preparing for connections, and finally, when it is accepting connections. Once you see that the server
is accepting connections, that is when a `./snarlClient` may be launched to connect to the server. The 
observer view can be launched in the terminal itself, via the `--observe` flag when the server is launched.
No additional GUI is required.

The following is a rundown of how the game is to be played for clients:

* The objective of the game as a client is to find the key and escape the level through the exit while avoiding 
  getting eaten by enemies, including a ghost and/or a zombie.
* The game will prompt each player to first register with a username. After a name is given, there will be 
  some wait time, and this is normal. This means the server is waiting for all players to register, so not getting
  an immediate response from the server after giving a name is normal. 
* Once all players have registered, or when the timeout is reached, the game will then begin. All players will
  receive a preliminary game message, indicating where they are in the level, and what/who is nearby.
* If it is a player's turn, you will be given a prompt asking to input your desired move on the board.  
* The player will be able to see their current view/ where they can move. 
* The game will continue to prompt the players for moves one by one. It is important to be wary of adversaries, who can
  expel players if they get close enough (within 1 cardinal position). All players will receive updates of other players' moves throughout the game
  and the status of their moves.
* A level will continue until one of these scenarios occurs:
    - All the players have been eaten by adversaries
    - The key has been found and at least one player has passed through the level exit
* The game will continue until one of these scenarios:
    - All the players have been eaten by adversaries
    - The key has been found and at least one player has passed through the level exit, AND it is the last 
    level in the game. 
   
