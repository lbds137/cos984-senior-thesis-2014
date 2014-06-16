import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Board {

    private Intersection[] intersections;
    private Road[][] iGraph; // existence of edge denoted by Road object; 'null' otherwise
    private Hex[] hexes;
    private boolean[][] hGraph; // existence of edge denoted by 'true'; 'false' otherwise
    private int robberIndex; // location of the robber in hexes[]
    
    /* Constructors */
    
    // build a new random board
    public Board() {
        initHGraph();
        initIGraph();
    }
    // build a board from a saved state
    public Board(String state) {
        // TODO
    }
    
    private void initHGraph() {
        int[][] g = Hex.GRAPH;
        int n = g.length;
        ArrayList<Integer> shuffledLand = new ArrayList<Integer>(Arrays.asList(Resource.TILES));
        Collections.shuffle(shuffledLand);
        robberIndex = shuffledLand.indexOf(Resource.DESERT); // robber starts in desert
        ArrayList<Integer> shuffledDiceRolls = getDiceRolls(robberIndex);
        
        hexes = new Hex[n];
        for (int i = 0; i < n; i++) {
            hexes[i] = new Hex(i, new Resource(shuffledLand.get(i)), shuffledDiceRolls.get(i));
        }
        hexes[robberIndex].placeRobber(); // actually place the robber on the desert hex
        
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
    private void initIGraph() {
        int[][] g = Intersection.GRAPH;
        int n = g.length;
        ArrayList<Port> ports = getPorts();
        
        intersections = new Intersection[n];
        for (int i = 0; i < n; i++) {
            ArrayList<Integer> hexList = new ArrayList<Integer>(Arrays.asList(Intersection.HEXES[i]));
            ArrayList<Hex> iHexes = new ArrayList<Hex>(hexList.size());
            for (Integer hexId : hexList) {
                iHexes.add(hexes[hexId]);
            }
            intersections[i] = new Intersection(i, ports.get(i), iHexes);
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
    // Dice roll chits 6 and 8 cannot be adjacent, so we have to work a bit harder
    private ArrayList<Integer> getDiceRolls(int desertIndex) {
        int[][] g = Hex.GRAPH;
        int n = g.length;
        boolean conflict;
        ArrayList<Integer> shuffledDiceRolls;

        do {
            conflict = false;
            shuffledDiceRolls = new ArrayList<Integer>(Arrays.asList(Constants.DICE_ROLLS));
            Collections.shuffle(shuffledDiceRolls);
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
    private ArrayList<Port> getPorts() {
        ArrayList<Port> ports = new ArrayList<Port>(Intersection.GRAPH.length);
		// each intersection initialized with a dummy INLAND port
        for (int i = 0; i < Intersection.GRAPH.length; i++) ports.add(new Port(Port.INLAND));
        
        ArrayList<Port> shuffledPorts = new ArrayList<Port>(Port.LOCATIONS.length);
        for (int i = 0; i < Port.NUM_GENERIC; i++) shuffledPorts.add(new Port(Port.GENERIC));
        for (int i = 0; i < Port.NUM_SPECIFIC / Resource.NUM_TYPES; i++) {
            for (int j = 0; j < Resource.NUM_TYPES; j++) {
                shuffledPorts.add(new Port(Port.SPECIFIC, new Resource(j)));
            }
        }
        
        Collections.shuffle(shuffledPorts);
        for (int i = 0; i < Port.LOCATIONS.length; i++) {
            for (int j = 0; j < Port.LOCATIONS[i].length; j++) {
                ports.set(Port.LOCATIONS[i][j], shuffledPorts.get(i));
            }
        }
        
        return ports;
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
    
    /* Operations */
    
    public void moveRobber(int index) {
        if (index < 0 || index > hexes.length) return;
        
        hexes[robberIndex].removeRobber();
        robberIndex = index;
        hexes[robberIndex].placeRobber();
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
            System.out.print(hexes[i] + "\n");
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
    public void printIntersections() {
        System.out.println(intersections.length + "\r");
        for (int i = 0; i < intersections.length; i++) {
            System.out.print(intersections[i]);
            System.out.print("\r\n");
        }
    }
    
    /* Testing */
    
    public static void main(String args[]) {
        Board b = new Board();
        System.out.println("HGRAPH");
        b.printHGraph();
        System.out.println("-----");
        System.out.println("IGRAPH");
        b.printIGraph();
        System.out.println("-----");
        System.out.println("INTERSECTIONS");
        b.printIntersections();
        System.out.println("-----");
        System.out.println("HEXES");
        b.printHexes();
        System.out.println("-----");
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

