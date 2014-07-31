import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Board {

    /* Constants */

    // radius = number of rings (3 for a standard Catan board)
    public static final int DEFAULT_RADIUS = 3;

    /* Fields */
    
    private int radius;
    private int numHexes;
    private int numIntersections;
    private ArrayList<ArrayList<Integer>> hRings;
    private ArrayList<ArrayList<Integer>> iRings;
    private boolean[][] hGraph;
    // existence of edge denoted by Road object; 'null' otherwise
    private Road[][] iGraph;
    // row index: hex id; col index: intersection id
    private ArrayList<ArrayList<Integer>> hIMapping;
    // row index: intersection id; col index: hex id
    private ArrayList<ArrayList<Integer>> iHMapping;
    private Hex[] hexes;
    private ArrayList<Integer> portLocations;
    private Intersection[] intersections;
    private int robberIndex; // location of the robber in hexes[]
    
    /* Constructors */
    
    // build a new random board of default size
    public Board() { this(DEFAULT_RADIUS); }
    public Board(int radius) {
        if (radius < DEFAULT_RADIUS) System.exit(1);
        this.radius = radius;
        initHRings();
        initIRings();
        initHGraph();
        initIGraph();
        initMappings();
        initHexes();
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
    private void initHGraph() {
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
                if (nextRingSize == 0) { continue; }
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
    private void initIGraph() {
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
                if (nextRingSize == 0) { continue; }
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
                else { hasNextRingLink = true; }
            }
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
                if (i == 0) { continue; } // there are no previous rings for ring 0
                for (int k = 0; k < numPrevI; k++) {
                    hIMapping.get(curHRing.get(j)).add(prevIRing.get(prevIIndex));
                    iHMapping.get(prevIRing.get(prevIIndex)).add(curHRing.get(j));
                    if (k + 1 < numPrevI) { prevIIndex = (prevIIndex + 1) % prevIRingSize; }
                }
            }
        }
        // sort both mappings for convenience
        for (int i = 0; i < numHexes; i++) { Collections.sort(hIMapping.get(i)); }
        for (int i = 0; i < numIntersections; i++) { Collections.sort(iHMapping.get(i)); }
    }
    private void initHexes() {
        int n = hGraph.length;
        ArrayList<Integer> shuffledLand = getHexTiles();
        ArrayList<Integer> desertIndices = new ArrayList<Integer>();
        ArrayList<Integer> partialLand = shuffledLand;
        while (partialLand.indexOf(Resource.DESERT) != -1) {
            int index = partialLand.lastIndexOf(Resource.DESERT); // starting from the end
            desertIndices.add(index);
            partialLand = new ArrayList<Integer>(partialLand.subList(0, index));
        }
        robberIndex = desertIndices.get(desertIndices.size() - 1); // robber starts in the first desert
        ArrayList<Integer> shuffledDiceRolls = getDiceRolls(desertIndices);
        
        hexes = new Hex[n];
        for (int i = 0; i < n; i++) {
            hexes[i] = new Hex(i, new Resource(shuffledLand.get(i)), shuffledDiceRolls.get(i));
        }
        hexes[robberIndex].placeRobber();
    }
    private void initIntersections() {
        int n = numIntersections;
        ArrayList<Port> ports = generatePorts();
        
        intersections = new Intersection[n];
        for (int i = 0; i < n; i++) {
            ArrayList<Integer> hexList = iHMapping.get(i);
            ArrayList<Hex> iHexes = new ArrayList<Hex>(hexList.size());
            for (Integer hexId : hexList) {
                iHexes.add(hexes[hexId]);
            }
            intersections[i] = new Intersection(i, ports.get(i), iHexes);
        }
    }
    private ArrayList<Integer> getHexTiles() {
        ArrayList<Integer> land = new ArrayList<Integer>(Arrays.asList(Resource.DEFAULT_TILES));
        // dynamically add new hex tiles (in addition to hardcoded ones)
        if (radius > DEFAULT_RADIUS) {
            for (int i = DEFAULT_RADIUS; i < radius; i++) {
                ArrayList<Integer> curRing = hRings.get(i);
                int curRingSize = curRing.size();
                for (int j = 0; j < curRingSize / Resource.NUM_TYPES; j++) {
                    for (int k = 0; k < Resource.NUM_TYPES; k++) { land.add(k); }
                }
                int decider = curRingSize % Resource.NUM_TYPES;
                switch (decider) {
                    case 0: break;
                    case 1: case 2: case 3: // we want to sporadically add new deserts
                        for (int j = 0; j < decider; j++) land.add(Resource.DESERT);
                        break;
                    case 4: 
                        // add one of each resource except ore
                        for (int j = 0; j < decider; j++) land.add(j);
                        break;
                    default: // we shouldn't be here
                }
            }
        }
        Collections.shuffle(land);
        return land;
    }
    private ArrayList<Integer> getDiceRolls(ArrayList<Integer> desertIndices) {
        boolean[][] g = hGraph;
        int n = g.length;
        boolean conflict;
        ArrayList<Integer> shuffledDiceRolls;
        // the rule that 6 and 8 cannot neighbor each other only applies when radius = DEFAULT_RADIUS
        do {
            conflict = false;
            shuffledDiceRolls = generateDiceRolls(desertIndices);
            // test generation for 6 and 8 adjacencies; regenerate if 6 and 8 end up adjacent
            for (int i = 0; i < n; i++) {
                if (conflict) { break; }
                for (int j = 0; j < n; j++) {
                    if (conflict) { break; }
                    if (!g[i][j]) { continue; }
                    if ((shuffledDiceRolls.get(i) == 6 || shuffledDiceRolls.get(i) == 8) && 
                        (shuffledDiceRolls.get(j) == 6 || shuffledDiceRolls.get(j) == 8)) {
                        conflict = true;
                    }
                }
            }
        } while (radius == DEFAULT_RADIUS && conflict);
        return shuffledDiceRolls;
    }
    private ArrayList<Integer> generateDiceRolls(ArrayList<Integer> desertIndices) {
        ArrayList<Integer> diceRolls = new ArrayList<Integer>(Constants.DEFAULT_DICE_ROLLS);
        // dynamically add new dice rolls (in addition to hardcoded ones)
        if (radius > DEFAULT_RADIUS) {
            for (int i = DEFAULT_RADIUS; i < radius; i++) {
                ArrayList<Integer> curRing = hRings.get(i);
                int curRingSize = curRing.size();
                for (int j = 0; j < curRingSize / (Constants.NUM_DICE_ROLLS - 1); j++) {
                    for (int k = 0; k < Constants.NUM_DICE_ROLLS; k++) {
                        int rollToAdd = k + 2;
                        if (rollToAdd == 7) { continue; }
                        diceRolls.add(rollToAdd);
                    }
                }
                ArrayList<Integer> rollsToAdd = new ArrayList<Integer>();
                int decider = curRingSize % (Constants.NUM_DICE_ROLLS - 1);
                // sevens get removed and re-added later, but we add them here to make code clearer
                switch (decider) {
                    case 0: break;
                    // corresponds to hex tile generation's case 2 
                    case 2: 
                        rollsToAdd = new ArrayList<Integer>(Arrays.asList(7, 7));
                        break;
                    // corresponds to hex tile generation's case 4
                    case 4: 
                        rollsToAdd = new ArrayList<Integer>(Arrays.asList(5, 6, 8, 9));
                        break;
                    // corresponds to hex tile generation's case 1
                    case 6: 
                        rollsToAdd = new ArrayList<Integer>(Arrays.asList(5, 6, 7, 8, 9, 10));
                        break;
                    // corresponds to hex tile generation's case 3
                    case 8:
                        rollsToAdd = new ArrayList<Integer>(Arrays.asList(5, 6, 7, 7, 7, 8, 9, 10));
                        break;
                    case 1: case 3: case 5: case 7: case 9: default: // we shouldn't be here
                }
                diceRolls.addAll(rollsToAdd);
            }
        }
        Collections.shuffle(diceRolls);
        // remove sevens
        while (diceRolls.indexOf(7) != -1) {
            int sevenIndex = diceRolls.indexOf(7);
            diceRolls.remove(sevenIndex);
        }
        // re-add sevens at proper indices (where the deserts are)
        for (int i = desertIndices.size() - 1; i >= 0; i--) { 
            diceRolls.add(desertIndices.get(i), new Integer(7));
        }
        return diceRolls;
    }
    private ArrayList<Port> generatePorts() {
        int numI = iRings.get(radius - 1).size(); // # intersections in last ring
        int numPortsI = (int) (numI * Port.PORT_RATIO); // # intersections with ports
        numPortsI -= numPortsI % 2; // must be even
        int numPortsL = numPortsI / 2; // # logical ports
        // we want the number of specific ports to be about half of the ports, but also
        // it needs to be a multiple of Resource.NUM_TYPES
        int numSpecificL = (numPortsL / 2) + (Resource.NUM_TYPES - ((numPortsL / 2) % Resource.NUM_TYPES));
        int numGenericL = numPortsL - numSpecificL;
        /* Initialize logical ports */
        ArrayList<Port> portsL = new ArrayList<Port>(numPortsL);
        for (int i = 0; i < numGenericL; i++) { portsL.add(new Port(Port.GENERIC)); }
        for (int i = 0; i < numSpecificL / Resource.NUM_TYPES; i++) {
            for (int j = 0; j < Resource.NUM_TYPES; j++) {
                portsL.add(new Port(Port.SPECIFIC, new Resource(j)));
            }
        }
        Collections.shuffle(portsL);
        /* Randomize port locations */
        int numInlandI = numI - numPortsI; 
        int numAvailable = numInlandI; // how many inland ports are left to distribute
        Port inland = new Port(Port.INLAND); // use one inland port for all references
        ArrayList<Port> portsI = new ArrayList<Port>(numIntersections);
        // add inland port references to all intersections not in last ring
        for (int i = 0; i < numIntersections - numI; i++) { portsI.add(inland); }
        for (int i = 0; i < numPortsL; i++) {
            // separate real ports with one inland port
            portsI.add(inland);
            numAvailable--;
            portsI.add(portsL.get(i));
            portsI.add(portsL.get(i));
        }
        int portsISize = portsI.size(); // need to keep track of this because size will change
        // distribute remaining inland ports
        while (numAvailable > 0) {
            for (int i = numIntersections - numI; numAvailable > 0 && i < portsISize; i++) {
                double rand = Math.random();
                if (portsI.get(i).getPortType() == Port.INLAND && rand > 0.5) {
                    portsI.add(i, inland);
                    portsISize++;
                    i++;
                    numAvailable--;
                }
            }
        }
        // initialize portLocations field (used by BoardDraw class)
        portLocations = new ArrayList<Integer>(numPortsI);
        for (int i = numIntersections - numI; i < portsISize; i++) {
            if (portsI.get(i).getPortType() != Port.INLAND) { portLocations.add(i); }
        }
        return portsI;
    }
    
    /* Getters */
    
    public int getRadius() {
        return radius;
    }
    public ArrayList<ArrayList<Integer>> getHRings() {
        return hRings;
    }
    public ArrayList<ArrayList<Integer>> getIRings() {
        return iRings;
    }
    public boolean[][] getHGraph() {
        return hGraph;
    }
    public Road[][] getIGraph() {
        return iGraph;
    }
    public Hex[] getHexes() {
        return hexes;
    }
    public Intersection[] getIntersections() {
        return intersections;
    }
    // get the locations of ONLY the maritime ports (i.e. GENERIC and SPECIFIC but not INLAND)
    public ArrayList<Integer> getPortLocations() {
        return portLocations;
    }
    
    /* Operations */
    
    // move the robber and pick a random player whose buildings border the chosen hex
    public Player moveRobber(int index) {
        hexes[robberIndex].removeRobber();
        robberIndex = index;
        hexes[robberIndex].placeRobber();
        // since we're not using a set, if a player has more settlements s/he is more likely to be picked
        ArrayList<Player> candidatePlayers = new ArrayList<Player>();
        for (int i = 0; i < hIMapping.get(index).size(); i++) {
            Intersection inter = intersections[hIMapping.get(index).get(i)];
            Player p = inter.getPlayer();
            if (p != null && p.getResourceCards().size() != 0) { candidatePlayers.add(p); }
        }
        if (candidatePlayers.size() == 0) { return null; }
        int randIndex = (int) (Math.random() * candidatePlayers.size());
        return candidatePlayers.get(randIndex);
    }
    
    /* Debug */
    
    public void printIGraph() {
        System.out.println(intersections.length + "\r");
        for (int i = 0; i < intersections.length; i++) {
            System.out.print(i + " :");
            for (int j = 0; j < intersections.length; j++) {
                if (iGraph[i][j] != null) { System.out.print(" " + j); }
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
                if (hGraph[i][j]) { System.out.print(" " + j); }
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
        b.printHIMapping();
        b.printIHMapping();
        System.out.println("=====");
        System.out.println("=====");
    }
}
