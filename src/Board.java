import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Board {

    private Intersection[] intersections;
    private Road[][] iGraph;
    private Hex[] hexes;
    private boolean[][] hGraph;
    private int robberIndex; // location of the robber in hexes[]
    
    // for each player, how many of [pieceType] are there?
    private int[] roadsBuilt;
    private int[] roadsFree;
    private int[] settlementsBuilt;
    private int[] settlementsFree;
    private int[] citiesBuilt;
    private int[] citiesFree;
    
    /* Constructors */
    
    // build a new random board
    public Board() {
        initIGraph();
        initHGraph();
        initPieces();
    }
    // build a board from a saved state
    public Board(String state) {
        // todo
    }
    
    private void initIGraph() {
        int[][] g = Constants.I_GRAPH;
        int n = g.length;
        
        intersections = new Intersection[n];
        for (int i = 0; i < n; i++) {
            intersections[i] = new Intersection(i);
        }
        
        iGraph = new Road[n][n];
        for (int i = 0; i < n; i++) {
            int id = i;
            for (int j = 0; j < g[i].length; j++) {
                int destId = g[i][j];
                if (iGraph[id][destId] == null) {
                    iGraph[id][destId] = new Road(id, destId);
                    iGraph[destId][id] = iGraph[id][destId];
                }
            }
        }
    }
    private void initHGraph() {
        int[][] g = Constants.H_GRAPH;
        int n = g.length;
        ArrayList<Integer> shuffledLand = new ArrayList<Integer>(Arrays.asList(Constants.LAND));
        Collections.shuffle(shuffledLand, new Random());
        robberIndex = shuffledLand.indexOf(Constants.DESERT); // robber starts in desert
        ArrayList<Integer> shuffledDiceRolls = getShuffledDiceRolls(robberIndex);
        
        hexes = new Hex[n];
        for (int i = 0; i < n; i++) {
            hexes[i] = new Hex(i, shuffledLand.get(i), shuffledDiceRolls.get(i));
        }
        
        hGraph = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            int id = i;
            for (int j = 0; j < g[i].length; j++) {
                int destId = g[i][j];
                if (!hGraph[id][destId]) {
                    hGraph[id][destId] = true;
                    hGraph[destId][id] = hGraph[id][destId];
                }
            }
        }
    }
    private void initPieces() {
        roadsBuilt = new int[Constants.NUM_PLAYERS];
        roadsFree = new int[Constants.NUM_PLAYERS];
        settlementsBuilt = new int[Constants.NUM_PLAYERS];
        settlementsFree = new int[Constants.NUM_PLAYERS];
        citiesBuilt = new int[Constants.NUM_PLAYERS];
        citiesFree = new int[Constants.NUM_PLAYERS];
        
        for (int i = 0; i < Constants.NUM_PLAYERS; i++) {
            roadsFree[i] = Constants.MAX_ROADS;
            settlementsFree[i] = Constants.MAX_SETTLEMENTS;
            citiesFree[i] = Constants.MAX_CITIES;
            // the [piece]Built arrays default to 0, which is what we want
        }    
    }
    // Dice roll chits 6 and 8 cannot be adjacent, so we have to work a bit harder
    private ArrayList<Integer> getShuffledDiceRolls(int desertIndex) {
        int[][] g = Constants.H_GRAPH;
        int n = g.length;
        boolean conflict;
        ArrayList<Integer> shuffledDiceRolls;

        do {
            conflict = false;
            shuffledDiceRolls = new ArrayList<Integer>(Arrays.asList(Constants.DICE_ROLLS));
            Collections.shuffle(shuffledDiceRolls, new Random());
            // Make sure die number 7 is on the desert tile
            shuffledDiceRolls.add(desertIndex, shuffledDiceRolls.remove(shuffledDiceRolls.indexOf(7)));
            
            // test generation for 6 and 8 adjacencies; regenerate if 6 and 8 end up adjacent
            for (int i = 0; i < n; i++) {
                if (conflict) break;
                for (int j = 0; j < g[i].length; j++) {
                    if (conflict) break;
                    if ((shuffledDiceRolls.get(i) == 6 || shuffledDiceRolls.get(i) == 8) && 
                        (shuffledDiceRolls.get(g[i][j]) == 6 || shuffledDiceRolls.get(g[i][j]) == 8)) {
                        conflict = true;
                    }
                }
            }
        } while (conflict);
        
        return shuffledDiceRolls;
    }
    
    /* Getters */
    
    public Intersection[] getIntersections() {
        return intersections;
    }
    public Road[][] getIGraph() {
        return iGraph;
    }
    public Hex[] getHexes() {
        return hexes;
    }
    public boolean[][] getHGraph() {
        return hGraph;
    }
    public int getNumSettlements(int player) {
        return settlementsBuilt[player];
    }
    public int getNumCities(int player) {
        return citiesBuilt[player];
    }
    public int getLongestRoad(int player) {
        // todo
        return 0;
    }
    /* returns 2D array: row index indicates player, column index indicates resource type, 
       resources[row][col] indicates number of resource cards of that type earned */
    public int[][] getResources(int diceRoll) {
        int[][] resources = new int[Constants.NUM_PLAYERS][Constants.NUM_RESOURCES];
        if (diceRoll == 7) return resources; // robber rolls get no resources from the board
        
        for (int i = 0; i < hexes.length; i++) {
            if ((hexes[i].getDiceRoll() != diceRoll) || (i == robberIndex)) continue; // no resources given
            
            for (int j = 0; j < Constants.H_I_MAP_GRAPH[i].length; j++) {
                Intersection intersection = intersections[Constants.H_I_MAP_GRAPH[i][j]];
                int building = intersection.getBuilding();
                int player = intersection.getPlayer();
                int resource = hexes[i].getResourceType();
                resources[player][resource] += (++building); // settlements give one, cities two
            }
        }
        return resources;
    }
    
    /* Operations */
    
    public boolean buildRoad(int id, int destId, int player) {
        if (roadsFree[player] > 0 && iGraph[id][destId].build(player)) {
            roadsBuilt[player]++;
            roadsFree[player]--;
            return true;
        }
        else {
            return false;
        }
    }
    public boolean buildSettlement(int id, int player) {
        if (settlementsFree[player] > 0 && intersections[id].build(Constants.SETTLEMENT, player)) {
            settlementsBuilt[player]++;
            settlementsFree[player]--;
            return true;
        }
        else {
            return false;
        }
    }
    public boolean buildCity(int id, int player) {
        int building = intersections[id].getBuilding();
        if (citiesFree[player] > 0 && intersections[id].build(Constants.CITY, player)) {
            citiesBuilt[player]++;
            citiesFree[player]--;
            if (building == Constants.SETTLEMENT) { // free up a settlement when a city is built
                settlementsBuilt[player]--;
                settlementsFree[player]++;
            }
            return true;
        }
        else {
            return false;
        }
    }
    public void moveRobber(int index) {
        robberIndex = index;
    }
    
    /* Debug */
    
    public void printIGraph() {
        System.out.println(intersections.length + "\r");
        for (int i = 0; i < intersections.length; i++) {
            System.out.print(i + " :");
            for (int j = 0; j < intersections.length; j++) {
                if (iGraph[i][j] != null) {
                    System.out.print(" " + j);
                }
            }
            System.out.print("\r\n");
        }
    }
    public void printHexes() {
        System.out.println(hexes.length + "\r");
        for (int i = 0; i < hexes.length; i++) {
            //System.out.print(hexes[i]);
            System.out.print(i + " : " + hexes[i].getStringResourceType() + " " + hexes[i].getDiceRoll() + "\n");
        }
    }
    public void printHGraph() {
        System.out.println(hGraph.length + "\r");
        for (int i = 0; i < hGraph.length; i++) {
            System.out.print(i + " :");
            for (int j = 0; j < hGraph.length; j++) {
                if (hGraph[i][j]) {
                    System.out.print(" " + j);
                }
            }
            System.out.print("\r\n");
        }
    }
    
    /* Testing */
    
    public static void main(String args[]) {
        Board b = new Board();
        //b.printRoads();
        b.printHexes();
        //b.printHexGraph();
        //System.out.print(String.format("\033[2J"));
        /*
        System.out.print(Constants.ANSI_YELLOW_BG_INTENSE);
        System.out.println("I am yellow!");
        System.out.print(Constants.ANSI_RESET);
        System.out.print("I am no longer yellow!");
        System.out.print("");
        System.out.print(Constants.ANSI_RESET);
        System.out.print(Constants.ANSI_RESET_BG);
        System.out.println();
        System.out.print(Constants.ANSI_PURPLE_INTENSE);
        System.out.print("I am purple!");
        System.out.print(Constants.ANSI_RESET);
        System.out.println("I am no longer purple!");
        for (int i = 0; i < 50; ++i) System.out.println();
        */
    }
}
