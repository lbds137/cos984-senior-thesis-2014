import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.awt.Font;

public class Board {

    public static final int DEFAULT_RADIUS = 3;

    private int radius;
    private int numHexes;
    private int numIntersections;
    
    private ArrayList<ArrayList<Integer>> hRings;
    private Hex[] hexes;
    private boolean[][] hGraph; // existence of edge denoted by 'true'; 'false' otherwise
    private int robberIndex; // location of the robber in hexes[]
    
    private ArrayList<ArrayList<Integer>> iRings;
    private Intersection[] intersections;
    private Road[][] iGraph; // existence of edge denoted by Road object; 'null' otherwise
    
    // row index: hex id; col index: intersection ids
    private ArrayList<ArrayList<Integer>> hIMapping;
    // row index: intersection id; col index: hex ids
    private ArrayList<ArrayList<Integer>> iHMapping;
    
    /* Constructors */
    
    // build a new random board of default size
    public Board() {
        radius = DEFAULT_RADIUS;
        initHGraph();
        initHexes();
        initIGraph();
    }
    public Board(int radius) {
        this.radius = radius;
        initHRings();
        initHGraphNew();
        initHexes();
        initIRings();
        initIGraphNew();
        initMappings();
        initIntersections();
    }
    // build a board from a saved state
    public Board(String state) {
        // TODO
    }
    
