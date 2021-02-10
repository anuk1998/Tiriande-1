public class Hallway {
  Position [] waypoints;

}

public class Position {
  int x_pos;
  int y_pos;

  public Position(int x_pos, int y_pos) {
    this.x_pos = x_pos;
    this.y_pos = y_pos;
  }

  public int getX() {
    return x_pos;
  }

  public int getY() {
    return y_pos;
  }

  public setX(int new_x) {
    x_pos = new_x;
  }

  public setY(int new_y) {
    y_pos = new_y;
  }

}

public class Room {
  int[][] room;
  Position

}

public class Level {
  int[][] rooms;
}


X X X X X X X X
O O O O O O O O
O O O O O O O O
O O O O O O O E
X X X X X X X X
X X X X X X X X

int x_pos = __;
int y_pos = __;
