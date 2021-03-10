package Game;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class Room {
    String[][] room;
    Position roomPositionInLevel;
    int numOfRows;
    int numOfCols;
    Set<Player> playersInRoom;
    Set<IAdversary> adversariesInRoom;
    ArrayList<Position> listOfAllPositions = new ArrayList<Position>();
    ArrayList<Position> listOfEdgePositions = new ArrayList<Position>();
    ArrayList<Position> listOfDoorsInRoom = new ArrayList<Position>();

    public Room(Position roomPos, int rows, int cols) {
        this.roomPositionInLevel = roomPos;
        this.numOfRows = rows;
        this.numOfCols = cols;
        room = new String[numOfRows][numOfCols];
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

    public int getNumOfRows() {
        return this.numOfRows;
    }

    public int getNumOfCols() {
        return this.numOfCols;
    }

    public Position getRoomOriginInLevel() {
        return this.roomPositionInLevel;
    }

    public String getTileInRoom(Position tilePosition) {
        return room[tilePosition.getRow()][tilePosition.getCol()];
    }

    public void makeRoom() {
        for (int i = 0; i < this.numOfRows; i++) {
            for (int j = 0; j < this.numOfCols; j++) {
                Position tempPos = new Position(i, j);
                this.room[i][j] = "■";
                listOfAllPositions.add(tempPos);
            }
        }
    }

    public void createRoomFromJSON(JSONArray inputArray) throws JSONException {
        for (int i=0; i<inputArray.length(); i++) {
            JSONArray innerArray = inputArray.getJSONArray(i);
            for (int j = 0; j < innerArray.length(); j++) {
                int num = innerArray.getInt(j);
                if (num == 0) {
                    this.room[i][j] = "#";
                } else if (num == 1) {
                    this.room[i][j] = "■";
                } else if (num == 2) {
                    this.room[i][j] = "|";
                    this.listOfDoorsInRoom.add(new Position(i, j));
                } else {
                    // do something, invalid number
                }
            }
        }
    }

    private void renderRoom() {
        StringBuilder levelASCII = new StringBuilder();
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                if (j == numOfCols - 1) {
                    levelASCII.append(room[i][j] + "\n");
                } else {
                    levelASCII.append(room[i][j] + " ");
                }
            }
        }
        System.out.println(levelASCII.toString());
    }

    private void collectEdges() {
        // puts all edge positions in a list
        for (int i=0; i<this.numOfRows; i++) {
            Position tempPosRow = new Position(i, 0);
            Position tempPosCol = new Position(i, this.numOfCols - 1);
            listOfEdgePositions.add(tempPosRow);
            listOfEdgePositions.add(tempPosCol);
        }
        for (int i=0; i<this.numOfCols; i++) {
            Position tempPosRow = new Position(0, i);
            Position tempPosCol = new Position(this.numOfRows - 1, i);

            // to prevent adding corner squares twice
            if (i != 0 && i != this.numOfCols - 1) {
                listOfEdgePositions.add(tempPosRow);
                listOfEdgePositions.add(tempPosCol);
            }
        }
    }

    public void addKey(Position p) throws ArrayIndexOutOfBoundsException {
        try {
            room[p.getRow()][p.getCol()] = "*";
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Given coordinate for key is beyond bounds of the room.");
        }

    }

    public void addExit(Position p) throws ArrayIndexOutOfBoundsException{
        try{
            room[p.getRow()][p.getCol()] = "●";
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Given coordinate for exit is beyond bounds of the room.");
        }

    }

    public void addDoor(Position p) throws IllegalArgumentException{
        if (this.listOfEdgePositions.contains(p)) {
            room[p.getRow()][p.getCol()] = "|";
            this.listOfDoorsInRoom.add(p);
        }
        
        else {
            throw new IllegalArgumentException("Given coordinate for a door is not a room coordinate on the room's boundary.");
        }
    }

    public Position getKeyPosition() {
        for (int i = 0; i < this.numOfRows; i++) {
            for (int j = 0; j < this.numOfCols; j++) {
                if (room[i][j].equals("*")) {
                    return new Position(i, j);
                }
            }
        }
        return null;
    }

    public ArrayList<Position> getDoorPositions() {
        return this.listOfDoorsInRoom;
    }

    public Position getExitPosition() {
        for (int i = 0; i < this.numOfRows; i++) {
            for (int j = 0; j < this.numOfCols; j++) {
                if (room[i][j].equals("O") || room[i][j].equals("●")) {
                    return new Position(i, j);

                }
            }
        }
        return null;
    }

    public Position placeCharacterInRandomLocation(ICharacter c) {
        Random rand = new Random();
        int randomIndex = rand.nextInt(listOfAllPositions.size());
        Position randomPos = listOfAllPositions.get(randomIndex);
        String tile = this.room[randomPos.getRow()][randomPos.getCol()];

        while (tile.equals("#") || tile.equals("*") || tile.equals("O")
                || tile.equals("●") || tile.equals("P") || tile.equals("A")) {
            randomIndex = rand.nextInt(listOfAllPositions.size());
            randomPos = listOfAllPositions.get(randomIndex);
            tile = this.room[randomPos.getRow()][randomPos.getCol()];
        }
        Position randomStartPosInRoom = new Position(this.roomPositionInLevel.getRow() + randomPos.getRow(),
                this.roomPositionInLevel.getCol() + randomPos.getCol());

        c.setCharacterPosition(randomStartPosInRoom);
        return randomStartPosInRoom;
    }



}
