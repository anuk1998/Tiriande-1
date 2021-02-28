package Game;

import java.util.ArrayList;

public class Hallway {
    ArrayList<Position> waypoints = new ArrayList<Position>();
    ArrayList<Position> allHallwayPositions = new ArrayList<Position>();
    Position startPosition;
    Position endPosition;

    public Hallway(Position startPosition, Position endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public void addAWaypoint(Position waypoint) {
        this.waypoints.add(waypoint);
    }

    public ArrayList<Position> getWaypoints() {
        return this.waypoints;
    }

    public ArrayList<Position> getAllHallwayPositions() {
        return this.allHallwayPositions;
    }

    public Position getStartPositionOfHallway() {
        return this.startPosition;
    }

    public Position getEndPositionOfHallway() {
        return this.endPosition;
    }


    //This method creates a list of all waypoints and door points (start/end positions),
    //then connects all the points in the Level plane in between that list using 'X's
    //  e.g. <door/"|"> . . . . <waypoint-1 "X">
    //                                .
    //                                .
    //                          <waypoint-2 "X"> . . . . <door/"|">
    // Turns into a filled-in hallways with "X"s:
    //            | X X X X X
    //                      X
    //                      X
    //                      X X X X X |
    public void connectHallwayWaypoints() {
        ArrayList<Position> waypointsAndDoors = new ArrayList<Position>();

        waypointsAndDoors.add(this.startPosition);
        for (Position wp : this.waypoints) {
            waypointsAndDoors.add(wp);
        }
        waypointsAndDoors.add(this.endPosition);

        for (int i=1; i<waypointsAndDoors.size(); i++) {
            int tempRow1 = waypointsAndDoors.get(i).getRow();
            int tempCol1 = waypointsAndDoors.get(i).getCol();

            int tempRow2 = waypointsAndDoors.get(i-1).getRow();
            int tempCol2 = waypointsAndDoors.get(i-1).getCol();

            if (tempRow1 == tempRow2) {
                int min = (tempCol1 <= tempCol2) ? tempCol1 : tempCol2;
                int max = (tempCol1 >= tempCol2) ? tempCol1 : tempCol2;
                for (int j=min; j<max+1; j++) {
                    if ((j == this.startPosition.getCol() && tempRow1 == this.startPosition.getRow()) ||
                            (j == this.endPosition.getCol() && tempRow2 == this.endPosition.getRow())) {
                        continue;
                    }
                    this.allHallwayPositions.add(new Position(tempRow1, j));
                }
            }
            else if (tempCol1 == tempCol2) {
                int min = (tempRow1 <= tempRow2) ? tempRow1 : tempRow2;
                int max = (tempRow1 >= tempRow2) ? tempRow1 : tempRow2;
                for (int j=min; j<max; j++) {
                    if (j == this.startPosition.getRow() && tempCol1 == this.startPosition.getCol() ||
                            j == this.endPosition.getRow() && tempCol2 == this.endPosition.getCol()) {

                        continue;
                    }

                    this.allHallwayPositions.add(new Position(j, tempCol1));
                }
            }
        }

    }

}

