import java.util.ArrayList;
import java.awt.Color;
import java.awt.Font;

public class BoardDraw {

    /* Constants */
    
    public static final int DEFAULT_DIM = 700;
    public static final int PLACES_TO_ROUND = 3;

    /* Private fields */

    Board board;
    int radius;
    ArrayList<ArrayList<Integer>> hRings;
    ArrayList<ArrayList<Integer>> iRings;
    Road[][] iGraph;
    Hex[] hexes;
    ArrayList<Integer> portLocations;
    Intersection[] intersections;
    int numHexes;
    int numIntersections;
    double dim;
    double xCenter;
    double yCenter;
    double h;
    double s;
    double w;
    double[] hexXCenters;
    double[] hexYCenters;
    HexShape ocean;
    HexShape[] hexShapes;
    double[] interXCoords;
    double[] interYCoords;
    double[] portXCoords;
    double[] portYCoords;
    double chitRadius;
    double portRadius;
    double buildingRadius;
    double innerCityRadius;
    double intersectionRadius;
    double roadOuterRadius;
    double roadInnerRadius;

    /* Constructors */
    
    public BoardDraw(Board board, double dim) {
        this.board = board;
        radius = board.getRadius();
        hRings = board.getHRings();
        iRings = board.getIRings();
        iGraph = board.getIGraph();
        hexes = board.getHexes();
        portLocations = board.getPortLocations();
        intersections = board.getIntersections();
        numHexes = hexes.length;
        numIntersections = intersections.length;
        this.dim = dim;
        initCoords();
        initCanvas();
    }
    public BoardDraw(Board board) {
        this(board, DEFAULT_DIM);
    }
    
    private void initCoords() {
        xCenter = dim / 2;
        yCenter = xCenter;
        hexXCenters = new double[numHexes];
        hexYCenters = new double[numHexes];
        ocean = new HexShape(xCenter, yCenter, HexShape.FLAT, dim, HexShape.FLAT_WIDTH);
        hexShapes = new HexShape[numHexes];
        w = dim / ((2 * radius) + 1);
        hexShapes[0] = new HexShape(xCenter, yCenter, HexShape.BALANCE, w, HexShape.BALANCE_WIDTH);
        h = hexShapes[0].getBalanceHeight();
        s = hexShapes[0].getSide();
        hexXCenters = getHexXCenters();
        hexYCenters = getHexYCenters();
        for (int i = 1; i < numHexes; i++) {
            hexShapes[i] = new HexShape(hexXCenters[i], hexYCenters[i], HexShape.BALANCE, w, HexShape.BALANCE_WIDTH);
        }
        interXCoords = getInterXCoords();
        interYCoords = getInterYCoords();
        // port coordinates must be calculated together
        initPortCoords();
    }
    private void initCanvas() {
        StdDraw.setCanvasSize((int) dim, (int) dim);
        StdDraw.setXscale(0, dim);
        StdDraw.setYscale(0, dim);
        StdDraw.setFont(new Font("Arial", Font.BOLD, (int) (w / 6)));
        chitRadius = w / 5;
        portRadius = w / 6;
        buildingRadius = w / 8;
        innerCityRadius = buildingRadius - (buildingRadius / 3);
        intersectionRadius = buildingRadius / 2;
        roadOuterRadius = 0.015;
        roadInnerRadius = 0.01;
    }
    
    /* Operations */
    
    public void draw() {
        drawOcean();
        drawHexes();
        drawChits();
        drawRoads();
        drawPorts();
        drawIntersections();
    }
    public void save(String name) {
        StdDraw.save(name);
    }
    
    /* Private helpers */
    
