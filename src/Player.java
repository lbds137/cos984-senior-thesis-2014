import java.util.ArrayList;

public abstract class Player {
    
	/* Constants */
	
	public static final int NUM_PLAYERS = 4;
    public static final int BLUE = 1;
    public static final int ORANGE = 2;
    public static final int RED = 3;
    public static final int WHITE = 4;
	public static final String BLUE_NAME = "Blue";
	public static final String ORANGE_NAME = "Orange";
	public static final String RED_NAME = "Red";
	public static final String WHITE_NAME = "White";
    public static final int MAX_ROADS = 15;
    public static final int MAX_SETTLEMENTS = 5;
    public static final int MAX_CITIES = 4;
	
	/* Fields */
	
	private int id;
    private ArrayList<String> roads; // roads built (String = location)
    private ArrayList<String> settlements; // settlements built (String = location)
    private ArrayList<String> cities; // cities built (String = location)
    private boolean longestRoad;
    private boolean largestArmy;
    private ArrayList<Integer> resources;
    private ArrayList<Integer> devCards;
    private ArrayList<Integer> publicDevCards; // dev cards that have already been played
    private int knights;
    private int devVP;
	private int roadsBuilt;
    private int roadsFree;
    private int settlementsBuilt;
    private int settlementsFree;
    private int citiesBuilt;
    private int citiesFree;
    
	/* Constructors */
	
    public Player() {
        
    }
	
	/* Getters */
	
	public int getId() {
		return id;
	}
	public int getPublicVP() {
        int VP = 0;
        if (longestRoad) VP += 2;
        if (largestArmy) VP += 2;
        VP += settlements.size();
        VP += (2 * cities.size());
        return VP;
    }
    public int getTotalVP() {
        return (getPublicVP() + devVP);
    }
    
	/* Operations */
	
    public boolean buildRoad(String location) {
        // return indicates success or error
        return false;
    }
    public boolean buildSettlement(String location) {
        // return indicates success or error
        return false;
    }
    public boolean buildCity(String location) {
        // return indicates success or error
        return false;
    }
	
	/* Inherits / overrides */
	
	@Override
	public String toString() {
		switch (id) {
			case BLUE: 
				return BLUE_NAME;
			case ORANGE:
				return ORANGE_NAME;
			case RED:
				return RED_NAME;
			case WHITE:
				return WHITE_NAME;
			default:
				return "Invalid player";
		}
	}
    
    /* Abstract methods */
    
    public abstract void firstMove();
    public abstract void secondMove();
    public abstract void normalMove();
	// cards discarded by player when 7 is rolled. Can be empty if player has 7 or fewer cards 
    public abstract int[][] discard();
}
