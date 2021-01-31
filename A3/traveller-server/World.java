package World;
import World.Town;
import World.Character;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class World {
    private Set<Town> towns;
    private Set<Character> characters;

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
        //List<Town> openRoute = new ArrayList<>();
        List<List<Town>> routesToDest = new ArrayList<List<Town>>();
        Town startTown = traveller.getTown();

        for(Town t: startTown.getNeighbors()) {
            List<Town> visited = new ArrayList<Town>();
            routesToDest.add(findAllRoutes(destination, t, visited));
        }

        for (List<Town> route : routesToDest) {
            for (Town town : route) {
                if (town.hasResidents()) {
                    break;
                }
            }
        }

        return openRoute;
    }

    public List<Town> findAllRoutes(Town goalTown, Town currentTown, List<Town> visited) {
        if (currentTown.getName().equals(goalTown.getName())) {
            visited.add(currentTown);
            return visited;
        }
        visited.add(currentTown);
        for (Town t : currentTown.getNeighbors()) {
            findAllRoutes(goalTown, t, visited);
        }
        return null;
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
