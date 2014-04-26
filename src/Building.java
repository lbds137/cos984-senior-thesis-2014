
public class Building implements Comparable<Building> {

    /* Constants */
	
	public static final int OPEN = 0;
    public static final int SETTLEMENT = 1;
    public static final int CITY = 2;
	public static final String OPEN_NAME = "Open";
	public static final String SETTLEMENT_NAME = "Settlement";
	public static final String CITY_NAME = "City";

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

	@Override
	public boolean equals(Building b) {
		return compareTo(b) == 0;
	}
	@Override
	public String toString() {
		switch (buildingType) {
			case OPEN: return OPEN_NAME;
			case SETTLEMENT: return SETTLEMENT_NAME;
			case CITY: return CITY_NAME;
			default: return "Invalid building";
		}
	}
	public int compareTo(Building b) {
		// anything is greater than null
		if (b == null) return buildingType + 1;
		// natural ordering defined by values of type constants
		else return buildingType - b.getBuildingType();
	}
	
	/* Operations */
	
	public boolean upgrade() {
		switch (buildingType) {
			// only OPEN or SETTLEMENT can be upgraded
			case OPEN: case SETTLEMENT:
				buildingType++;
                return true;
			default:
				return false;
		}
	}

	/* Testing */

	public static void main(String[] args) {
		Building b = new Building(Building.OPEN);
        Building c = new Building(Building.SETTLEMENT);
		Building d = new Building(Building.CITY);
		Building e = null;
        System.out.println(b.toString());
        System.out.println(c.toString());
        System.out.println(b.compareTo(c));
		System.out.println(d.equals(c));
		System.out.println(c.compareTo(d));
		System.out.println(d.equals(e));
		System.out.println(b.upgrade() + " " + b.getBuildingType());
		System.out.println(c.upgrade() + " " + c.getBuildingType());
		System.out.println(d.upgrade() + " " + d.getBuildingType());
	}
}

