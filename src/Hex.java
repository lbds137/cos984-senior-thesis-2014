
public class Hex {
    
	/* Constants */
	
	public static final int HEX = 6;
	public static final int[][] GRAPH = {{1,3,4},{0,2,4,5},{1,5,6},
                                         {0,4,7,8},{0,1,3,5,8,9}, 
		                                 {1,2,4,6,9,10},{2,5,10,11}, 
		                                 {3,8,12},{3,4,7,9,12,13}, 
		                                 {4,5,8,10,13,14},
		                                 {5,6,9,11,14,15},
		                                 {6,10,15},{7,8,13,16}, 
		                                 {8,9,12,14,16,17}, 
		                                 {9,10,13,15,17,18},
		                                 {10,11,14,18},{12,13,17},
		                                 {13,14,16,18},{14,15,17}};
	public static final int[][] INTERSECTIONS = {{0,1,2,8,9,10},
                                                 {2,3,4,10,11,12},
                                                 {4,5,6,12,13,14},
                                                 {7,8,9,17,18,19},
                                                 {9,10,11,19,20,21},
                                                 {11,12,13,21,22,23},
                                                 {13,14,15,23,24,25},
                                                 {16,17,18,27,28,29},
                                                 {18,19,20,29,30,31},
                                                 {20,21,22,31,32,33},
                                                 {22,23,24,33,34,35},
                                                 {24,25,26,35,36,37},
                                                 {28,29,30,38,39,40},
                                                 {30,31,32,40,41,42},
                                                 {32,33,34,42,43,44},
                                                 {34,35,36,44,45,46},
                                                 {39,40,41,47,48,49},
                                                 {41,42,43,49,50,51},
                                                 {43,44,45,51,52,53}};
	
    /* Fields */
    
    private int id;
    private Resource resource;
    private int diceRoll;
    
    /* Constructors */
    
    public Hex(int id, Resource resource, int diceRoll) {
        this.id = id;
        this.resource = resource;
        this.diceRoll = diceRoll;
    }
    
    /* Getters */
    
    public int getId() {
        return id;
    }
    public Resource getResource() {
        return resource;
    }
    public int getDiceRoll() {
        return diceRoll;
    }
    
    /* Inherits / overrides */
    
	@Override
    public String toString() {
        String s = (char) (id + 'A') + " : ";
        for (int i = 0; i < HEX; i++) {
            s += INTERSECTIONS[id][i];
            if (i + 1 != HEX) s += " ";
            else s += "; " + resource.toString() + " " + diceRoll;
        }
        return s;
    }
	
	/* Testing */
	
	public static void main(String[] args) {
		Hex h = new Hex(1, new Resource(Resource.BRICK), 8);
		System.out.println(h.toString());
	}
}
