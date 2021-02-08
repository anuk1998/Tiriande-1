---------------------------------------------------------------------------------------------
       Snarl server       TCP/IP      Snarl client                  user/player
                            ||                                          | 
           |                ||            |                             |
           |                ||            |<----------------------------| user starts/requests new game
           |<~~~~~~~~~~~~~~~~~~~~~~~~~~~~ | send request for game       | 
           |                ||            |                             |
           |~~~~~~~~~~~~~~~~~~~~~~~~~~~~> | receive confirmation of game| 
           |                ||            |---------------------------->| user can now make moves
           |                ||            |<----------------------------| make moves
           |<~~~~~~~~~~~~~~~~~~~~~~~~~~~~~| send moves to server        |
           |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~>| receive response            | render response
           |                ||            |                             |
          ...               ||           ...                           ...
           |                ||            |                             |
           |                ||            |<----------------------------| user requests move to unlocked exit tile
           |<~~~~~~~~~~~~~~~~~~~~~~~~~~~~~| sends request               | 
           |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~>| level ends                  |
           |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~>| creates new level for player| 
           |                ||            |---------------------------->| render response
           |                ||            |                             |
          ...              ....          ...                           ...
           |                ||            |                             |
           |                ||            |<----------------------------| user requests to move to tile and gets expelled next move
           |<~~~~~~~~~~~~~~~~~~~~~~~~~~~~~| sends request to server     |
           |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~>| expels player               |
           |                ||            |---------------------------->| user can no longer/frozen move until another player makes it through the unlocked exit
           |                ||            |                             |
          ...              ....          ...                           ...
           |                ||            |                             |
           |                ||            |<----------------------------| user makes it through unlocked exit on last level
           |<~~~~~~~~~~~~~~~~~~~~~~~~~~~~~| sends request to server     |
           |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~>| game is won                 |
           |                ||            |---------------------------->| game ends
           
 
