import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class Board {

    private Intersection[] intersections;
    private Road[][] iGraph;
    private Hex[] hexes;
    private boolean[][] hGraph;
    
    /* Constructors */
    
    public Board() {
        createIGraph();
        createHGraph();
    }
    
    private void createIGraph() {
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
    private void createHGraph() {
        int[][] g = Constants.H_GRAPH;
        int n = g.length;
        ArrayList<Integer> shuffledLand = new ArrayList<Integer>(Arrays.asList(Constants.LAND));
        Collections.shuffle(shuffledLand, new Random());
        ArrayList<Integer> shuffledDice = getShuffledDice(shuffledLand.indexOf(Constants.DESERT));
        
        hexes = new Hex[n];
        for (int i = 0; i < n; i++) {
            hexes[i] = new Hex(i, shuffledLand.get(i), shuffledDice.get(i));
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
    // Dice roll chits 6 and 8 cannot be adjacent, so we have to work a bit harder
    private ArrayList<Integer> getShuffledDice(int desertIndex) {
        int[][] g = Constants.H_GRAPH;
        int n = g.length;
        boolean conflict;
        ArrayList<Integer> shuffledDice;

        do {
            conflict = false;
            shuffledDice = new ArrayList<Integer>(Arrays.asList(Constants.DICE));
            Collections.shuffle(shuffledDice, new Random());
            // Make sure die number 7 is on the desert tile
            shuffledDice.add(desertIndex, shuffledDice.remove(shuffledDice.indexOf(7)));
            
            // test generation for 6 and 8 adjacencies; regenerate if 6 and 8 end up adjacent
            for (int i = 0; i < n; i++) {
                if (conflict) break;
                for (int j = 0; j < g[i].length; j++) {
                    if (conflict) break;
                    if ((shuffledDice.get(i) == 6 || shuffledDice.get(i) == 8) && 
                        (shuffledDice.get(g[i][j]) == 6 || shuffledDice.get(g[i][j]) == 8)) {
                        conflict = true;
                    }
                }
            }
        } while (conflict);
        
        return shuffledDice;
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
            System.out.print(i + " : " + hexes[i].getStringResourceType() + " " + hexes[i].getDieNumber() + "\n");
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
    
    /* Methods for Game to call */
    
    public int buildRoad(String location) {
        // return indicates success or error
        return -1;
    }
    public int buildSettlement(String location) {
        // return indicates success or error
        return -1;
    }
    public int buildCity(String location) {
        // return indicates success or error
        return -1;
    }
    public int getNumSettlements(int player) {
        return 0;
    }
    public int getNumCities(int player) {
        return 0;
    }
    public int getLongestRoad(int player) {
        return 0;
    }
    public ArrayList<ArrayList<Integer>> getResources(int roll) {
        // returns 2D array list: row index indicates player, column index indicates resource type, 
        // arr[row][col] indicates number of resource cards of that type earned
        return null;
    }
    
    /* Testing */
    
    public static void main(String args[]) {
        Board b = new Board();
        //b.printRoads();
        b.printHexes();
        //b.printHexGraph();
    }
}