    private void drawOcean() {
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.filledPolygon(ocean.getXCoords(), ocean.getYCoords());
    }
    private void drawHexes() {
        for (int i = 0; i < hexes.length; i++) {
            StdDraw.setPenColor(hexes[i].getResource().getColor());
            StdDraw.filledPolygon(hexShapes[i].getXCoords(), hexShapes[i].getYCoords());
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.polygon(hexShapes[i].getXCoords(), hexShapes[i].getYCoords());
        }
    }
    private void drawChits() {
        for (int i = 0; i < hexes.length; i++) {
            if (!hexes[i].hasRobber()) {
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.filledCircle(hexXCenters[i], hexYCenters[i], chitRadius);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.circle(hexXCenters[i], hexYCenters[i], chitRadius);
                StdDraw.text(hexXCenters[i], hexYCenters[i], " " + hexes[i].getDiceRoll());
            }
            else {
                StdDraw.setPenColor(StdDraw.DARK_GRAY);
                StdDraw.filledCircle(hexXCenters[i], hexYCenters[i], chitRadius);
            }
        }
    }
    private void drawRoads() {
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < numIntersections; i++) {
            for (int j = i + 1; j < numIntersections; j++) {
                Road road = iGraph[i][j];
                Player p;
                if (road != null) { p = road.getPlayer(); }
                else { p = null; }
                if (p != null) {
                    ArrayList<Integer> both = road.both();
                    double x0 = interXCoords[both.get(0)];
                    double y0 = interYCoords[both.get(0)];
                    double x1 = interXCoords[both.get(1)];
                    double y1 = interYCoords[both.get(1)];
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.setPenRadius(roadOuterRadius);
                    StdDraw.line(x0, y0, x1, y1);
                    StdDraw.setPenRadius(roadInnerRadius);
                    StdDraw.setPenColor(getPlayerColor(p));
                    StdDraw.line(x0, y0, x1, y1);
                    StdDraw.setPenRadius();
                }
            }
        }
    }
    private void drawIntersections() {
        for (int i = 0; i < numIntersections; i++) {
            Intersection inter = intersections[i];
            Player p = inter.getPlayer();
            Building b = inter.getBuilding();
            if (p == null) { 
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.filledCircle(interXCoords[i], interYCoords[i], intersectionRadius);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.circle(interXCoords[i], interYCoords[i], intersectionRadius);
            }
            else { 
                StdDraw.setPenColor(getPlayerColor(p));
                StdDraw.filledCircle(interXCoords[i], interYCoords[i], buildingRadius);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.circle(interXCoords[i], interYCoords[i], buildingRadius);
                if (b.getBuildingType() == Building.CITY) {
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.filledCircle(interXCoords[i], interYCoords[i], innerCityRadius);
                }
            }
        }
    }
    private void drawPorts() {
        for (int i = 0; i < portLocations.size(); i += 2) {
            // draw lines showing which intersections the port applies to
            double x0 = interXCoords[portLocations.get(i)];
            double y0 = interYCoords[portLocations.get(i)];
            double x1 = interXCoords[portLocations.get(i + 1)];
            double y1 = interYCoords[portLocations.get(i + 1)];
            double x2 = portXCoords[i / 2];
            double y2 = portYCoords[i / 2];
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.line(x0, y0, x2, y2);
            StdDraw.line(x1, y1, x2, y2);
            // draw the port
            Port port = intersections[portLocations.get(i)].getPort();
            if (port.getPortType() == Port.SPECIFIC) {
                StdDraw.setPenColor(port.getResource().getColor());
            }
            else { StdDraw.setPenColor(StdDraw.BLACK); }
            StdDraw.filledCircle(portXCoords[i / 2], portYCoords[i / 2], portRadius);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.circle(portXCoords[i / 2], portYCoords[i / 2], portRadius);
        }
    }
    private Color getPlayerColor(Player p) {
        switch (p.getId()) {
            case Player.BLUE: return StdDraw.BLUE;
            case Player.ORANGE: return StdDraw.ORANGE;
            case Player.RED: return StdDraw.RED;
            case Player.WHITE: return StdDraw.WHITE;
            default: return StdDraw.BLACK;
        }
    }
    private double[] getHexXCenters() {
        double[] xCenters = new double[numHexes];
        xCenters[0] = xCenter;
        for (int i = 1; i < radius; i++) {
            ArrayList<Integer> curHRing = hRings.get(i);
            int curHRingSize = curHRing.size();
            for (int j = 0; j < curHRingSize; j++) {
                int curHex = curHRing.get(j);
                if (j == 0) { xCenters[curHex] = xCenter + (i * w); }
                int nextHex;
                if (j + 1 < curHRingSize) { nextHex = curHRing.get(j + 1); }
                else {
                    nextHex = Constants.INVALID;
                    continue;
                }
                xCenters[nextHex] = getNextX(xCenters[curHex], w, j / i);
            }
        }
        return xCenters;
    }
    private double[] getHexYCenters() {
        double[] yCenters = new double[numHexes];
        yCenters[0] = yCenter;
        for (int i = 1; i < radius; i++) {
            ArrayList<Integer> curHRing = hRings.get(i);
            int curHRingSize = curHRing.size();
            for (int j = 0; j < curHRingSize; j++) {
                int curHex = curHRing.get(j);
                if (j == 0) { yCenters[curHex] = yCenter; }
                int nextHex;
                if (j + 1 < curHRingSize) { nextHex = curHRing.get(j + 1); }
                else {
                    nextHex = Constants.INVALID;
                    continue;
                }
                yCenters[nextHex] = getNextY(yCenters[curHex], h, s, j / i);
            }
        }
        return yCenters;
    }
    private double[] getInterXCoords() {
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
                    xCoords[iOffset + iOdd] = getNextX(xCoords[iOffset + prevIOdd], w, j);
                }
                for (int k = 0; k < evenInc; k++) {
                    iEven = (iEven + 2) % curIRingSize;
                    int prevIEven = (iEven - 2 + curIRingSize) % curIRingSize;
                    xCoords[iOffset + iEven] = getNextX(xCoords[iOffset + prevIEven], w, j);
                }
                oddInc = ((oddInc + 1 - i) % 2) + i;
                evenInc = ((evenInc + 1 - i) % 2) + i;
            }
            iOffset += curIRingSize;
        }
        return xCoords;
    }
    private double[] getInterYCoords() {
        double[] yCoords = new double[numIntersections];
        // we don't know yCenter here but it doesn't matter, so we can use yCenter for both x and y
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
                    yCoords[iOffset + iOdd] = getNextY(yCoords[iOffset + prevIOdd], h, s, j);
                }
                for (int k = 0; k < evenInc; k++) {
                    iEven = (iEven + 2) % curIRingSize;
                    int prevIEven = (iEven - 2 + curIRingSize) % curIRingSize;
                    yCoords[iOffset + iEven] = getNextY(yCoords[iOffset + prevIEven], h, s, j);
                }
                oddInc = ((oddInc + 1 - i) % 2) + i;
                evenInc = ((evenInc + 1 - i) % 2) + i; 
            }
            iOffset += curIRingSize;
        }
        return yCoords;
    }
    private void initPortCoords() {
        portXCoords = new double[portLocations.size() / 2];
        portYCoords = new double[portLocations.size() / 2];
        for (int i = 0; i < portLocations.size(); i += 2) {
            double xOne = interXCoords[portLocations.get(i)];
            double xTwo = interXCoords[portLocations.get(i + 1)];
            double yOne = interYCoords[portLocations.get(i)];
            double yTwo = interYCoords[portLocations.get(i + 1)];
            double xDiff = round(xTwo - xOne);
            double yDiff = round(yTwo - yOne);
            int decider;
            if (xDiff < 0 && yDiff < 0) { decider = 0; }
            else if (xDiff == 0 && yDiff < 0) { decider = 1; }
            else if (xDiff > 0 && yDiff < 0) { decider = 2; }
            else if (xDiff > 0 && yDiff > 0) { decider = 3; }
            else if (xDiff == 0 && yDiff > 0) { decider = 4; }
            else /* if (xDiff < 0 && yDiff > 0) */ { decider = 5; }
            // use dummy hex shape to calculate lengths
            HexShape shape = new HexShape(0, 0, HexShape.BALANCE, s / 2, HexShape.SIDE);
            double wTemp = shape.getBalanceWidth();
            double hTemp = shape.getBalanceHeight();
            double sTemp = shape.getSide();
            // use decider to find coordinates
            portXCoords[i / 2] = getNextX(xOne + (xDiff / 2), wTemp, decider);
            portYCoords[i / 2] = getNextY(yOne + (yDiff / 2), hTemp, sTemp, decider);
        }
    }
    private double getNextX(double x, double w, int transType) {
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
    private double getNextY(double y, double h, double s, int transType) {
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
    // needed because port coordinate calculation is sensitive to precision
    private double round(double d) {
        return ((double) Math.round(d * (10 * PLACES_TO_ROUND))) / (10 * PLACES_TO_ROUND);
    }
}
