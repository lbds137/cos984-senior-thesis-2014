import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

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
    public static final Color[] COLORS = {new Color(0xFF, 0xFF, 0x99), new Color(0, 0xCC, 0),
                                          new Color(0xFF, 0xCC, 0), new Color(0, 0x66, 0), 
                                          new Color(0xCC, 0x66, 0), new Color(0x66, 0x66, 0x66)};
    public static final ArrayList<String> NAMES = 
        new ArrayList<String>(Arrays.asList("Desert","Wool","Grain","Lumber","Brick","Ore"));
    
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
    
    public Color getColor() {
        switch (resourceType) {
            case WOOL: case GRAIN: case LUMBER: 
            case BRICK: case ORE: case DESERT:
                return COLORS[resourceType + 1];
            default: return null;
        }
    }
    
    /* Inherits / overrides */
    
    //@Override
    public boolean equals(Resource r) {
        return compareTo(r) == 0;
    }
    @Override
    public String toString() {
        switch (resourceType) {
            case WOOL: case GRAIN: case LUMBER: 
            case BRICK: case ORE: case DESERT:
                return NAMES.get(resourceType + 1);
            default: return "Invalid resource";
        }
    }
    // method only useful to AI players when evaluating "value" of a resource
    public int compareTo(Resource r) {
        // valid resource > null, invalid resource = null
        if (r == null) {
            if (resourceType == Constants.INVALID) { return 0; }
            else { return resourceType + 1; }
        }
        else { return resourceType - r.getResourceType(); }
    }
    
    /* Static methods */
    
    public static int getResourceType(String sResource) {
        String s = sResource.toLowerCase();
        s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
        int i = NAMES.indexOf(s);
        switch (i - 1) {
            case WOOL: case GRAIN: case LUMBER:
            case BRICK: case ORE: case DESERT:
                return i - 1;
            default: return Constants.INVALID;
        }
    }
}
