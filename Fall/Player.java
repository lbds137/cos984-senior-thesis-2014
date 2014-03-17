
public class Player {
    
    private int id;
    private Board board;
    private int resourceType;
    private int dieNumber;
    private Intersection[] intersections;
    
    public Player(int id, Board board, int[] intersectionIds, int resourceType, int dieNumber) {
        this.id = id;
        this.board = board;
        Intersection[] all = board.getIntersections();
        this.intersections = new Intersection[Constants.HEX];
        for (int i = 0; i < Constants.HEX; i++) {
            intersections[i] = all[intersectionIds[i]]; 
        }
        this.resourceType = resourceType;
        this.dieNumber = dieNumber;
    }
    
    public String toString() { // for debugging purposes
        String s = id + " : ";
        for (int i = 0; i < Constants.HEX; i++) {
            s += intersections[i].get_id();
            if (i + 1 != Constants.HEX) s += " ";
            else s += "\r\n";
        }
        return s;
    }
    public String getStringResourceType() {
        switch (resourceType) {
            case Constants.DESERT:
                return "Desert";
            case Constants.BRICK:
                return "Brick";
            case Constants.GRAIN:
                return "Grain";
            case Constants.LUMBER:
                return "Lumber";
            case Constants.ORE:
                return "Ore";
            case Constants.WOOL:
                return "Wool";
        }
        return "Invalid Resource Type";
    }
    public int getDieNumber() {
        return dieNumber;
    }
    
}