import java.util.ArrayList;
import java.awt.Color;
import java.awt.Font;

public class BoardDraw {

    /* Constants */
    
    public static final int DEFAULT_DIM = 700;
    public static final int PLACES_TO_ROUND = 3;
    public static final double ROAD_OUTER_RADIUS = 0.015;
    public static final double ROAD_INNER_RADIUS = 0.01;

    /* Private fields */

    Board board;
    int radius;
    ArrayList<ArrayList<Integer>> hRings;
    ArrayList<ArrayList<Integer>> iRings;
    Hex[] hexes;
    ArrayList<Integer> portLocations;
    Intersection[] intersections;
    Road[][] roads;
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
    
    public BoardDraw(Board board, int dim) {
        this.board = board;
        radius = Rules.getRadius();
        numHexes = Rules.getNumHexes();
        numIntersections = Rules.getNumIntersections();
        hRings = Rules.getHRings();
        iRings = Rules.getIRings();
        hexes = board.getHexes();
        portLocations = board.getPortLocations();
        intersections = board.getIntersections();
        roads = board.getRoads();
        this.dim = (double) dim;
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
        initHexCenters();
        for (int i = 1; i < numHexes; i++) {
            hexShapes[i] = new HexShape(hexXCenters[i], hexYCenters[i], HexShape.BALANCE, w, HexShape.BALANCE_WIDTH);
        }
        initInterCoords();
        initPortCoords();
    }
    private void initCanvas() {
        StdDraw.setCanvasSize((int) dim, (int) dim);
        StdDraw.setXscale(0, dim);
        StdDraw.setYscale(0, dim);
        chitRadius = w / 5;
        portRadius = w / 6;
        buildingRadius = w / 8;
        innerCityRadius = buildingRadius - (buildingRadius / 3);
        intersectionRadius = buildingRadius - (buildingRadius / 4);
        roadOuterRadius = ROAD_OUTER_RADIUS;
        roadInnerRadius = ROAD_INNER_RADIUS;
    }
    
    /* Operations */
    
