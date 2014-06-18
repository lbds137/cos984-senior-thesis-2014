import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.awt.Font;

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
        /*
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
        */
        
        for (int k = 0; k < 20; k++) {
            StdDraw.setXscale(0, 500);
            StdDraw.setYscale(0, 500);
            double xCenter = 250;
            double yCenter = 250;
            double[] xCenters = new double[Hex.GRAPH.length];
            double[] yCenters = new double[Hex.GRAPH.length];
            HexShape[] hexShapes = new HexShape[Hex.GRAPH.length];
            
            double w = 80;
            
            xCenters[9] = xCenter;
            yCenters[9] = xCenter;
            hexShapes[9] = new HexShape(xCenter, yCenter, HexShape.BALANCE, w, HexShape.BALANCE_WIDTH);
            double h = hexShapes[9].getBalanceHeight();
            double s = hexShapes[9].getSide();
            
            xCenters[0] = xCenter - w;
            yCenters[0] = yCenter + h + s;
            xCenters[1] = xCenter;
            yCenters[1] = yCenter + h + s;
            xCenters[2] = xCenter + w;
            yCenters[2] = yCenter + h + s;
            xCenters[3] = xCenter - (w / 2) - w;
            yCenters[3] = yCenter + ((h + s) / 2);
            xCenters[4] = xCenter - (w / 2);
            yCenters[4] = yCenter + ((h + s) / 2);
            xCenters[5] = xCenter + (w / 2);
            yCenters[5] = yCenter + ((h + s) / 2);
            xCenters[6] = xCenter + (w / 2) + w;
            yCenters[6] = yCenter + ((h + s) / 2);
            xCenters[7] = xCenter - (2 * w);
            yCenters[7] = yCenter;
            xCenters[8] = xCenter - w;
            yCenters[8] = yCenter;
            xCenters[10] = xCenter + w;
            yCenters[10] = yCenter;
            xCenters[11] = xCenter + (2 * w);
            yCenters[11] = yCenter;
            xCenters[12] = xCenter - (w / 2) - w;
            yCenters[12] = yCenter - ((h + s) / 2);
            xCenters[13] = xCenter - (w / 2);
            yCenters[13] = yCenter - ((h + s) / 2);
            xCenters[14] = xCenter + (w / 2);
            yCenters[14] = yCenter - ((h + s) / 2);
            xCenters[15] = xCenter + (w / 2) + w;
            yCenters[15] = yCenter - ((h + s) / 2);
            xCenters[16] = xCenter - w;
            yCenters[16] = yCenter - h - s;
            xCenters[17] = xCenter;
            yCenters[17] = yCenter - h - s;
            xCenters[18] = xCenter + w;
            yCenters[18] = yCenter - h - s;
            
            for (int i = 0; i < Hex.GRAPH.length; i++) {
                if (i == 9) continue;
                hexShapes[i] = new HexShape(xCenters[i], yCenters[i], HexShape.BALANCE, w, HexShape.BALANCE_WIDTH);
            }
            
            HexShape bigBlue = new HexShape(xCenter, yCenter, HexShape.FLAT, 500, HexShape.FLAT_WIDTH);
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.filledPolygon(bigBlue.getXCoords(), bigBlue.getYCoords());
            StdDraw.setFont(new Font("Arial", Font.BOLD, 12));
            
            Board b = new Board();
            Hex[] hexes = b.getHexes();
            
            for (int i = 0; i < hexes.length; i++) {
                StdDraw.setPenColor(hexes[i].getResource().getColor());
                StdDraw.filledPolygon(hexShapes[i].getXCoords(), hexShapes[i].getYCoords());
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.text(xCenters[i], yCenters[i], hexes[i].getResource().toString() + " " + hexes[i].getDiceRoll());
            }
            StdDraw.save("result" + k + ".png");
        }
    }
}

