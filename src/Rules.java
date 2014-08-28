import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Rules {
    
    /* Constants */
    
    // radius = number of rings (3 for a standard Catan board)
    public static final int DEFAULT_RADIUS = 3;
    public static final int MIN_PLAYERS = 3;
    // 1x Desert, 4x Wool Grain Lumber, 3x Brick Ore
    public static final int[] DEFAULT_HEXES = {1,4,4,4,3,3};
    public static final int DIE_SIDES = 6;
    // count of each type of dice roll chit (possible dice rolls go from 2 to 12)
    public static final int[] DEFAULT_DICE_ROLLS = {1,2,2,2,2,1,2,2,2,2,1};
    // ratio of port to non-port intersections in outer ring (for radius 3 = 18/30 = 6/10)
    public static final double PORT_RATIO = 0.6;
    public static final int[] ROAD_COST = {0,0,1,1,0};
    public static final int[] SETTLEMENT_COST = {1,1,1,1,0};
    public static final int[] CITY_COST = {0,2,0,0,3};
    public static final int[] DEV_CARD_COST = {1,1,0,0,1};
    // max number of each type
    public static final int[] MAX_DEV_CARDS = {14,2,2,2,1,1,1,1,1};
    public static final int INITIAL_FREE_ROADS = 2;
    public static final int INITIAL_FREE_SETTLEMENTS = 2;
    public static final int DEV_CARD_FREE_ROADS = 2;
    public static final int LONGEST_ROAD_VP = 2;
    public static final int LARGEST_ARMY_VP = 2;
    public static final int MIN_LARGEST_ARMY = 3;
    
    /* Static fields */
    
    private static boolean initialized = false;
    private static int radius;
    private static int numHexes;
    private static int numIntersections;
    private static ArrayList<ArrayList<Integer>> hRings;
    private static ArrayList<ArrayList<Integer>> iRings;
    private static boolean[][] hGraph;
    // existence of edge denoted by Road object; 'null' otherwise
    private static boolean[][] iGraph;
    // row index: hex id; col index: intersection id
    private static ArrayList<ArrayList<Integer>> hIMapping;
    // row index: intersection id; col index: hex id
    private static ArrayList<ArrayList<Integer>> iHMapping;
    private static ArrayList<Integer> hexTiles;
    private static ArrayList<Integer> diceRolls;
    private static ArrayList<Port> ports;
    private static int maxVP; // default (for radius 3) = 10
    private static int maxRoads; // default (for radius 3) = 15
    private static int maxSettlements; // default (for radius 3) = 5
    private static int maxCities; // default (for radius 3) = 4
    private static int minLongestRoad; // default (for radius 3) = 5
    

    public static void init(int radius) {
        if (!initialized) { // we only initialize once
            Rules.radius = radius;
            initHRings();
            initIRings();
            initHGraph();
            initIGraph();
            initMappings();
            initHexTiles();
            initDiceRolls();
            initPorts();
            initPlayerLimits();
            initialized = true;
        }
        else { System.out.println("The Rules class has already been initialized. No action was performed."); }
    }
    
    /* Getters */
    
    public static int getRadius() { return radius; }
    public static int getNumHexes() { return numHexes; }
    public static int getNumIntersections() { return numIntersections; }
    public static ArrayList<ArrayList<Integer>> getHRings() { return hRings; }
    public static ArrayList<ArrayList<Integer>> getIRings() { return iRings; }
    public static boolean[][] getHGraph() { return hGraph; }
    public static boolean[][] getIGraph() { return iGraph; }
    public static ArrayList<ArrayList<Integer>> getHIMapping() { return hIMapping; }
    public static ArrayList<ArrayList<Integer>> getIHMapping() { return iHMapping; }
    public static ArrayList<Integer> getHexTiles() { return hexTiles; }
    public static ArrayList<Integer> getDiceRolls() { return diceRolls; }
    public static ArrayList<Port> getPorts() { return ports; }
    public static int getMaxVP() { return maxVP; }
    public static int getMaxRoads() { return maxRoads; }
    public static int getMaxSettlements() { return maxSettlements; }
    public static int getMaxCities() { return maxCities; }
    public static int getMinLongestRoad() { return minLongestRoad; }
    
    /* Private helpers */
    
    private static void initHRings() {
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
    private static void initIRings() {
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
    private static void initHGraph() {
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
                }
                // same in next ring
                int nextCurHex = nextRing.get(j + sextant);
                hGraph[curCurHex][nextCurHex] = true;
                hGraph[nextCurHex][curCurHex] = hGraph[curCurHex][nextCurHex];
                // next in next ring
                int nextNextHex = nextRing.get(j + 1 + sextant);
                hGraph[curCurHex][nextNextHex] = true;
                hGraph[nextNextHex][curCurHex] = hGraph[curCurHex][nextNextHex];
            }
        }
    }
    private static void initIGraph() {
        int n = numIntersections;
        iGraph = new boolean[n][n];
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
                iGraph[curCurInter][curNextInter] = true;
                iGraph[curNextInter][curCurInter] = iGraph[curCurInter][curNextInter];
                if (nextRingSize == 0) { continue; }
                // deal with link to next ring
                if (hasNextRingLink) {
                    int nextCurInter = nextRing.get(j + ringIndexDiff);
                    iGraph[curCurInter][nextCurInter] = true;
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
    private static void initMappings() {
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
                    if (k + 1 < numCurI) { curIIndex = (curIIndex + 1) % curIRingSize; }
                }
                for (int k = 0; k < numPrevI; k++) {
                    hIMapping.get(curHRing.get(j)).add(prevIRing.get(prevIIndex));
                    iHMapping.get(prevIRing.get(prevIIndex)).add(curHRing.get(j));
                    if (k + 1 < numPrevI) { prevIIndex = (prevIIndex + 1) % prevIRingSize; }
                }
            }
        }
    }
    private static void initHexTiles() {
        hexTiles = new ArrayList<Integer>(numHexes);
        // add standard hex tiles (for radius = 3)
        for (int i = 0; i < DEFAULT_HEXES.length; i++) {
            for (int j = 0; j < DEFAULT_HEXES[i]; j++) { hexTiles.add(i - 1); }
        }
        // dynamically add new hex tiles (in addition to hardcoded ones)
        if (radius > DEFAULT_RADIUS) {
            for (int i = DEFAULT_RADIUS; i < radius; i++) {
                ArrayList<Integer> curRing = hRings.get(i);
                int curRingSize = curRing.size();
                for (int j = 0; j < curRingSize / Resource.NUM_TYPES; j++) {
                    for (int k = 0; k < Resource.NUM_TYPES; k++) { hexTiles.add(k); }
                }
                int decider = curRingSize % Resource.NUM_TYPES;
                switch (decider) {
                    case 0: break;
                    case 1: case 2: case 4: // we want to sporadically add new deserts
                        for (int j = 0; j < decider; j++) hexTiles.add(Resource.DESERT);
                        break;
                    case 3: 
                        // add one of each resource except brick and ore
                        for (int j = 0; j < decider; j++) hexTiles.add(j);
                        break;
                    default: // we shouldn't be here
                }
            }
        }
    }
    private static void initDiceRolls() {
        diceRolls = new ArrayList<Integer>(numHexes);
        // add standard dice rolls (for radius = 3);
        for (int i = 0; i < DEFAULT_DICE_ROLLS.length; i++) {
            for (int j = 0; j < DEFAULT_DICE_ROLLS[i]; j++) { diceRolls.add(i + 2); }
        }
        // dynamically add new dice rolls (in addition to hardcoded ones)
        if (radius > DEFAULT_RADIUS) {
            for (int i = DEFAULT_RADIUS; i < radius; i++) {
                ArrayList<Integer> curRing = hRings.get(i);
                int curRingSize = curRing.size();
                for (int j = 0; j < curRingSize / (DEFAULT_DICE_ROLLS.length - 1); j++) {
                    for (int k = 0; k < DEFAULT_DICE_ROLLS.length; k++) {
                        int rollToAdd = k + 2; // values range from 2 to 12, not 0 to 10, so add 2
                        if (rollToAdd == 7) { continue; }
                        diceRolls.add(rollToAdd);
                    }
                }
                ArrayList<Integer> rollsToAdd = new ArrayList<Integer>();
                int decider = curRingSize % (DEFAULT_DICE_ROLLS.length - 1);
                // sevens get removed and re-added later, but we add them here to make code clearer
                switch (decider) {
                    case 0: break;
                    // corresponds to hex tile generation's case 2 
                    case 2: 
                        rollsToAdd = new ArrayList<Integer>(Arrays.asList(7, 7));
                        break;
                    // corresponds to hex tile generation's case 4
                    case 4: 
                        rollsToAdd = new ArrayList<Integer>(Arrays.asList(7, 7, 7, 7));
                        break;
                    // corresponds to hex tile generation's case 1
                    case 6: 
                        rollsToAdd = new ArrayList<Integer>(Arrays.asList(5, 6, 7, 8, 9, 10));
                        break;
                    // corresponds to hex tile generation's case 3
                    case 8:
                        rollsToAdd = new ArrayList<Integer>(Arrays.asList(3, 4, 5, 6, 8, 9, 10, 11));
                        break;
                    case 1: case 3: case 5: case 7: case 9: default: // we shouldn't be here
                }
                diceRolls.addAll(rollsToAdd);
            }
        }
    }
    private static void initPorts() {
        int numI = iRings.get(radius - 1).size(); // # intersections in last ring
        int numPortsI = (int) (numI * PORT_RATIO); // # intersections with ports
        numPortsI -= numPortsI % 2; // must be even
        int numPortsL = numPortsI / 2; // # logical ports
        // we want the number of specific ports to be about half of the ports, but also
        // it needs to be a multiple of Resource.NUM_TYPES
        int numSpecificL = (numPortsL / 2) + (Resource.NUM_TYPES - ((numPortsL / 2) % Resource.NUM_TYPES));
        int numGenericL = numPortsL - numSpecificL;
        /* Initialize logical ports */
        ports = new ArrayList<Port>(numPortsL);
        for (int i = 0; i < numGenericL; i++) { ports.add(new Port(Port.GENERIC)); }
        for (int i = 0; i < numSpecificL / Resource.NUM_TYPES; i++) {
            for (int j = 0; j < Resource.NUM_TYPES; j++) {
                ports.add(new Port(Port.SPECIFIC, new Resource(j)));
            }
        }
    }
    private static void initPlayerLimits() {
        maxVP = (numHexes + 1) / 2;
        maxRoads = maxVP + (maxVP / 2);
        maxSettlements = maxRoads - maxVP;
        maxCities = maxSettlements - (maxVP / (Resource.NUM_TYPES * 2));
        minLongestRoad = maxRoads / 3;
    }
}
