The strategies for determining the next move of adversaries are up to you, 
but they should be sufficiently different: a ghost should clearly take advantage 
of the additional movement possibilities. Additionally, it has to be clear that 
the adversaries react to a player in the vicinity by moving towards it.

Describe the strategies in Planning/adversary-strategies.md and include some 
example situations and the choices the adversary will make.

- zombies cannot move onto walls. They are confined to the room tiles in the room they are spawned in.
- adversaries are able to move onto keys and exits
- adversaries cannot land on another adversaries 
- when a player gets close to a ghost, the ghost chases them (by distance, room doesn't matter)
- when a player is in the same room as a zombie, the zombie chases the closest player to it
