package travellerServer;
import travellerServer.Character;

 import java.util.Set;

public class Town {
    private String name;
    private Set<Town> neighbors;
    private Set<Character> residents;

    public Town(String n,Set<Town> ne, Set<Character> r ) {
        this.name = n;
        this.neighbors = ne;
        this.residents = r;
    }

    public Set<Town> getNeighbors() {
        return this.neighbors;
    }

    //added this method to help with getOpenRoute
    public String getName() {
        return this.name;
    }

    public void addResident(Character c) {
        this.residents.add(c);
    }

    public Set<Character> getResidents() {
        return this.residents;
    }

    public boolean hasResidents() {
        return !(this.residents.isEmpty());
    }





}