    public void draw() {
        StdDraw.show(0);
        drawOcean();
        drawHexes();
        drawChits();
        drawRoads();
        drawPorts();
        drawIntersections();
        StdDraw.show(0);
    }
    public void drawRef() {
        drawRefHexes();
        drawRefChits();
        drawRefPorts();
        drawRefIntersections();
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
        for (int i = 0; i < hexes.length; i++) { drawHex(i); }
    }
    private void drawRefHexes() {
        for (int i = 0; i < hexes.length; i++) { drawEmptyHex(i); }
    }
    private void drawHex(int i) {
        StdDraw.setPenColor(hexes[i].getResource().getColor());
        StdDraw.filledPolygon(hexShapes[i].getXCoords(), hexShapes[i].getYCoords());
        drawEmptyHex(i);
    }
    private void drawEmptyHex(int i) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.polygon(hexShapes[i].getXCoords(), hexShapes[i].getYCoords());
    }
    private void drawChits() {
        for (int i = 0; i < hexes.length; i++) {
            if (!hexes[i].hasRobber()) { drawNormalChit(i); }
            else { drawRobberChit(i); }
        }
    }
    private void drawRefChits() {
        for (int i = 0; i < hexes.length; i++) { drawIdChit(i); }
    }
    private void drawNormalChit(int i) {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.filledCircle(hexXCenters[i], hexYCenters[i], chitRadius);
        drawIdChit(i);
        StdDraw.setFont(new Font("Arial", Font.BOLD, (int) (w / 5)));
        StdDraw.text(hexXCenters[i], hexYCenters[i], "" + hexes[i].getDiceRoll());
    }
    private void drawRobberChit(int i) {
        StdDraw.setPenColor(StdDraw.DARK_GRAY);
        StdDraw.filledCircle(hexXCenters[i], hexYCenters[i], chitRadius);
        drawEmptyChit(i);
        // "R" is for "Robber"
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Arial", Font.BOLD, (int) (w / 5)));
        StdDraw.text(hexXCenters[i], hexYCenters[i], "R");
    }
    private void drawIdChit(int i) {
        drawEmptyChit(i);
        double yOffset = chitRadius - (chitRadius / 4);
        StdDraw.setFont(new Font("Arial", Font.BOLD, (int) (w / 11)));
        StdDraw.text(hexXCenters[i], hexYCenters[i] - yOffset, "" + hexes[i].getId());
    }
    private void drawEmptyChit(int i) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.circle(hexXCenters[i], hexYCenters[i], chitRadius);
    }
    private void drawRoads() {
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < numIntersections; i++) {
            for (int j = i + 1; j < numIntersections; j++) {
                Road road = roads[i][j];
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
                    StdDraw.setPenColor(Player.getPlayerColor(p));
                    StdDraw.line(x0, y0, x1, y1);
                    StdDraw.setPenRadius();
                }
            }
        }
    }
    private void drawIntersections() {
        for (int i = 0; i < numIntersections; i++) {
            Player p = intersections[i].getPlayer();
            if (p != null) { drawIntersection(i); }
            else { drawIdIntersection(i); }
        }
    }
    private void drawRefIntersections() {
        for (int i = 0; i < numIntersections; i++) { drawIdIntersection(i); }
    }
    private void drawIntersection(int i) {
        Player p = intersections[i].getPlayer();
        Building b = intersections[i].getBuilding();
        StdDraw.setPenColor(Player.getPlayerColor(p));
        StdDraw.filledCircle(interXCoords[i], interYCoords[i], buildingRadius);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.circle(interXCoords[i], interYCoords[i], buildingRadius);
        if (b.getBuildingType() == Building.CITY) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.filledCircle(interXCoords[i], interYCoords[i], innerCityRadius);
        }
    }
    private void drawIdIntersection(int i) {
        drawEmptyIntersection(i);
        StdDraw.setFont(new Font("Arial", Font.BOLD, (int) (w / 11)));
        StdDraw.text(interXCoords[i], interYCoords[i], "" + intersections[i].getId());
    }
    private void drawEmptyIntersection(int i) {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.filledCircle(interXCoords[i], interYCoords[i], intersectionRadius);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.circle(interXCoords[i], interYCoords[i], intersectionRadius);
    }
    private void drawPorts() {
        for (int i = 0; i < portLocations.size(); i += 2) {
            drawPortLines(i);
            drawPort(i);
        }
    }
    private void drawRefPorts() {
        for (int i = 0; i < portLocations.size(); i += 2) {
            drawPortLines(i);
            drawRefPort(i);
        }
    }
    private void drawPortLines(int i) {
        double x0 = interXCoords[portLocations.get(i)];
        double y0 = interYCoords[portLocations.get(i)];
        double x1 = interXCoords[portLocations.get(i + 1)];
        double y1 = interYCoords[portLocations.get(i + 1)];
        double x2 = portXCoords[i / 2];
        double y2 = portYCoords[i / 2];
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.line(x0, y0, x2, y2);
        StdDraw.line(x1, y1, x2, y2);
    }
    private void drawPort(int i) {
        Port port = intersections[portLocations.get(i)].getPort();
        if (port.getPortType() == Port.SPECIFIC) { StdDraw.setPenColor(port.getResource().getColor()); }
        else { StdDraw.setPenColor(StdDraw.BLACK); }
        StdDraw.filledCircle(portXCoords[i / 2], portYCoords[i / 2], portRadius);
        drawEmptyPort(i);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Arial", Font.BOLD, (int) (w / 9)));
        StdDraw.text(portXCoords[i / 2], portYCoords[i / 2], port.getStringRatio());
    }
    private void drawRefPort(int i) {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.filledCircle(portXCoords[i / 2], portYCoords[i / 2], portRadius);
        drawEmptyPort(i);
        //StdDraw.setFont(new Font("Arial", Font.BOLD, (int) (w / 11)));
        //StdDraw.text(portXCoords[i / 2], portYCoords[i / 2], portLocations.get(i) + "," + portLocations.get(i + 1));
    }
    private void drawEmptyPort(int i) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.circle(portXCoords[i / 2], portYCoords[i / 2], portRadius);
    }
    
    private void initHexCenters() {
        hexXCenters = new double[numHexes];
        hexYCenters = new double[numHexes];
        hexXCenters[0] = xCenter;
        hexYCenters[0] = yCenter;
        for (int i = 1; i < radius; i++) {
            ArrayList<Integer> curHRing = hRings.get(i);
            int curHRingSize = curHRing.size();
            for (int j = 0; j < curHRingSize; j++) {
                int curHex = curHRing.get(j);
                if (j == 0) { 
                    hexXCenters[curHex] = xCenter + (i * w); 
                    hexYCenters[curHex] = yCenter;
                }
                int nextHex;
                if (j + 1 < curHRingSize) { nextHex = curHRing.get(j + 1); }
                else {
                    nextHex = Constants.INVALID;
                    continue;
                }
                hexXCenters[nextHex] = getNextX(hexXCenters[curHex], w, j / i);
                hexYCenters[nextHex] = getNextY(hexYCenters[curHex], h, s, j / i);
            }
        }
    }
    private void initInterCoords() {
        interXCoords = new double[numIntersections];
        interYCoords = new double[numIntersections];
        HexShape hFirst = new HexShape(xCenter, yCenter, HexShape.BALANCE, w, HexShape.BALANCE_WIDTH);
        System.arraycopy(hFirst.getXCoords(), 0, interXCoords, 0, hFirst.getXCoords().length);
        System.arraycopy(hFirst.getYCoords(), 0, interYCoords, 0, hFirst.getYCoords().length);
        int iOffset = iRings.get(0).size();
        int oddStart = 1;
        int evenStart = 2;
        // we already dealt with ring 0 so start at 1
        for (int i = 1; i < radius; i++) {
            ArrayList<Integer> curIRing = iRings.get(i);
            int curIRingSize = curIRing.size();
            int iOdd = oddStart;
            int iEven = evenStart;
            interXCoords[iOffset + iOdd] = xCenter + (i * w) + (w / 2);
            interXCoords[iOffset + iEven] = xCenter + (i * w) + (w / 2);
            interYCoords[iOffset + iOdd] = yCenter - (s / 2);
            interYCoords[iOffset + iEven] = yCenter + (s / 2);
            int oddInc = i + 1;
            int evenInc = i;
            for (int j = 0; j < HexShape.NUM_SIDES; j++) {
                for (int k = 0; k < oddInc; k++) {
                    iOdd = (iOdd + 2) % curIRingSize;
                    int prevIOdd = (iOdd - 2 + curIRingSize) % curIRingSize;
                    interXCoords[iOffset + iOdd] = getNextX(interXCoords[iOffset + prevIOdd], w, j);
                    interYCoords[iOffset + iOdd] = getNextY(interYCoords[iOffset + prevIOdd], h, s, j);
                }
                for (int k = 0; k < evenInc; k++) {
                    iEven = (iEven + 2) % curIRingSize;
                    int prevIEven = (iEven - 2 + curIRingSize) % curIRingSize;
                    interXCoords[iOffset + iEven] = getNextX(interXCoords[iOffset + prevIEven], w, j);
                    interYCoords[iOffset + iEven] = getNextY(interYCoords[iOffset + prevIEven], h, s, j);
                }
                oddInc = ((oddInc + 1 - i) % 2) + i;
                evenInc = ((evenInc + 1 - i) % 2) + i;
            }
            iOffset += curIRingSize;
        }
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
            int direction;
            if (xDiff < 0 && yDiff < 0) { direction = HexShape.NW; }
            else if (xDiff == 0 && yDiff < 0) { direction = HexShape.W; }
            else if (xDiff > 0 && yDiff < 0) { direction = HexShape.SW; }
            else if (xDiff > 0 && yDiff > 0) { direction = HexShape.SE; }
            else if (xDiff == 0 && yDiff > 0) { direction = HexShape.E; }
            else /* if (xDiff < 0 && yDiff > 0) */ { direction = HexShape.NE; }
            // use dummy hex shape to calculate lengths
            HexShape shape = new HexShape(0, 0, HexShape.BALANCE, s / 2, HexShape.SIDE);
            double wTemp = shape.getBalanceWidth();
            double hTemp = shape.getBalanceHeight();
            double sTemp = shape.getSide();
            // use decider to find coordinates
            portXCoords[i / 2] = getNextX(xOne + (xDiff / 2), wTemp, direction);
            portYCoords[i / 2] = getNextY(yOne + (yDiff / 2), hTemp, sTemp, direction);
        }
    }
    private double getNextX(double x, double w, int direction) {
        double xNew = x;
        switch (direction) {
            case HexShape.NW: case HexShape.SW:
                xNew -= w / 2;
                break;
            case HexShape.W:
                xNew -= w;
                break;
            case HexShape.SE: case HexShape.NE:
                xNew += w / 2;
                break;
            case HexShape.E:
                xNew += w;
                break;
            default:
                // we shouldn't be here
        }
        return xNew;
    }
    private double getNextY(double y, double h, double s, int direction) {
        double yNew = y;
        switch (direction) {
            case HexShape.NW: case HexShape.NE:
                yNew += (h + s) / 2;
                break;
            case HexShape.W: case HexShape.E:
                // yNew = y;
                break;
            case HexShape.SW: case HexShape.SE:
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
