import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

class Character {
  private String name;

  public Character(String name) {
    this.name = name;
  }
}

class Town {
  private String name;
  private int id;
  private Boolean isEmpty;
  private Character character_assigned;

  public Town(String name, int id) {
    this.id = id;
    this.name = name;
    this.isEmpty = true;
    this.character_assigned = null;
  }

  void placeCharacter(Character c) {
    this.character_assigned = c;
    this.isEmpty = false;
  }

  boolean isEmpty() {
    return this.isEmpty;
  }

  int getID() {
    return this.id;
  }

  String getName() {
    return this.name;
  }
}

class Edge {
  private Town startTown;
  private Town endTown;

  public Edge(Town startTown, Town endTown) {
    this.startTown = startTown;
    this.endTown = endTown;
  }

  Town getStartTown() {
    return this.startTown;
  }

  Town getEndTown() {
    return this.endTown;
  }
}

class TownNetwork {
  private ArrayList<Edge> edges;
  private int numVertices;
  private int [][] adjMatrix;
  private Town [] vertexList;

  public TownNetwork(ArrayList<Edge> edges, Town[] vertexList) {
    this.edges = edges;
    this.numVertices= vertexList.length;
    this.vertexList = vertexList;
    this.adjMatrix = new int[this.numVertices][this.numVertices];
    for(int i = 0; i < this.edges.size(); i ++) {
      addEdge(this.edges.get(i).getStartTown().getID(), this.edges.get(i).getEndTown().getID());
    }
  }

  // Function to add an edge to the graph
  void addEdge(int start, int e) {        
      // Considering a bidirectional edge
      this.adjMatrix[start][e] = 1;
      this.adjMatrix[e][start] = 1;
  }

  // BFS, and addEdge use source code from the below source
  // https://www.geeksforgeeks.org/implementation-of-bfs-using-adjacency-matrix/
  boolean BFS(Town start, Town goal) {
    // Visited vector to so that
    // a vertex is not visited more than once
    // Initializing the vector to false as no
    // vertex is visited at the beginning
    boolean[] visited = new boolean[this.numVertices];
    Arrays.fill(visited, false);
    List<Town> q = new ArrayList<>();
    q.add(start);

    // Set source as visited
    visited[start.getID()] = true;

    Town vis;
    while (!q.isEmpty())
    {
        vis = q.get(0);

        // Print the current node
        System.out.print(vis.getName() + " ");
        q.remove(q.get(0));
        if(vis.getName().equals(goal.getName())) {
          return true;
        }

        // For every adjacent vertex to
        // the current vertex
        for(int i = 0; i < this.numVertices; i++)
        {
            if (this.adjMatrix[vis.getID()][i] == 1 && (!visited[i]) && vis.isEmpty())
            {
                  
                // Push the adjacent node to
                // the queue
                q.add(this.vertexList[i]);

                // Set
                visited[i] = true;
            }
        }
    }
    return false;
  }

  public boolean query(Town start, Town goal) {
    return BFS(start, goal);
  }
}

class Test {
  public static void main(String[] args) {

    // Create characters
    Character jeff = new Character("Jeff");
    Character eric = new Character("Eric");
    Character samantha = new Character("Samantha");
    Character jenny = new Character("Jenny");

    // Create towns
    Town boston = new Town("Boston", 0);
    Town providence = new Town("Providence", 1);
    Town newyork = new Town("New York", 2);
    Town philadelphia = new Town("Philadelphia", 3);
    Town baltimore = new Town("Baltimore", 4);
    Town washington = new Town("Washington", 5);
    Town pittsburgh = new Town("Pittsburgh", 6);
    Town cleveland = new Town("Cleveland", 7);

    newyork.placeCharacter(jeff);

    // Create edges
    Edge edge1 = new Edge(boston, providence);
    Edge edge2 = new Edge(providence, newyork);
    Edge edge3 = new Edge(newyork, philadelphia);
    Edge edge4 = new Edge(philadelphia, cleveland);
    Edge edge5 = new Edge(philadelphia, baltimore);
    Edge edge6 = new Edge(pittsburgh, washington);
    Edge edge7 = new Edge(baltimore, washington);
    Edge edge8 = new Edge(baltimore, pittsburgh);
    Edge edge9 = new Edge(philadelphia, pittsburgh);
    Edge edge10 = new Edge(boston, newyork);
    Edge edge11 = new Edge(boston, cleveland);

    // Populate edge ArrayList
    ArrayList<Edge> edges = new ArrayList<Edge>();
    edges.add(edge1);
    edges.add(edge2);
    edges.add(edge3);
    edges.add(edge4);
    edges.add(edge5);
    edges.add(edge6);
    edges.add(edge7);
    edges.add(edge8);
    edges.add(edge9);
    edges.add(edge10);
    edges.add(edge11);

    Town [] vertexList = {boston, providence, newyork, philadelphia, baltimore, washington, pittsburgh, cleveland};

    // Create TownNetwork
    TownNetwork network = new TownNetwork(edges, vertexList);
    System.out.println(network.query(boston, washington));
  }
}