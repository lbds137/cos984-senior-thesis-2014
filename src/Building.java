
public class Building implements Comparable<Building> {

    /* Constants */
    
    public static final int OPEN = 0;
    public static final int SETTLEMENT = 1;
    public static final int CITY = 2;
    public static final String[] NAMES = {"Open","Settlement","City"};

    /* Fields */

    private int buildingType;

    /* Constructors */

    public Building(int buildingType) {
        switch (buildingType) {
            case OPEN: case SETTLEMENT: case CITY:
                this.buildingType = buildingType;
                break;
            default:
                this.buildingType = OPEN;
                break;
        }
    }

    /* Getters */

    public int getBuildingType() {
        return buildingType;
    }
    public int getNumResources() {
        return buildingType; // number of resources given on a dice roll
    }

    /* Inherits / overrides */

    public boolean equals(Building b) {
        return compareTo(b) == 0;
    }
    @Override
    public String toString() {
        switch (buildingType) {
            case OPEN: case SETTLEMENT: case CITY: return NAMES[buildingType];
            default: return "Invalid building";
        }
    }
    public int compareTo(Building b) {
        // anything is greater than null
        if (b == null) { return buildingType + 1; }
        // natural ordering defined by values of type constants
        else return buildingType - b.getBuildingType();
    }
    
    /* Operations */
    
    public boolean canUpgrade() {
        switch (buildingType) {
            case OPEN: case SETTLEMENT: return true;
            default: return false;
        }
    }
    public boolean upgrade() {
        if (!canUpgrade()) { return false; }
        else {
            buildingType++;
            return true;
        }
    }
}
