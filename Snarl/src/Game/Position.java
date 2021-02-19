package Game;

public class Position {
    int x_pos;
    int y_pos;

    public Position(int x_pos, int y_pos) {
        this.x_pos = x_pos;
        this.y_pos = y_pos;
    }

    public int getX() {
        return this.y_pos;
    }

    public int getY() {
        return this.x_pos;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof Position) {
            Position pos = (Position) other;
            if (this.getX() == pos.getX() && this.getY() == pos.getY()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return (this.getX() + "," + this.getY());
    }

}
