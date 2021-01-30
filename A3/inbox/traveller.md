# Traveller

### Intro

The purpose of this Java 8 package is to provide the framework for a route-based travelling game
containing characters and towns. This contains the implementation for **Character**s, **Town**s, and 
a **World** class to interact with the game. The towns in this game are connected as a simple graph, and
each town can contain residing characters. Moving characters between towns is handled by the **World**.

### Entities
#### World
###### Variables
- **Set\<Town>**: towns
    - represents the **Town** objects in _this **World**_
- **Set\<Character>**: characters
    - represents the **Character** objects in _this **World**_

###### Methods
- **boolean**: hasOpenRoute(**Character** traveller, **Town** destination)
    - returns whether a route exists from the given traveller's current **Town** to the given destination, without 
      running into towns with other **Character** residents
- **List\<Town>**: getOpenRoute(**Character** traveller, **Town** destination)
    - returns the optimal route from the given traveller's current **Town** to the given destination, without
      running into towns with other **Character** residents
- **boolean**: travelOpenRoute(**Character** traveller, **Town** destination)
    - moves the given traveller to the given destination **Town** if an open route is available, and returns whether
      the **Character** object was successfully moved


#### Town
###### Variables
- **String**: name
    - represents the name of _this **Town**_
- **Set\<Town>**: neighbors
    - represents the adjacent **Town** objects to _this **Town**_
- **Set\<Character>**: residents
    - represents the **Character** objects residing in _this **Town**_
###### Methods
- **Set\<Town>**: getNeighbors()
    - returns the set of adjacent **Town** objects for _this **Town**_
- **Set\<Character>**: getResidents()
    - returns the set **Character** objects residing in _this **Town**_
- **boolean**: hasResidents()
    - returns whether _this **Town**_ has no residing **Character** objects

#### Character
###### Variables
- **String**: name
    - represents the name of _this **Character**_
- **Town**: town
    - represents the **Town** where _this **Character**_ is located

###### Methods
- **Town**: getTown()
    - returns the **Town** where _this **Character**_ is located
- **void**: setTown(**Town** town)
    - sets the **Town** where _this **Character**_ is located
    