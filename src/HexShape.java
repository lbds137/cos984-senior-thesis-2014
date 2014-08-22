
public class HexShape {
    
    /* Constants */
    
    public static final int NUM_SIDES = 6;
    // parameter selector
    public static final int SIDE = 0;
    public static final int FLAT_WIDTH = 1;
    public static final int FLAT_HEIGHT = 2;
    public static final int BALANCE_WIDTH = 3;
    public static final int BALANCE_HEIGHT = 4;
    // (balance) cardinal directions
    public static final int NW = 0;
    public static final int W = 1;
    public static final int SW = 2;
    public static final int SE = 3;
    public static final int E = 4;
    public static final int NE = 5;
    // rotation selector
    public static final int FLAT = 0;
    public static final int BALANCE = 1;
    
    /* Fields */
    
    private double xCenter;
    private double yCenter;
    private double[] xCoords;
    private double[] yCoords;
    // measurements
    private double side;
    private double flatWidth;
    private double flatHeight;
    private double balanceWidth;
    private double balanceHeight;
    
    // default rotation is flat
    private int rotation;
    
    /* Constructors */
    
    public HexShape(double xCenter, double yCenter, int rotation, double param, int selector) {
        this.xCenter = xCenter;
        this.yCenter = yCenter;
        this.rotation = rotation;
        
        switch (selector) {
            case FLAT_WIDTH:
                givenFlatWidth(param);
                break;
            case FLAT_HEIGHT:
                givenFlatHeight(param);
                break;
            case BALANCE_WIDTH:
                givenBalanceWidth(param);
                break;
            case BALANCE_HEIGHT:
                givenBalanceHeight(param);
                break;
            case SIDE: default:
                givenSide(param);
                break;
        }
        computeCoords();
    }
    private void givenSide(double side) {
        this.side = side;
        flatWidth = 2 * side;
        flatHeight = Math.sqrt(3) * side;
        balanceWidth = flatHeight;
        balanceHeight = flatWidth;
    }
    private void givenFlatWidth(double flatWidth) {
        side = flatWidth / 2;
        this.flatWidth = flatWidth;
        flatHeight = Math.sqrt(3) * side;
        balanceWidth = flatHeight;
        balanceHeight = flatWidth;
    }
    private void givenFlatHeight(double flatHeight) {
        side = flatHeight / Math.sqrt(3);
        flatWidth = 2 * side;
        this.flatHeight = flatHeight;
        balanceWidth = flatHeight;
        balanceHeight = flatWidth;
    }
    private void givenBalanceWidth(double balanceWidth) {
        side = balanceWidth / Math.sqrt(3);
        flatWidth = 2 * side;
        flatHeight = balanceWidth;
        this.balanceWidth = balanceWidth;
        balanceHeight = flatWidth;
    }
    private void givenBalanceHeight(double balanceHeight) {
        side = balanceHeight / 2;
        flatWidth = balanceHeight;
        flatHeight = Math.sqrt(3) * side;
        balanceWidth = flatHeight;
        this.balanceHeight = balanceHeight;
    }
    // order of coordinates is: (case FLAT: clockwise from lowest x) or 
    //                          (case BALANCE: counterclockwise from lowest y)
    private void computeCoords() {
        double[] coords1 = new double[NUM_SIDES];
        double[] coords2 = new double[NUM_SIDES];
        double center1, center2;
        
        switch (rotation) {
            case BALANCE:
                center1 = yCenter;
                center2 = xCenter;
                xCoords = coords2;
                yCoords = coords1;
                break;
            case FLAT: default:
                center1 = xCenter;
                center2 = yCenter;
                xCoords = coords1;
                yCoords = coords2;
                break;
        }
        
        coords1[0] = center1 - (flatWidth / 2); 
        coords2[0] = center2;
        coords1[1] = center1 - (flatWidth / 4);
        coords2[1] = center2 + (flatHeight / 2);
        coords1[2] = center1 + (flatWidth / 4);
        coords2[2] = center2 + (flatHeight / 2);
        coords1[3] = center1 + (flatWidth / 2);
        coords2[3] = center2;
        coords1[4] = center1 + (flatWidth / 4);
        coords2[4] = center2 - (flatHeight / 2);
        coords1[5] = center1 - (flatWidth / 4);
        coords2[5] = center2 - (flatHeight / 2);
    }
    
    /* Getters */
    
    public double getXCenter() { return xCenter; }
    public double getYCenter() { return yCenter; }
    public double getSide() { return side; }
    public double getFlatWidth() { return flatWidth; }
    public double getFlatHeight() { return flatHeight; }
    public double getBalanceWidth() { return balanceWidth; }
    public double getBalanceHeight() { return balanceHeight; }
    public double[] getXCoords() { return xCoords; }
    public double[] getYCoords() { return yCoords; }
    
    /* Inherits / overrides */
    
    @Override
    public String toString() {
        String sXCoords = "x coordinates: { ";
        String sYCoords = "y coordinates: { ";
        
        for (int i = 0; i < NUM_SIDES; i++) {
            sXCoords += String.format("%.2f", xCoords[i]) + " ";
            sYCoords += String.format("%.2f", yCoords[i]) + " ";
        }
        sXCoords += "}";
        sYCoords += "}";
        return sXCoords + "; " + sYCoords;
    }
    
    /* Testing */
    
    public static void main(String[] args) {
        HexShape hBig = new HexShape(250, 250, HexShape.FLAT, 500, HexShape.FLAT_WIDTH);
        System.out.println(hBig);
        HexShape hSmall = new HexShape(250, 250, HexShape.BALANCE, 80, HexShape.BALANCE_WIDTH);
        System.out.println(hSmall);
    }
}
