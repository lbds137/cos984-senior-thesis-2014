
public final class Constants  {

    // filenames we need
    public static final String HEX_GRAPH_FILE = "h-graph.soc";
    public static final String INTERSECTION_GRAPH_FILE = "i-graph.soc";
    public static final String HEX_INTERSECTION_MAPPING_FILE = "h-i-mapping.soc";

    // max degree
    public static final int MAX_DEG = 3;
  
    // number of vertices in a hex
    public static final int HEX = 6;
    
    // dice roll chits
    public static final Integer[] DICE = {2,3,3,4,4,5,5,6,6,7,8,8,9,9,10,10,11,11,12};
    
    // resource tiles
    public static final Integer[] LAND = {DESERT,BRICK,BRICK,BRICK,GRAIN,GRAIN,GRAIN,GRAIN,LUMBER,LUMBER,LUMBER,LUMBER,ORE,ORE,ORE,WOOL,WOOL,WOOL,WOOL};
    
    // player colors
    public static final int GAIA = -1;
    public static final int BLUE = 0;
    public static final int ORANGE = 1;
    public static final int RED = 2;
    public static final int WHITE = 3;
    
    // resource types
    public static final int DESERT = -1;
    public static final int BRICK = 0;
    public static final int GRAIN = 1;
    public static final int LUMBER = 2;
    public static final int ORE = 3;
    public static final int WOOL = 4;
    
    // building types
    public static final int OPEN = -1;
    public static final int SETTLEMENT = 0;
    public static final int CITY = 1;
    
    // dev card types
    public static final int KNIGHT = 0;
    public static final int ROAD = 1;
    public static final int PLENTY = 2;
    public static final int MONOPOLY = 3;
    public static final int VP = 4;
    
    private Constants(){
        throw new AssertionError();
    }
}