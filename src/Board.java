import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.awt.Font;

public class Board {

    /* Constants */

    public static final int DEFAULT_RADIUS = 3;

    /* Fields */
    
    private int radius;
    private int numHexes;
    private int numIntersections;
    private ArrayList<ArrayList<Integer>> hRings;
    private ArrayList<ArrayList<Integer>> iRings;
    // existence of edge denoted by 'true'; 'false' otherwise
    private boolean[][] hGraph;
    // existence of edge denoted by Road object; 'null' otherwise
    private Road[][] iGraph;
    // row index: hex id; col index: intersection id
    private ArrayList<ArrayList<Integer>> hIMapping;
    // row index: intersection id; col index: hex id
    private ArrayList<ArrayList<Integer>> iHMapping;
    private Hex[] hexes;
    private Intersection[] intersections;
    private int robberIndex; // location of the robber in hexes[]
    
    /* Constructors */
    
    // build a new random board of default size
    public Board() {
        this(DEFAULT_RADIUS);
    }
    // invocations with anything other than DEFAULT_RADIUS will fail (in the current state)
    public Board(int radius) {
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
        for (int i = 0; i < numHexes; i++) Collections.sort(hIMapping.get(i));
        for (int i = 0; i < numIntersections; i++) Collections.sort(iHMapping.get(i));
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
    private void initIntersections() {
        int n = numIntersections;
        ArrayList<Port> ports = getPorts();
        
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
    // Dice roll chits 6 and 8 cannot be adjacent, so we have to work a bit harder
    private ArrayList<Integer> getDiceRolls(int desertIndex) {
        boolean[][] g = hGraph;
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
                for (int j = 0; j < n; j++) {
                    if (conflict) break;
                    if (!g[i][j]) continue;
                    if ((shuffledDiceRolls.get(i) == 6 || shuffledDiceRolls.get(i) == 8) && 
                        (shuffledDiceRolls.get(j) == 6 || shuffledDiceRolls.get(j) == 8)) {
                        conflict = true;
                    }
                }
            }
        } while (conflict);
        
        return shuffledDiceRolls;
    }
    private ArrayList<Port> getPorts() {
        ArrayList<Port> ports = new ArrayList<Port>(numIntersections);
		// each intersection initialized with a dummy INLAND port
        for (int i = 0; i < numIntersections; i++) ports.add(new Port(Port.INLAND));
        
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
    
    /* Drawing */
    
    // draws a dim x dim square containing the game board
    public void draw(double dim) {
        StdDraw.setCanvasSize((int) dim, (int) dim);
        StdDraw.setXscale(0, dim);
        StdDraw.setYscale(0, dim);
        double xCenter = dim / 2;
        double yCenter = dim / 2;
        double[] xCenters = new double[numHexes];
        double[] yCenters = new double[numHexes];
        HexShape[] hexShapes = new HexShape[numHexes];
        
        double w = (dim - (dim / 5)) / ((2 * radius) - 1);
        xCenters[0] = xCenter;
        yCenters[0] = xCenter;
        hexShapes[0] = new HexShape(xCenter, yCenter, HexShape.BALANCE, w, HexShape.BALANCE_WIDTH);
        double h = hexShapes[0].getBalanceHeight();
        double s = hexShapes[0].getSide();
        xCenters = getHexXCenters(xCenter, w);
        yCenters = getHexYCenters(yCenter, h, s);
        
        for (int i = 1; i < numHexes; i++) {
            hexShapes[i] = new HexShape(xCenters[i], yCenters[i], HexShape.BALANCE, w, HexShape.BALANCE_WIDTH);
        }
        
        HexShape ocean = new HexShape(xCenter, yCenter, HexShape.FLAT, dim, HexShape.FLAT_WIDTH);
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.filledPolygon(ocean.getXCoords(), ocean.getYCoords());
        StdDraw.setFont(new Font("Arial", Font.BOLD, (int) (w / 8)));
        
        for (int i = 0; i < hexes.length; i++) {
            StdDraw.setPenColor(hexes[i].getResource().getColor());
            StdDraw.filledPolygon(hexShapes[i].getXCoords(), hexShapes[i].getYCoords());
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(xCenters[i], yCenters[i], hexes[i].getResource().toString() + " " + hexes[i].getDiceRoll());
            StdDraw.polygon(hexShapes[i].getXCoords(), hexShapes[i].getYCoords());
        }
        
        double[] interXCoords = getInterXCoords(xCenter, w);
        double[] interYCoords = getInterYCoords(yCenter, h, s);
        
        double r = w / 8;
        
        for (int i = 0; i < radius; i++) {
            ArrayList<Integer> curIRing = iRings.get(i);
            int curIRingSize = curIRing.size();
            for (int j = 0; j < curIRingSize; j++) {
                switch (i) {
                    case 0:
                        StdDraw.setPenColor(StdDraw.RED);
                        break;
                    case 1:
                        StdDraw.setPenColor(StdDraw.BLUE);
                        break;
                    case 2:
                        StdDraw.setPenColor(StdDraw.BLACK);
                        break;
                    default:
                        StdDraw.setPenColor(StdDraw.BLACK);
                }
                StdDraw.filledCircle(interXCoords[curIRing.get(j)], interYCoords[curIRing.get(j)], r);
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.text(interXCoords[curIRing.get(j)], interYCoords[curIRing.get(j)], "" + curIRing.get(j));
            }
        }
        for (int i = 0; i < numIntersections; i++) {
            System.out.println("coords for intersection " + i + ": (" + interXCoords[i] + ", " + interYCoords[i] + ")");
        }
    }
    private double getXTransition(double x, double w, int transType) {
        double xNew = x;
        switch (transType) {
            case 0: case 2:
                xNew -= w / 2;
                break;
            case 1:
                xNew -= w;
                break;
            case 3: case 5:
                xNew += w / 2;
                break;
            case 4:
                xNew += w;
                break;
            default:
                // we shouldn't be here
        }
        return xNew;
    }
    private double getYTransition(double y, double h, double s, int transType) {
        double yNew = y;
        switch (transType) {
            case 0: case 5:
                yNew += (h + s) / 2;
                break;
            case 1: case 4:
                // yNew = y;
                break;
            case 2: case 3:
                yNew -= (h + s) / 2;
                break;
            default:
                // we shouldn't be here
        }
        return yNew;
    }
    private double[] getHexXCenters(double xCenter, double w) {
        double[] xCenters = new double[numHexes];
        xCenters[0] = xCenter;
        for (int i = 1; i < radius; i++) {
            ArrayList<Integer> curHRing = hRings.get(i);
            int curHRingSize = curHRing.size();
            for (int j = 0; j < curHRingSize; j++) {
                int curHex = curHRing.get(j);
                if (j == 0) xCenters[curHex] = xCenter + (i * w);
                int nextHex;
                if (j + 1 < curHRingSize) {
                    nextHex = curHRing.get(j + 1);
                }
                else {
                    nextHex = Constants.INVALID;
                    continue;
                }
                xCenters[nextHex] = getXTransition(xCenters[curHex], w, j / i);
            }
        }
        return xCenters;
    }
    private double[] getHexYCenters(double yCenter, double h, double s) {
        double[] yCenters = new double[numHexes];
        yCenters[0] = yCenter;
        for (int i = 1; i < radius; i++) {
            ArrayList<Integer> curHRing = hRings.get(i);
            int curHRingSize = curHRing.size();
            for (int j = 0; j < curHRingSize; j++) {
                int curHex = curHRing.get(j);
                if (j == 0) yCenters[curHex] = yCenter;
                int nextHex;
                if (j + 1 < curHRingSize) {
                    nextHex = curHRing.get(j + 1);
                }
                else {
                    nextHex = Constants.INVALID;
                    continue;
                }
                yCenters[nextHex] = getYTransition(yCenters[curHex], h, s, j / i);
            }
        }
        return yCenters;
    }
    private double[] getInterXCoords(double xCenter, double w) {
        double[] xCoords = new double[numIntersections];
        // we don't know yCenter here but it doesn't matter, so we can use xCenter for both x and y
        HexShape hFirst = new HexShape(xCenter, xCenter, HexShape.BALANCE, w, HexShape.BALANCE_WIDTH);
        System.arraycopy(hFirst.getXCoords(), 0, xCoords, 0, hFirst.getXCoords().length);
        int iOffset = iRings.get(0).size();
        int oddStart = 1;
        int evenStart = 2;
        // we already dealt with ring 0 so start at 1
        for (int i = 1; i < radius; i++) {
            ArrayList<Integer> curIRing = iRings.get(i);
            int curIRingSize = curIRing.size();
            int iOdd = oddStart;
            int iEven = evenStart;
            xCoords[iOffset + iOdd] = xCenter + (i * w) + (w / 2);
            xCoords[iOffset + iEven] = xCenter + (i * w) + (w / 2);
            int oddInc = i + 1;
            int evenInc = i;
            for (int j = 0; j < HexShape.NUM_SIDES; j++) {
                for (int k = 0; k < oddInc; k++) {
                    iOdd = (iOdd + 2) % curIRingSize;
                    int prevIOdd = (iOdd - 2 + curIRingSize) % curIRingSize;
                    xCoords[iOffset + iOdd] = getXTransition(xCoords[iOffset + prevIOdd], w, j);
                }
                for (int k = 0; k < evenInc; k++) {
                    iEven = (iEven + 2) % curIRingSize;
                    int prevIEven = (iEven - 2 + curIRingSize) % curIRingSize;
                    xCoords[iOffset + iEven] = getXTransition(xCoords[iOffset + prevIEven], w, j);
                }
                oddInc = ((oddInc + 1 - i) % 2) + i;
                evenInc = ((evenInc + 1 - i) % 2) + i;
            }
            iOffset += curIRingSize;
        }
        return xCoords;
    }
    private double[] getInterYCoords(double yCenter, double h, double s) {
        double[] yCoords = new double[numIntersections];
        // we don't know yCenter here but it doesn't matter, so we can use xCenter for both x and y
        HexShape hFirst = new HexShape(yCenter, yCenter, HexShape.BALANCE, h, HexShape.BALANCE_HEIGHT);
        System.arraycopy(hFirst.getYCoords(), 0, yCoords, 0, hFirst.getYCoords().length);
        int iOffset = iRings.get(0).size();
        int oddStart = 1;
        int evenStart = 2;
        // we already dealt with ring 0 so start at 1
        for (int i = 1; i < radius; i++) {
            ArrayList<Integer> curIRing = iRings.get(i);
            int curIRingSize = curIRing.size();
            int iOdd = oddStart;
            int iEven = evenStart;
            yCoords[iOffset + iOdd] = yCenter - (s / 2);
            yCoords[iOffset + iEven] = yCenter + (s / 2);
            int oddInc = i + 1;
            int evenInc = i;
            for (int j = 0; j < HexShape.NUM_SIDES; j++) {
                for (int k = 0; k < oddInc; k++) {
                    iOdd = (iOdd + 2) % curIRingSize;
                    int prevIOdd = (iOdd - 2 + curIRingSize) % curIRingSize;
                    yCoords[iOffset + iOdd] = getYTransition(yCoords[iOffset + prevIOdd], h, s, j);
                }
                for (int k = 0; k < evenInc; k++) {
                    iEven = (iEven + 2) % curIRingSize;
                    int prevIEven = (iEven - 2 + curIRingSize) % curIRingSize;
                    yCoords[iOffset + iEven] = getYTransition(yCoords[iOffset + prevIEven], h, s, j);
                }
                oddInc = ((oddInc + 1 - i) % 2) + i;
                evenInc = ((evenInc + 1 - i) % 2) + i; 
            }
            iOffset += curIRingSize;
        }
        return yCoords;
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
        
        int canvasSize = 450;
        for (int i = 0; i < 20; i++) {
            b.draw(canvasSize);
            StdDraw.save("result" + i + ".png");
            b = new Board();
            canvasSize += 25;
        }
        System.exit(0);
    }
}

