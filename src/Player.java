import java.util.ArrayList;

public class Player {
    
    /* Constants */
    
    public static final int NUM_PLAYERS = 4;
    public static final int BLUE = 0;
    public static final int ORANGE = 1;
    public static final int RED = 2;
    public static final int WHITE = 3;
    public static final String BLUE_NAME = "Blue";
    public static final String ORANGE_NAME = "Orange";
    public static final String RED_NAME = "Red";
    public static final String WHITE_NAME = "White";
    public static final int MAX_ROADS = 15;
    public static final int MAX_SETTLEMENTS = 5;
    public static final int MAX_CITIES = 4;
    
    /* Fields */
    
    private int id;
    private ArrayList<Road> roads; // roads built (String = location)
    private ArrayList<Intersection> settlements;
    private ArrayList<Intersection> cities;
    private int roadsFree;
    private int settlementsFree;
    private int citiesFree;
    private ResourceBundle resourceCards;
    private DevCardBundle devCards;
    private DevCardBundle playedDevCards;
    
    /* Constructors */
    
    public Player(int id) throws Exception {
        switch (id) {
            case BLUE: case ORANGE: case RED: case WHITE:
                this.id = id;
                roads = new ArrayList<Road>(MAX_ROADS);
                settlements = new ArrayList<Building>(MAX_SETTLEMENTS);
                cities = new ArrayList<Building>(MAX_CITIES);
                roadsFree = MAX_ROADS;
                settlementsFree = MAX_SETTLEMENTS;
                citiesFree = MAX_CITIES;
                resourceCards = new ResourceBundle();
                devCards = new DevCardBundle();
                playedDevCards = new DevCardBundle();
                break;
            default:
                // we only accept BLUE, ORANGE, RED, and WHITE players
                throw new Exception();
        }
    }
    
    /* Helper methods */
    
    private boolean satisfiesBuildCost(int[] buildCost) {
        boolean satisfied = true;
        for (int i = 0; i < Resource.NUM_TYPES; i++) {
            if (buildCost[i] > resourceCards.size(i)) satisfied = false;
        }
        return satisfied;
    }
    private ResourceBundle payBuildCost(int[] buildCost) {
        ResourceBundle b = new ResourceBundle();
        for (int i = 0; i < Resource.NUM_TYPES; i++) {
            for (int j = 0; j < buildCost[i]; j++) {
                b.add(resourceCards.remove(i));
            }
        }
        return b;
    }
    
    /* Getters */
    
    public int getId() {
        return id;
    }
    public ArrayList<Road> getRoads() {
        return roads;
    }
    public ArrayList<Intersection> getSettlements() {
        return settlements;
    }
    public ArrayList<Intersection> getCities() {
        return cities;
    }
    public ResourceBundle getResourceCards() {
        return resourceCards;
    }
    public DevCardBundle getDevCards() {
        return devCards;
    }
    public DevCardBundle getPlayedDevcards() {
        return playedDevCards;
    }
    
    /* Operations */
    
    // build the road and remove and return the resource cards needed to build a road (null if build failure)
    public ResourceBundle buildRoad(Road r) {
        // must check that this player has enough resources to build this road
        if (!satisfiesBuildCost(Road.ROAD_COST)) return null;
        
        // must check if road is adjacent to a road owned by this player
        boolean isRValid = false;
        for (int i = 0; i < roads.size(); i++) {
            if (roads.get(i).isNeighbor(r)) {
                isRValid = true;
                break;
            }
        }
        if (!isRValid) return null;
        
        // attempt to actually perform the build
        if (!r.build(this)) return null;
        
        roads.add(r);
        roadsFree -= 1;
        return payBuildCost(Road.ROAD_COST);
    }
    // build the settlement and remove and return the resource cards needed to build a settlement (null if build failure)
    public ResourceBundle buildSettlement(Intersection i) {
        // must check that this player has enough resources to build this settlement
        if (!satisfiesBuildCost(Building.SETTLEMENT_COST)) return null;
        
        // must check if settlement is adjacent to a road owned by this player
        /* todo: don't forget to have Game class check whether settlement location is valid 
           (i.e. not adjacent to another settlement) */
        int id = i.getId();
        boolean isSValid = false;
        for (int k = 0; k < roads.size(); k++) {
            if (roads.get(k).other(id) != Constants.INVALID) {
                isSValid = true;
                break;
            }
        }
        if (!isSValid) return null;
        
        // must check if there exists an unowned and empty space for building
        if (i.getPlayer() != null || i.getBuilding().getBuildingType() != Building.OPEN) return null;
        
        // attempt to actually perform the build
        if (!i.upgrade(this)) return null;
        
        settlements.add(i);
        settlementsFree -= 1;
        return payBuildCost(Building.SETTLEMENT_COST);
    }
    // build the city and remove and return the resource cards needed to build a city (null if build failure)
    public ResourceBundle buildCity(Intersection i) {
        // must check that this player has enough resources to build this settlement
        if (!satisfiesBuildCost(Building.CITY_COST)) return null;
        
        // must check if there exists a settlement owned by this player
        if (settlements.indexOf(i) == -1) return null;
        
        // attempt to actually perform the build
        if (!i.upgrade(this)) return null;
        
        settlements.remove(i);
        settlementsFree += 1;
        cities.add(i);
        citiesFree -= 1;
        return payBuildCost(Building.CITY_COST);
    }
    public boolean buildDevCard() {
        // todo
        return false;
    }
    public boolean drawResourceCard() {
        // todo
        return false;
    }
    // todo: need a protocol for trading between players
    
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
    
    // cards discarded by player when 7 is rolled. Can be empty if player has 7 or fewer cards 
    public ArrayList<ArrayList<Resource>> discard() {
        return null;
    }
}
