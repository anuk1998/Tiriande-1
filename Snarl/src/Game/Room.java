package Game;

import java.util.ArrayList;

public class Room {
    String[][] room;
    Position roomPositionInLevel;
    int numOfRows;
    int numOfCols;
    ArrayList<Position> listOfAllPositions = new ArrayList<Position>();
    ArrayList<Position> listOfAllPositionsLevelScale = new ArrayList<Position>();
    ArrayList<Position> listOfEdgePositions = new ArrayList<Position>();
    ArrayList<Position> listOfDoorsInRoom = new ArrayList<Position>();

    //Constants for tile types
    String WALL = TileType.WALL.toString();
    String DOOR = TileType.DOOR.toString();
    String ROOM = TileType.ROOM.toString();

    public Room(Position roomPos, int rows, int cols) {
        this.roomPositionInLevel = roomPos;
        this.numOfRows = rows;
        this.numOfCols = cols;
        room = new String[numOfRows][numOfCols];
        collectEdges();
        makeRoom();
    }

    public void renderRoom() {
        for (int i=0; i<numOfRows; i++) {
            for (int j=0; j<numOfCols; j++) {
                if (j == numOfCols - 1) {
                    System.out.print(room[i][j] + "\n");
                }
                else {
                    System.out.print(room[i][j] + " ");
                }
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

    public void makeRoom() {
        for (int i = 0; i < this.numOfRows; i++) {
            for (int j = 0; j < this.numOfCols; j++) {
                Position tempPos = new Position(i, j);
                if (this.listOfEdgePositions.contains(tempPos)) {
                    this.room[i][j] = WALL;
                } else {
                    this.room[i][j] = ROOM;
                }
                listOfAllPositions.add(tempPos);
            }
        }
    }

    public void addDoor(Position p) throws IllegalArgumentException{
        if (this.listOfEdgePositions.contains(p)) {
            room[p.getRow()][p.getCol()] = DOOR;
            this.listOfDoorsInRoom.add(p);
        }
        else {
            throw new IllegalArgumentException("Given coordinate for a door is not a room coordinate on the room's boundary.");
        }
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

    public ArrayList<Position> getDoorPositions() {
        return this.listOfDoorsInRoom;
    }

    public ArrayList<Position> getListOfAllPositions() {
        return new ArrayList<>(this.listOfAllPositions);
    }

    public ArrayList<Position> getListOfAllPositionsLevelScale() {
        return new ArrayList<>(this.listOfAllPositionsLevelScale);
    }

    public void addToListOfAllPositionsLevelScale(Position p) {
        this.listOfAllPositionsLevelScale.add(p);
    }

    public String getTileInRoom(Position tilePosition) {
        return room[tilePosition.getRow()][tilePosition.getCol()];
    }

    // used for Testing tasks
    public void setTileInRoom(int row, int col, String tile) {
        this.room[row][col] = tile;
    }


}
