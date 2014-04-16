public class Resource implements Comparable<Resource> {
	
    /* Constants */
    
    // number of "real" resources (DESERT doesn't count)
	public static final int NUM_TYPES = 5;
    // values assigned to the types can be changed to implement a different ordering (e.g. by scarcity)
	public static final int DESERT = 0; // not a "useful" resource
    public static final int WOOL = 1;
	public static final int GRAIN = 2;
	public static final int LUMBER = 3;
	public static final int BRICK = 4;
    public static final int ORE = 5;
	
    /* Fields */
    
	private int resourceType;
	
    /* Constructors */
    
	public Resource(int resourceType) {
		switch (resourceType) {
			case DESERT: case WOOL: case GRAIN: case BRICK: case ORE:
				this.resourceType = resourceType;
                break;
			default:
				this.resourceType = DESERT; // executes for invalid resource types
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
			case DESERT: return "Desert";
			case WOOL: return "Wool";
			case GRAIN: return "Grain";
			case BRICK: return "Brick";
			case ORE: return "Ore";
			default: return "Invalid resource type"; // this shouldn't ever happen
		}
	}
    // method only useful to AI players when evaluating "value" of a resource
	public int compareTo(Resource r) {
		// natural ordering defined by values of type constants
		return this.getResourceType() - r.getResourceType();
	}
	
	/* Testing */
    
	public static void main(String[] args) {
		Resource r = new Resource(Resource.BRICK);
        Resource q = new Resource(Resource.ORE);
        System.out.println(r.toString());
        System.out.println(q.toString());
        System.out.println(r.compareTo(q));
	}
}

