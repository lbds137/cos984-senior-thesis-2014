
public final class Constants  {

    // basic board structure information
    public static final int MAX_DEG = 3;
    public static final int HEX = 6;
    public static final int[][] H_GRAPH = {{1,3,4},{0,2,4,5},{1,5,6},
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
    public static final int[][] I_GRAPH = {{1,8},{0,2},{1,3,10},{2,4},
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
    public static final int[][] H_I_MAP_GRAPH = {{0,1,2,8,9,10},
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
    public static final int[][] PORT_LOCATIONS = {{0,1},{3,4},{7,17},
                                                  {14,15},{26,37},{28,38},
                                                  {45,46},{47,48},{50,51}};
    // players
    public static final int NUM_PLAYERS = 4;
    public static final int GAIA = -1; // placeholder, not real player
    public static final int BLUE = 0;
    public static final int ORANGE = 1;
    public static final int RED = 2;
    public static final int WHITE = 3;
    // resource types
    public static final int NUM_RESOURCES = 5;
    public static final int DESERT = -1; // not a "useful" resource
    public static final int BRICK = 0;
    public static final int GRAIN = 1;
    public static final int LUMBER = 2;
    public static final int ORE = 3;
    public static final int WOOL = 4;
    // port types
    public static final int INLAND = -1; // not a port
    /* remaining port types same as resources */
    public static final int PORT = 5; // generic 3:1 port
    // building types
    public static final int OPEN = -1;
    public static final int SETTLEMENT = 0;
    public static final int CITY = 1;
    // dev card types
    public static final int KNIGHT = 0;
    public static final int ROAD = 1;
    public static final int PLENTY = 2;
    public static final int MONOPOLY = 3;
    public static final int CHAPEL = 4;
    public static final int UNIVERSITY = 5;
    public static final int PALACE = 6;
    public static final int LIBRARY = 7;
    public static final int MARKET = 8;
    // resource tiles
    public static final Integer[] LAND = {DESERT,BRICK,BRICK,BRICK,GRAIN,
                                          GRAIN,GRAIN,GRAIN,LUMBER,LUMBER,
                                          LUMBER,LUMBER,ORE,ORE,ORE,WOOL,
                                          WOOL,WOOL,WOOL};
    // ports
    public static final Integer[] PORTS = {PORT,PORT,PORT,PORT,BRICK,GRAIN,
                                           LUMBER,ORE,WOOL};
    // dice roll chits
    public static final Integer[] DICE_ROLLS = {2,3,3,4,4,5,5,6,6,7,8,
                                                8,9,9,10,10,11,11,12};
    // dev cards
    public static final Integer[] DEV_CARDS = {KNIGHT,KNIGHT,KNIGHT,KNIGHT,
                                               KNIGHT,KNIGHT,KNIGHT,KNIGHT,
                                               KNIGHT,KNIGHT,KNIGHT,KNIGHT,
                                               KNIGHT,KNIGHT,ROAD,ROAD,
                                               PLENTY,PLENTY,MONOPOLY,MONOPOLY,
                                               CHAPEL,UNIVERSITY,PALACE,
                                               LIBRARY,MARKET};
    // build limits for each player
    public static final int MAX_ROADS = 15;
    public static final int MAX_SETTLEMENTS = 5;
    public static final int MAX_CITIES = 4;
    // text interface colors for terminal (interface sub-project on hold)
    public static final String ANSI_RESET = "\u001b[0m";
    public static final String ANSI_RESET_BG = "\u001b[49m";
    public static final String ANSI_BLACK = "\u001b[30m";
    public static final String ANSI_BLACK_INTENSE = "\u001b[30;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_RED_INTENSE = "\u001b[31;1m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_GREEN_INTENSE = "\u001b[32;1m";
    public static final String ANSI_YELLOW = "\u001b[33m";
    public static final String ANSI_YELLOW_INTENSE = "\u001b[33;1m";
    public static final String ANSI_YELLOW_BG_INTENSE = "\u001b[43;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_BLUE_INTENSE = "\u001b[34;1m";
    public static final String ANSI_PURPLE = "\u001b[35m";
    public static final String ANSI_PURPLE_INTENSE = "\u001b[35;1m";
    public static final String ANSI_CYAN = "\u001b[36m";
    public static final String ANSI_CYAN_INTENSE = "\u001b[36;1m";
    public static final String ANSI_WHITE = "\u001b[37m";
    public static final String ANSI_WHITE_INTENSE = "\u001b[37;1m";
    
    private Constants(){
        throw new AssertionError();
    }
}