    private void initHRings() {
        hRings = new ArrayList<ArrayList<Integer>>(radius);
        int curIndex = 0;
        for (int i = 0; i < radius; i++) {
            int circleSize;
            if (i == 0) circleSize = 1;
            else circleSize = i * HexShape.NUM_SIDES;
            hRings.add(new ArrayList<Integer>(circleSize));
            for (int j = 0; j < circleSize; j++) {
                hRings.get(i).add(curIndex);
                curIndex++;
            }
        }
        numHexes = curIndex;
    }
    private void initHGraphNew() {
        int n = numHexes;
        hGraph = new boolean[n][n];
        for (int i = 0; i < radius; i++) {
            ArrayList<Integer> curRing = hRings.get(i);
            int curRingSize = curRing.size();
            for (int j = 0; j < curRingSize; j++) {
                ArrayList<Integer> nextRing;
                int nextRingSize;
                // determine if current ring is last ring
                if ((i + 1) < radius) {
                    nextRing = hRings.get(i + 1);
                    nextRingSize = nextRing.size();
                }
                else {
                    nextRing = null;
                    nextRingSize = 0;
                }
                int curCurHex = curRing.get(j);
                // first ring is a special case
                if (curRingSize == 1) {
                    for (int k = 0; k < nextRingSize; k++) {
                        hGraph[curCurHex][nextRing.get(k)] = true;
                        hGraph[nextRing.get(k)][curCurHex] = hGraph[curCurHex][nextRing.get(k)];
                    }
                    continue;
                }
                // next in same ring
                int curNextHex = curRing.get((j + 1) % curRingSize);
                hGraph[curCurHex][curNextHex] = true;
                hGraph[curNextHex][curCurHex] = hGraph[curCurHex][curNextHex];
                // for the last ring there is no next ring
                if (nextRingSize == 0) continue;
                // deal with links to next ring
                boolean isCorner = ((j % i) == 0);
                int sextant = j / i;
                if (isCorner) {
                    // prev in next ring
                    int nextPrevHex = nextRing.get((j - 1 + sextant + nextRingSize) % nextRingSize);
                    hGraph[curCurHex][nextPrevHex] = true;
                    hGraph[nextPrevHex][curCurHex] = hGraph[curCurHex][nextPrevHex];
                    // same in next ring
                    int nextCurHex = nextRing.get(j + sextant);
                    hGraph[curCurHex][nextCurHex] = true;
                    hGraph[nextCurHex][curCurHex] = hGraph[curCurHex][nextCurHex];
                    // next in next ring
                    int nextNextHex = nextRing.get(j + 1 + sextant);
                    hGraph[curCurHex][nextNextHex] = true;
                    hGraph[nextNextHex][curCurHex] = hGraph[curCurHex][nextNextHex];
                }
                else {
                    // prev in next ring
                    int nextPrevHex = nextRing.get(j + sextant);
                    hGraph[curCurHex][nextPrevHex] = true;
                    hGraph[nextPrevHex][curCurHex] = hGraph[curCurHex][nextPrevHex];
                    // next in next ring
                    int nextNextHex = nextRing.get(j + 1 + sextant);
                    hGraph[curCurHex][nextNextHex] = true;
                    hGraph[nextNextHex][curCurHex] = hGraph[curCurHex][nextNextHex];
                }
            }
        }
    }
    private void initHGraph() {
        int[][] g = Hex.GRAPH;
        int n = g.length;
        
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
    private void initHexes() {
        int n = hGraph.length;
        ArrayList<Integer> shuffledLand = new ArrayList<Integer>(Arrays.asList(Resource.TILES));
        Collections.shuffle(shuffledLand);
        robberIndex = shuffledLand.indexOf(Resource.DESERT); // robber starts in desert
        ArrayList<Integer> shuffledDiceRolls = getDiceRolls(robberIndex);
        
        hexes = new Hex[n];
        for (int i = 0; i < n; i++) {
            hexes[i] = new Hex(i, new Resource(shuffledLand.get(i)), shuffledDiceRolls.get(i));
        }
        hexes[robberIndex].placeRobber(); // actually place the robber on the desert hex
    }
    private void initIRings() {
        iRings = new ArrayList<ArrayList<Integer>>(radius);
        int curIndex = 0;
        for (int i = 0; i < radius; i++) {
            int circleSize;
            circleSize = ((2 * i) + 1) * HexShape.NUM_SIDES;
            iRings.add(new ArrayList<Integer>(circleSize));
            for (int j = 0; j < circleSize; j++) {
                iRings.get(i).add(curIndex);
                curIndex++;
            }
        }
        numIntersections = curIndex;
    }
    private void initIGraphNew() {
        int n = numIntersections;
        iGraph = new Road[n][n];
        for (int i = 0; i < radius; i++) {
            ArrayList<Integer> curRing = iRings.get(i);
            int curRingSize = curRing.size();
            int ringIndexDiff = -1;
            int waitTillNextInc = 0;
            boolean hasNextRingLink = true;
            for (int j = 1; j <= curRingSize; j++) {
                ArrayList<Integer> nextRing;
                int nextRingSize;
                // determine if current ring is last ring
                if ((i + 1) < radius) {
                    nextRing = iRings.get(i + 1);
                    nextRingSize = nextRing.size();
                }
                else {
                    nextRing = null;
                    nextRingSize = 0;
                }
                int curCurInter = curRing.get(j % curRingSize);
                // next in same ring
                int curNextInter = curRing.get((j + 1) % curRingSize);
                iGraph[curCurInter][curNextInter] = new Road(curCurInter, curNextInter);
                iGraph[curNextInter][curCurInter] = iGraph[curCurInter][curNextInter];
                // for the last ring there is no next ring
                if (nextRingSize == 0) continue;
                // deal with link to next ring
                if (hasNextRingLink) {
                    int nextCurInter = nextRing.get(j + ringIndexDiff);
                    iGraph[curCurInter][nextCurInter] = new Road(curCurInter, nextCurInter);
                    iGraph[nextCurInter][curCurInter] = iGraph[curCurInter][nextCurInter];
                    if (waitTillNextInc == 0) {
                        waitTillNextInc = i;
                        ringIndexDiff += 2;
                    }
                    else {
                        waitTillNextInc--;
                        hasNextRingLink = false;
                    }
                }
                else {
                    hasNextRingLink = true;
                }
            }
        }
    }
    private void initIGraph() {
        int[][] g = Intersection.GRAPH;
        
        //
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
        //
        
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
    private void initIntersections() {
        int n = numIntersections;
        ArrayList<Port> ports = getPorts();
        
        intersections = new Intersection[n];
        for (int i = 0; i < n; i++) {
            // this needs to be fixed for the new ring system
            ArrayList<Integer> hexList = iHMapping.get(i);
            ArrayList<Hex> iHexes = new ArrayList<Hex>(hexList.size());
            for (Integer hexId : hexList) {
                iHexes.add(hexes[hexId]);
            }
            intersections[i] = new Intersection(i, ports.get(i), iHexes);
        }
    }
    private void initMappings() {
        hIMapping = new ArrayList<ArrayList<Integer>>(numHexes);
        for (int i = 0; i < numHexes; i++) {
            hIMapping.add(new ArrayList<Integer>(HexShape.NUM_SIDES));
        }
        iHMapping = new ArrayList<ArrayList<Integer>>(numIntersections);
        for (int i = 0; i < numIntersections; i++) {
            iHMapping.add(new ArrayList<Integer>(HexShape.NUM_SIDES / 2));
        }
        
        for (int i = 0; i < radius; i++) {
            ArrayList<Integer> curHRing = hRings.get(i);
            int curHRingSize = curHRing.size();
            ArrayList<Integer> curIRing = iRings.get(i);
            int curIRingSize = curIRing.size();
            
            ArrayList<Integer> prevIRing;
            int prevIRingSize;
            if (i > 0) {
                prevIRing = iRings.get(i - 1);
                prevIRingSize = prevIRing.size();
            }
            else {
                prevIRing = null;
                prevIRingSize = 0;
            }
            int curIIndex = 0;
            int prevIIndex = 1;
            for (int j = 0; j < curHRingSize; j++) {
                // first ring is a special case
                if (curHRingSize == 1) {
                    for (int k = 0; k < curIRingSize; k++) {
                        hIMapping.get(curHRing.get(j)).add(curIRing.get(k));
                        iHMapping.get(curIRing.get(k)).add(curHRing.get(j));
                    }
                    continue;
                }
                int numCurI = HexShape.NUM_SIDES / 2;
                int numPrevI = numCurI;
                boolean isCorner = ((j % i) == 0);
                if (isCorner) {
                    numCurI++;
                    numPrevI--;
                }
                for (int k = 0; k < numCurI; k++) {
                    hIMapping.get(curHRing.get(j)).add(curIRing.get(curIIndex));
                    iHMapping.get(curIRing.get(curIIndex)).add(curHRing.get(j));
                    if (k + 1 < numCurI) curIIndex = (curIIndex + 1) % curIRingSize;
                }
                if (i == 0) continue; // there are no previous rings for ring 0
                for (int k = 0; k < numPrevI; k++) {
                    hIMapping.get(curHRing.get(j)).add(prevIRing.get(prevIIndex));
                    iHMapping.get(prevIRing.get(prevIIndex)).add(curHRing.get(j));
                    if (k + 1 < numPrevI) prevIIndex = (prevIIndex + 1) % prevIRingSize;
                }
            }
        }
        // sort both mappings for convenience
        for (int i = 0; i < numHexes; i++) {
            Collections.sort(hIMapping.get(i));
        }
        for (int i = 0; i < numIntersections; i++) {
            Collections.sort(iHMapping.get(i));
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
        // need to update port locations (and maybe not have port locations as constant anymore)
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
    public void printHRings() {
        for (int i = 0; i < hRings.size(); i++) {
            System.out.println("Circle " + i);
            System.out.print("{ ");
            for (int j = 0; j < hRings.get(i).size(); j++) {
                System.out.print(hRings.get(i).get(j) + " "); 
            }
            System.out.print("}\n");
        }
    }
    public void printIRings() {
        for (int i = 0; i < iRings.size(); i++) {
            System.out.println("Circle " + i);
            System.out.print("{ ");
            for (int j = 0; j < iRings.get(i).size(); j++) {
                System.out.print(iRings.get(i).get(j) + " "); 
            }
            System.out.print("}\n");
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
    public void printHIMapping() {
        System.out.println("Hex to intersection mapping");
        for (int i = 0; i < numHexes; i++) {
            System.out.print(i + " :");
            for (int j = 0; j < HexShape.NUM_SIDES; j++) {
                System.out.print(" " + hIMapping.get(i).get(j));
            }
            System.out.print("\r\n");
        }
    }
    public void printIHMapping() {
        System.out.println("Intersection to hex mapping");
        for (int i = 0; i < numIntersections; i++) {
            System.out.print(i + " :");
            for (int j = 0; j < iHMapping.get(i).size(); j++) {
                System.out.print(" " + iHMapping.get(i).get(j));
            }
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
        Board b = new Board(3);
        //for (int k = 0; k < 20; k++) {
        /*for (int k = 0; k < 1; k++) {
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
            
            //HexShape bigBlue = new HexShape(xCenter, yCenter, HexShape.FLAT, 500, HexShape.FLAT_WIDTH);
            //StdDraw.setPenColor(StdDraw.BLUE);
            //StdDraw.filledPolygon(bigBlue.getXCoords(), bigBlue.getYCoords());
            StdDraw.setFont(new Font("Arial", Font.BOLD, 12));
            
            b = new Board(3);
            Hex[] hexes = b.getHexes();
            
            for (int i = 0; i < hexes.length; i++) {
                //StdDraw.setPenColor(hexes[i].getResource().getColor());
                //StdDraw.filledPolygon(hexShapes[i].getXCoords(), hexShapes[i].getYCoords());
                StdDraw.polygon(hexShapes[i].getXCoords(), hexShapes[i].getYCoords());
                StdDraw.setPenColor(StdDraw.BLACK);
                //StdDraw.text(xCenters[i], yCenters[i], hexes[i].getResource().toString() + " " + hexes[i].getDiceRoll());
            }
            StdDraw.save("result" + k + ".png");
        }*/
        //b.printIGraph();
        //b.printIntersections();
        b.printHIMapping();
        b.printIHMapping();
        System.exit(0);
    }
}

