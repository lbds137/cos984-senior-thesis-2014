import java.util.ArrayList;
import java.util.TreeSet;

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
    private void payBuildCost(int[] buildCost, Decks d) {
        for (int i = 0; i < Resource.NUM_TYPES; i++) {
            for (int j = 0; j < buildCost[i]; j++) {
                d.add(resourceCards.remove(i));
            }
        }
    }
    // recursive helper method for getLongestRoad()
    private int getLongestRoad(ArrayList<Integer> idsVisited, boolean[][] iGraph) {
        int iCurrent = idsVisited.get(idsVisited.size() - 1);
        ArrayList<Integer> nextIds = new ArrayList<Integer>(Intersection.MAX_DEGREE - 1);
        for (int k = 0; k < iGraph[iCurrent].length; k++) {
            if (k = iCurrent) continue;
            if (iGraph[iCurrent][k]) nextIds.add(k);
        }
        /* base case: dead-ended (not into a loop) */
        if (nextIds.size() == 0) return idsVisited.size();
        TreeSet<Integer> longestRoads = new TreeSet<Integer>();
        for (int j = 0; j < nextIds.size(); j++) {
            ArrayList<Integer> newIdsVisited = new ArrayList<Integer>(idsVisited);
            // continue only to intersections not visited yet
            if (idsVisited.indexOf(j) == -1) {
                newIdsVisited.add(nextIds.get(j));
                longestRoads.add(getLongestRoad(newIdsVisited, iGraph);
            }
        }
        /* base case: dead-ended (into a loop) */
        if (longestRoads.size() == 0) return idsVisited.size();
        
        /* recursive case: return the longest road (out of 1 or more candidates, 
                           depending on whether or not there is a fork) */
        return longestRoads.last();
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
    
    // build the road and return a boolean status (true if success, false if failure)
    public boolean buildRoad(Road r, Decks d) {
        // must check that this player has enough resources to build this road
        if (!satisfiesBuildCost(Road.ROAD_COST)) return false;
        
        // must check if road is adjacent to a road owned by this player
        boolean isRValid = false;
        for (int i = 0; i < roads.size(); i++) {
            if (roads.get(i).isNeighbor(r)) {
                isRValid = true;
                break;
            }
        }
        if (!isRValid) return false;
        
        // attempt to actually perform the build
        if (!r.build(this)) return false;
        
        roads.add(r);
        roadsFree -= 1;
        payBuildCost(Road.ROAD_COST, d);
        return true;
    }
    // build the settlement and return a boolean status (true if success, false if failure)
    public boolean buildSettlement(Intersection i, Decks d) {
        // must check that this player has enough resources to build this settlement
        if (!satisfiesBuildCost(Building.SETTLEMENT_COST)) return false;
        
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
        if (!isSValid) return false;
        
        // must check if there exists an unowned and empty space for building
        if (i.getPlayer() != null || i.getBuilding().getBuildingType() != Building.OPEN) return false;
        
        // attempt to actually perform the build
        if (!i.upgrade(this)) return false;
        
        settlements.add(i);
        settlementsFree -= 1;
        payBuildCost(Building.SETTLEMENT_COST, d);
        return true;
    }
    // build the city and return a boolean status (true if success, false if failure)
    public boolean buildCity(Intersection i, Decks d) {
        // must check that this player has enough resources to build this settlement
        if (!satisfiesBuildCost(Building.CITY_COST)) return false;
        
        // must check if there exists a settlement owned by this player
        if (settlements.indexOf(i) == -1) return false;
        
        // attempt to actually perform the build
        if (!i.upgrade(this)) return false;
        
        settlements.remove(i);
        settlementsFree += 1;
        cities.add(i);
        citiesFree -= 1;
        payBuildCost(Building.CITY_COST, d);
        return true;
    }
    // build a dev card and return a boolean status (true if success, false if failure)
    public boolean buildDevCard(Decks d) {
        // must check that this player has enough resources to build a dev card
        if (!satisfiesBuildCost(DevCard.DEV_CARD_COST)) return false;
        
        // attempt to draw a dev card
        DevCard card = d.drawDevCard();
        if (card == null) return false;
        
        devCards.add(card);
        payBuildCost(DevCard.DEV_CARD_COST);
        return true;
    }
    public boolean collectResources(int diceRoll, Decks d) {
        // todo (relies on change made to intersection class)
        return false;
    }
    // returns the length of this player's longest road
    public int getLongestRoad() {
        TreeSet<Integer> iAll = new TreeSet<Integer>();
        // get all intersection IDs touched by this player's roads
        for (int i = 0; i < roads.size(); i++) {
            ArrayList<Integer> both = roads.get(i).both();
            iAll.add(both.get(0));
            iAll.add(both.get(1));
        }
        TreeSet<Integer> iCommon = new TreeSet<Integer>();
        // find vertices that are shared by two or more roads
        for (int i = 0; i < roads.size(); i++) {
            for (int j = i + 1; j < roads.size(); j++) {
                int common = roads.get(i).common(roads.get(j));
                if (common != Constants.INVALID) iCommon.add(common);
            }
        }
        // find termini by set minus operation
        TreeSet<Integer> iTermini = new TreeSet<Integer>(iAll);
        iTermini.removeAll(iCommon);
        
        n = Intersection.GRAPH.length;
        boolean[][] iGraph = new boolean[n][n];
        // create intersection graph
        for (int i = 0; i < roads.size(); i++) {
            ArrayList<Integer> both = roads.get(i).both();
            iGraph[both.get(0)][both.get(1)] = true;
            iGraph[both.get(1)][both.get(0)] = true;
        }
        
        ArrayList<Integer> longestRoads = new ArrayList<Integer>();
        for (Integer i : iTermini) {
            ArrayList<Integer> idsVisited = new ArrayList<Integer>();
            idsVisited.add(i);
            longestRoads.add(getLongestRoad(idsVisited, iGraph));
        }
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
