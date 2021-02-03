# Khoury College of Computer Science

## To: Client

## From: Oliver Vazquez and Jin Shutima Han

## Date: February 2, 2021

## Re: Request for Traveller Package

Due to the fact that the requested definition of a path has no weight or cost, Dijkstra's cannot be properly implemented.
Instead, a breadth first graph search algorithm was implemented, which will return if a path is possible between the two
requested nodes, without passing through a node that contains a character.
Additionally, several getter methods were implemented to allow for the classes to access private variables.
The requested method Query() did not accept any arguments, we passed it a goal Town and a start Town.
To construct a TownNetwork, an ArrrayList of Edges is required, as well as an array of all Towns.
