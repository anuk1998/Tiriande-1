package Game;

import java.util.ArrayList;

public class Room {
    String[][] room;
    Position roomPositionInLevel;
    int numOfRows;
    int numOfCols;
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

    // used for Testing tasks
    public void setTileInRoom(int row, int col, String tile) {
        this.room[row][col] = tile;
    }

    public void makeRoom() {
        for (int i = 0; i < this.numOfRows; i++) {
            for (int j = 0; j < this.numOfCols; j++) {
                Position tempPos = new Position(i, j);
                this.room[i][j] = ".";
//                this.room[i][j] = "■";
                listOfAllPositions.add(tempPos);
            }
        }
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

    public void addDoor(Position p) throws IllegalArgumentException{
        if (this.listOfEdgePositions.contains(p)) {
            room[p.getRow()][p.getCol()] = "|";
            this.listOfDoorsInRoom.add(p);
        }
        
        else {
            throw new IllegalArgumentException("Given coordinate for a door is not a room coordinate on the room's boundary.");
        }
    }

    public ArrayList<Position> getDoorPositions() {
        return this.listOfDoorsInRoom;
    }


}
