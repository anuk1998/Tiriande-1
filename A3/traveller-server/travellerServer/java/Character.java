package travellerServer.java;

public class Character {
    private String name;
    private Town town;

    public Character(String n, Town t) {
        this.name = n;
        this.town = t;
    }

    public Town getTown() {
        return this.town;
    }

    public void setTown(Town t) {
        this.town = t;
    }

}
