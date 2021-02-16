package level;

import java.util.ArrayList;
import java.util.Set;

public class Room {
    String[][] room;
    Position roomPositionInLevel;
    int roomHorizontalLength;
    int roomVerticalLength;
    Set<Player> playersInRoom;
    Set<Adversary> adversariesInRoom;
    ArrayList<Position> listOfAllPositions = new ArrayList<Position>();
    ArrayList<Position> listOfEdgePositions = new ArrayList<Position>();
    ArrayList<Position> listOfDoorsInRoom = new ArrayList<Position>();

    public Room(Position roomPos, int horiz, int vertic) {
        this.roomPositionInLevel = roomPos;
        this.roomHorizontalLength = horiz;
        this.roomVerticalLength = vertic;
        room = new String[roomVerticalLength][roomHorizontalLength];
        makeRoom();
        collectEdges();
    }

    //removes the given player from that room
    public void removePlayer(Player p) {
    }

    // UNUSED IN THIS MILESTONE, WILL TEST AT LATER DATE
    public boolean isValidMove(Position from, Position to) {
        return true;
    }

    public int getHorizontalLength() {
        return this.roomHorizontalLength;
    }

    public int getVerticalLength() {
        return this.roomVerticalLength;
    }

    public Position getRoomStartPositionInLevel() {
        return this.roomPositionInLevel;
    }

    public String getTileInRoom(Position tilePosition) {
        return room[tilePosition.getX()][tilePosition.getY()];
    }

    public void makeRoom() {
        for (int i = 0; i < this.roomVerticalLength; i++) {
            for (int j = 0; j < this.roomHorizontalLength ; j++) {
                Position tempPos = new Position(i, j);
                this.room[i][j] = "■";
                listOfAllPositions.add(tempPos);
            }
        }
    }

    private void collectEdges() {
        // puts all edge positions in a list
        for (int i=0; i<this.roomHorizontalLength; i++) {
            Position tempPosX = new Position(i, 0);
            Position tempPosY = new Position(i, this.roomVerticalLength - 1);
            listOfEdgePositions.add(tempPosX);
            listOfEdgePositions.add(tempPosY);
        }
        for (int i=0; i<this.roomVerticalLength; i++) {
            Position tempPosX = new Position(0, i);
            Position tempPosY = new Position(this.roomHorizontalLength - 1, i);

            // to prevent adding corner squares twice
            if (i != 0 && i != this.roomVerticalLength - 1) {
                listOfEdgePositions.add(tempPosX);
                listOfEdgePositions.add(tempPosY);
            }
        }

        for (Position edge : this.listOfEdgePositions) {
            System.out.println("(" + edge.getY() + ", " + edge.getX() + ")");
        }
    }

    public void addKey(Position p) throws IllegalArgumentException {
        try {
            room[p.getX()][p.getY()] = "*";
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Given coordinate for key is beyond bounds of the room.");
        }

    }

    public void addExit(Position p) throws IllegalArgumentException{
        try{
            room[p.getX()][p.getY()] = "O";
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Given coordinate for exit is beyond bounds of the room.");
        }

    }

    public void addDoor(Position p) throws IllegalArgumentException{
        if (this.listOfEdgePositions.contains(p)) {
            room[p.getX()][p.getY()] = "|";
            this.listOfDoorsInRoom.add(p);
        }
        else {
            throw new IllegalArgumentException("Given coordinate for a door is not a room coordinate on the room's boundary.");
        }
    }

    public Position getKeyPosition() {
        for (int i = 0; i < this.roomVerticalLength; i++) {
            for (int j = 0; j < this.roomHorizontalLength; j++) {
                if (room[i][j].equals("*")) {
                    System.out.println("Got into if statement");
                    System.out.println("i: " + i + " j: " + j);
                    return new Position(j, i);

                }
            }
        }

        return null;
    }

    public ArrayList<Position> getTilePosition(int x, int y) {
        ArrayList<Position> tilePositions = new ArrayList<Position>();

        for (int i = 0; i < this.roomVerticalLength; i++) {
            for (int j = 0; j < this.roomHorizontalLength; j++) {
                if (room[i][j].equals("■")) {
                    tilePositions.add(new Position(j, i));

                }
            }
        }
        return tilePositions;
    }

    public ArrayList<Position> getDoorPositions() {
        return this.listOfDoorsInRoom;
    }

    public Position getExitPosition() {
        for (int i = 0; i < this.roomVerticalLength; i++) {
            for (int j = 0; j < this.roomHorizontalLength; j++) {
                if (room[i][j].equals("O")) {
                    return new Position(j, i);

                }
            }
        }
        return null;
    }


}
