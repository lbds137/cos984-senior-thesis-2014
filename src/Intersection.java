
public class Intersection {
    
    /* Constants */
    
    public static final int MAX_DEGREE = 3;
    public static final int[][] GRAPH = {{1,8},{0,2},{1,3,10},{2,4},
                                         {3,5,12},{4,6},{5,14},{8,17},
                                         {0,7,9},{8,10,19},{2,9,11},
                                         {10,12,21},{4,11,13},
                                         {12,14,23},{6,13,15},{14,25},
                                         {17,27},{7,16,18},{17,19,29},
                                         {9,18,20},{19,21,31},
                                         {11,20,22},{21,23,33},
                                         {13,22,24},{23,25,35},
                                         {15,24,26},{25,37},{16,28},
                                         {27,29,38},{18,28,30},
                                         {29,31,40},{20,30,32},
                                         {31,33,42},{22,32,34},
                                         {33,35,44},{24,34,36},
                                         {35,37,46},{26,36},{28,39},
                                         {38,40,47},{30,39,41},
                                         {40,42,49},{32,41,43},
                                         {42,44,51},{34,43,45},
                                         {44,46,53},{36,45},{39,48},
                                         {47,49},{41,48,50},{49,51},
                                         {43,50,52},{51,53},{45,52}};
    public static final int[][] HEXES = {{0},{0},{0,1},{1},{1,2},{2},
                                         {2},{3},{0,3},{0,3,4},
                                         {0,1,4},{1,4,5},{1,2,5},
                                         {2,5,6},{2,6},{6},{7},{3,7},
                                         {3,7,8},{3,4,8},{4,8,9},
                                         {4,5,9},{5,9,10},{5,6,10},
                                         {6,10,11},{6,11},{11},{7},
                                         {7,12},{7,8,12},{8,12,13},
                                         {8,9,13},{9,13,14},{9,10,14},
                                         {10,14,15},{10,11,15},
                                         {11,15},{11},{12},{12,16},
                                         {12,13,16},{13,16,17},
                                         {13,14,17},{14,17,18},
                                         {14,15,18},{15,18},{15},{16},
                                         {16},{16,17},{17},{17,18},
                                         {18},{18}};
    
    /* Fields */
    
    private int id;
    private Player player;
    private Building building;
    private Port port;
    private ArrayList<Hex> hexes;

    /* Constructors */
    
    public Intersection(int id, Port port, ArrayList<Hex> hexes) {
        this.id = id;
        this.player = null;
        this.building = new Building(Building.OPEN);
        this.port = port;
        this.hexes = hexes;
    }

    /* Getters */
    
    public int getId() {
        return id;
    }
    public Player getPlayer() {
        return player;
    }
    public Building getBuilding() {
        return building;
    }
    public Port getPort() {
        return port;
    }
    public ArrayList<Hex> getHexes() {
        return hexes;
    }
    
    /* Inherits / overrides */
    
    @Override
    public String toString() {
		String sPlayer = "Unowned";
		if (player != null) sPlayer = player.toString(); 
        return id + " : " + sPlayer + ", " + building.toString() + ", " + port.toString();
    }
    
    /* Operations */
    
    public boolean upgrade(Player player) {
        if (this.player == null) this.player = player;
        return this.player == player && this.building.upgrade();
    }
}

