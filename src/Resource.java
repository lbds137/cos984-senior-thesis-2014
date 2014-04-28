
public class Resource implements Comparable<Resource> {
    
    /* Constants */
    
    // number of "real" resources (DESERT doesn't count)
    public static final int NUM_TYPES = 5;
    // values assigned to the types can be changed to implement a different ordering (e.g. by scarcity)
    public static final int DESERT = -1; // not a "useful" resource
    public static final int WOOL = 0;
    public static final int GRAIN = 1;
    public static final int LUMBER = 2;
    public static final int BRICK = 3;
    public static final int ORE = 4;
    public static final String DESERT_NAME = "Desert";
    public static final String WOOL_NAME = "Wool";
    public static final String GRAIN_NAME = "Grain";
    public static final String LUMBER_NAME = "Lumber";
    public static final String BRICK_NAME = "Brick";
    public static final String ORE_NAME = "Ore";
    // set of 19 tiles out of which the board is created
    public static final Integer[] TILES = {DESERT,BRICK,BRICK,BRICK,GRAIN,
                                        GRAIN,GRAIN,GRAIN,LUMBER,LUMBER,
                                        LUMBER,LUMBER,ORE,ORE,ORE,WOOL,
                                        WOOL,WOOL,WOOL};
	public static final int[] MAX_CARDS = {19,19,19,19,19};
    
    /* Fields */
    
    private int resourceType;
    
    /* Constructors */
    
    public Resource(int resourceType) {
        switch (resourceType) {
            case DESERT: case WOOL: case GRAIN: case LUMBER: case BRICK: case ORE:
                this.resourceType = resourceType;
                break;
            default:
                this.resourceType = Constants.INVALID;
                break;
        }
    }
    
    /* Getters */
    
    public int getResourceType() {
        return resourceType;
    }
    
    /* Inherits / overrides */
    
    @Override
    public boolean equals(Resource r) {
        return compareTo(r) == 0;
    }
    @Override
    public String toString() {
        switch (resourceType) {
            case DESERT: return DESERT_NAME;
            case WOOL: return WOOL_NAME;
            case GRAIN: return GRAIN_NAME;
            case LUMBER: return LUMBER_NAME;
            case BRICK: return BRICK_NAME;
            case ORE: return ORE_NAME;
            default: return "Invalid resource";
        }
    }
    // method only useful to AI players when evaluating "value" of a resource
    public int compareTo(Resource r) {
        // valid resource > null, invalid resource = null
        if (r == null) {
            if (resourceType == Constants.INVALID) return 0;
            else return resourceType + 1;
        }
        else return resourceType - r.getResourceType();
    }
    
    /* Testing */
    
    public static void main(String[] args) {
        Resource r = new Resource(Resource.BRICK);
        Resource q = new Resource(Resource.ORE);
        Resource s = null;
        System.out.println(r.toString());
        System.out.println(q.toString());
        System.out.println(r.compareTo(q));
        System.out.println(r.equals(q));
        System.out.println(q.compareTo(s));
        System.out.println(q.equals(s));
    }
}

