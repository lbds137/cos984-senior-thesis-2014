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
    public static final Color DESERT_COLOR = new Color(255, 255, 153);
    public static final Color WOOL_COLOR = new Color(0, 204, 0);
    public static final Color GRAIN_COLOR = new Color(255, 204, 0);
    public static final Color LUMBER_COLOR = new Color(0, 102, 0);
    public static final Color BRICK_COLOR = new Color(204, 102, 0);
    public static final Color ORE_COLOR = new Color(102, 102, 102);
    public static final String DESERT_NAME = "Desert";
    public static final String WOOL_NAME = "Wool";
    public static final String GRAIN_NAME = "Grain";
    public static final String LUMBER_NAME = "Lumber";
    public static final String BRICK_NAME = "Brick";
    public static final String ORE_NAME = "Ore";
    //public static final String[] CARD_NAMES = {"Wool","Grain","Lumber","Brick","Ore"};
    public static final ArrayList<String> CARD_NAMES = 
        new ArrayList<String>(Arrays.asList("Wool","Grain","Lumber","Brick","Ore"));
    // set of 19 tiles out of which the board is created
    public static final Integer[] DEFAULT_TILES = {DESERT,BRICK,BRICK,BRICK,GRAIN,
                                                   GRAIN,GRAIN,GRAIN,LUMBER,LUMBER,
                                                   LUMBER,LUMBER,ORE,ORE,ORE,WOOL,
                                                   WOOL,WOOL,WOOL};
    
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
            case DESERT: return DESERT_COLOR;
            case WOOL: return WOOL_COLOR;
            case GRAIN: return GRAIN_COLOR;
            case LUMBER: return LUMBER_COLOR;
            case BRICK: return BRICK_COLOR;
            case ORE: return ORE_COLOR;
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
            if (resourceType == Constants.INVALID) { return 0; }
            else { return resourceType + 1; }
        }
        else { return resourceType - r.getResourceType(); }
    }
    
    /* Static methods */
    
    public static int getResourceType(String sResource) {
        String s = sResource.toLowerCase();
        s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
        switch (s) {
            case Resource.WOOL_NAME:
            case Resource.GRAIN_NAME:
            case Resource.LUMBER_NAME:
            case Resource.BRICK_NAME:
            case Resource.ORE_NAME:
                return CARD_NAMES.indexOf(s);
            default: return Constants.INVALID;
        }
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

