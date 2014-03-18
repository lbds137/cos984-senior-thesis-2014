
public class Hex {
    
    /* Private fields */
    
    private int id;
    private int resourceType;
    private int dieNumber;
    
    /* Constructors */
    
    public Hex(int id, int resourceType, int dieNumber) {
        this.id = id;
        this.resourceType = resourceType;
        this.dieNumber = dieNumber;
    }
    
    /* Getters */
    
    public int getId() {
        return id;
    }
    public int getResourceType() {
        return resourceType;
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
    
    /* Operations */
    
    public String toString() { // for debugging purposes
        String s = id + " : ";
        for (int i = 0; i < Constants.HEX; i++) {
            s += Constants.H_I_MAP_GRAPH[id][i];
            if (i + 1 != Constants.HEX) s += " ";
            else s += "\r\n";
        }
        return s;
    }
}