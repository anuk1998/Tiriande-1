Design an interface for Snarl adversaries. An adversary is similar to a Player, in that it interacts with the Game Manager on every turn. However there are a some differences:

An adversary gets the full level information (comprised of rooms, hallways and objects) at the beginning of a level
An adversary gets an update on all player locations, but only when it’s about to make a turn
Note: the extent of what adversaries see might change if we determine they are too powerful.


Scope: We are looking for data definitions, signatures and purpose statements à la Fundies, or definitions and interface specifications approximating your chosen language (if it has such constructs). You are encouraged to use examples and/or diagrams to illustrate interaction between adversaries and the Game Manager

We have already constructed an IAdversary interface 
- broadcastTurn method: use this to update adversaries on player's move
- in callRenderView() in GameManager check if it's an adversary, if it is, render a different view
- look into potentially showing the adversary the full level image when they're being registered?