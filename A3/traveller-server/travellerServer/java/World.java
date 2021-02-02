package travellerServer.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class World {
    private Set<Town> towns;
    private Set<Character> characters;
    private List<List<Town>> routesToDest = new ArrayList<List<Town>>();

    public World(Set<Town> t, Set<Character> c) {
        this.towns = t;
        this.characters = c;
    }

    /**
    returns whether a route exists from the given traveller's current
    Town to the given destination, without running into towns with other
    Character residents
     */
    public boolean hasOpenRoute(Character traveller, Town destination) {
        //if getOpenRoute returns an empty list, that assumes there is no optimal
        //route that allows the traveller to get from one town to another
        //without running into any other characters.
        //Therefore, if it is empty, there is no open route
        if(getOpenRoute(traveller,destination).size() == 0) {
            return false;
        }
        //if it is not empty, there is an open route
        return true;
    }

    /**
     returns the optimal route from the given traveller's current Town
     to the given destination, without running into towns with other Character
     residents
     */
    public List<Town> getOpenRoute(Character traveller, Town destination) {
        List<List<Town>> allValidRoutes = new ArrayList<List<Town>>();
        Town startTown = traveller.getTown();

        // finds all potential routes to the destination town from the start town
        for (Town t: startTown.getNeighbors()) {
            List<Town> visited = new ArrayList<Town>();
            findARoute(destination, t, visited);
        }

        // checks if any paths to the destination town are valid (i.e. can be reached without running into residents)
        for (List<Town> route : routesToDest) {
            boolean isValidRoute = true;

            for (Town town : route) {
                if (town.hasResidents()) {
                    isValidRoute = false;
                }
            }

            if (isValidRoute) {
                allValidRoutes.add(route);
            }
        }

        // determines the shortest valid path of all valid paths to destination town
        int minRoute = allValidRoutes.get(0).size();
        int shortestRoute = 0;
        for (int i=0; i<allValidRoutes.size(); i++) {
            if (allValidRoutes.get(i).size() < minRoute) {
                minRoute = allValidRoutes.get(i).size();
                shortestRoute = i;
            }
        }
        return allValidRoutes.get(shortestRoute);
    }

    /**
     * Recursively finds a route to the goal destination by hopping to neighboring towns until the goal
     * destination is reached, then adding it to a list of all valid routes.
     */
    public void findARoute(Town goalTown, Town currentTown, List<Town> visited) {
        visited.add(currentTown);
        if (currentTown.getName().equals(goalTown.getName())) {
            routesToDest.add(visited);
        }

        for (Town t : currentTown.getNeighbors()) {
            if (!visited.contains(t)) {
                findARoute(goalTown, t, visited);
            }
        }
    }

    /**
     moves the given traveller to the given destination Town if an open route is available,
     and returns whether the Character object was successfully moved
     */
    public boolean travelOpenRoute(Character traveller, Town destination) {
        boolean didMove = false;
        if (hasOpenRoute(traveller, destination)) {
            destination.addResident(traveller);
            traveller.setTown(destination);
            didMove = true;
        }
        return didMove;

    }


}
