package Game;

public class Position {
    int row;
    int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof Position) {
            Position pos = (Position) other;
            if (this.getRow() == pos.getRow() && this.getCol() == pos.getCol()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "[" + this.getRow() + "," + this.getCol() + "]";
    }

}
