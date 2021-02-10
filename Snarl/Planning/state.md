## Snarl Game States

Snarl game states: all classes & interfaces in the server (players, levels, rooms, etc.)

Client: check validity of moves, progress the game

Server: Game Manager, does not have access to full game state.

An implementation of Snarl demands a data representation for game states. A Snarl state should contain information necessary to check validity of moves and progress the game. The full state will be private to the Game Manager, while other components (players, AIs) might be provided a restricted view of the state.

Describe a data representation for Snarl game states. Think back to data definitions in Fundies I and use a mix of English and the data definition constructs from your chosen language.

Add a description of an interface with operations that other components may need to perform on the game state, or to interact with it. This might look like a wishlist with function signatures and purpose statements.

The memo must not exceed two pages. Less is more.

Scope: The purpose of this task is to think about what information is relevant for the game manager to discharge its responsibilities of running a dungeon, managing actors and progressing the game; and how this information should be represented. We are looking for a careful analysis of the information available to you (including any clarifications), not a perfect spec set in stone.

OH questions:
- what is Snarl game state?
- is game manager server? is that up to us to decide?
- discuss what we did in milestone 1, ask if this design task is essentially
  asking us to do what we already did in milestone 1
- submission--> how to submit properly, maven?
