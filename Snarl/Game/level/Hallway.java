package level;

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

    public void connectHallwayWaypoints() {
        ArrayList<Position> waypointsAndDoors = new ArrayList<Position>();

        waypointsAndDoors.add(this.startPosition);
        for (Position wp : this.waypoints) {
            waypointsAndDoors.add(wp);
        }
        waypointsAndDoors.add(this.endPosition);

        // JUST FOR DEBUGGING
        //System.out.println("About to print waypointsAndDoors");
        //for (Position p : waypointsAndDoors) {
        //    System.out.println("(" + p.getX() + ", " + p.getY() + ")");
        //}
        //////////////////////

        for (int i=1; i<waypointsAndDoors.size(); i++) {
            int tempX1 = waypointsAndDoors.get(i).getX();
            int tempY1 = waypointsAndDoors.get(i).getY();

            int tempX2 = waypointsAndDoors.get(i-1).getX();
            int tempY2 = waypointsAndDoors.get(i-1).getY();

            if (tempX1 == tempX2) {
                int min = (tempY1 <= tempY2) ? tempY1 : tempY2;
                int max = (tempY1 >= tempY2) ? tempY1 : tempY2;
                for (int j=min; j<max+1; j++) {
                    if ((j == this.startPosition.getY() && tempX1 == this.startPosition.getX()) ||
                            (j == this.endPosition.getY() && tempX2 == this.endPosition.getX())) {
                        continue;
                    }
                    this.allHallwayPositions.add(new Position(j, tempX1));
                }
            }
            else if (tempY1 == tempY2) {
                int min = (tempX1 <= tempX2) ? tempX1 : tempX2;
                int max = (tempX1 >= tempX2) ? tempX1 : tempX2;
                for (int j=min; j<max; j++) {
                    if (j == this.startPosition.getX() && tempY1 == this.startPosition.getY() || 
                            j == this.endPosition.getX() && tempY2 == this.endPosition.getY()) {

                        continue;
                    }

                    this.allHallwayPositions.add(new Position(tempY1, j));
                }
            }
        }


        // DEBUG PURPOSES ONLY
        // System.out.println("ALLHALLWAYPOSITIONS::::::");
        //for (Position p : allHallwayPositions) {
        //    System.out.println("(" + p.getY() + ", " + p.getX() + ")");
        //}
        //////////////////////

    }

}

