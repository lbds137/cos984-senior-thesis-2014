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
    private ArrayList<Road> roads;
    private ArrayList<Intersection> settlements;
    private ArrayList<Intersection> cities;
    private int roadsFree;
    private int settlementsFree;
    private int citiesFree;
    private ResourceBundle resourceCards;
    private DevCardBundle devCards;
    private DevCardBundle playedDevCards;
    
    /* Constructors */
    
    public Player(int id) {
        switch (id) {
            case BLUE: case ORANGE: case RED: case WHITE:
                this.id = id;
                roads = new ArrayList<Road>(MAX_ROADS);
                settlements = new ArrayList<Intersection>(MAX_SETTLEMENTS);
                cities = new ArrayList<Intersection>(MAX_CITIES);
                roadsFree = MAX_ROADS;
                settlementsFree = MAX_SETTLEMENTS;
                citiesFree = MAX_CITIES;
                resourceCards = new ResourceBundle();
                devCards = new DevCardBundle();
                playedDevCards = new DevCardBundle();
                break;
            default:
                //
        }
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
    public int getLongestRoad() {
        return findLongestRoad();
    }
    
    /* Operations */
    
    // build the road and return a boolean status (true if success, false if failure)
    public boolean buildRoad(Road r, ResourceBundle resDeck) {
        // must check that this player has enough resources to build this road
        if (!resourceCards.canRemove(Road.ROAD_COST)) return false;
        
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
        resDeck.add(resourceCards.remove(Road.ROAD_COST));
        return true;
    }
    // build the settlement and return a boolean status (true if success, false if failure)
    public boolean buildSettlement(Intersection i, ResourceBundle resDeck) {
        // must check that this player has enough resources to build this settlement
        if (!resourceCards.canRemove(Building.SETTLEMENT_COST)) return false;
        
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
        resDeck.add(resourceCards.remove(Building.SETTLEMENT_COST));
        return true;
    }
    // build the city and return a boolean status (true if success, false if failure)
    public boolean buildCity(Intersection i, ResourceBundle resDeck) {
        // must check that this player has enough resources to build this settlement
        if (!resourceCards.canRemove(Building.CITY_COST)) return false;
        
        // must check if there exists a settlement owned by this player
        if (settlements.indexOf(i) == -1) return false;
        
        // attempt to actually perform the build
        if (!i.upgrade(this)) return false;
        
        settlements.remove(i);
        settlementsFree += 1;
        cities.add(i);
        citiesFree -= 1;
        resDeck.add(resourceCards.remove(Building.CITY_COST));
        return true;
    }
    // build a dev card and return a boolean status (true if success, false if failure)
    public boolean buildDevCard(DevCardBundle devDeck, ResourceBundle resDeck) {
        // must check that this player has enough resources to build a dev card
        if (!resourceCards.canRemove(DevCard.DEV_CARD_COST)) return false;
        
        // attempt to draw a dev card
        DevCard card = devDeck.removeRandom();
        if (card == null) return false;
        
        devCards.add(card);
        resDeck.add(resourceCards.remove(DevCard.DEV_CARD_COST));
        return true;
    }
    // attempts to collect resources (fails if deck doesn't have enough cards)
    public boolean collectResources(int diceRoll, ResourceBundle resDeck) {
        int[] resourcesOwed = new int[Resource.NUM_TYPES];
        ArrayList<Intersection> buildings = new ArrayList<Intersection>();
        buildings.addAll(settlements);
        buildings.addAll(cities);
        
        for (int i = 0; i < buildings.size(); i++) {
            ArrayList<Hex> hexes = buildings.get(i).getHexes();
            for (int j = 0; j < hexes.size(); j++) {
                Hex h = hexes.get(j);
                if ((!h.hasRobber()) && (h.getDiceRoll() == diceRoll)) {
                    int resourceType = h.getResource().getResourceType();
                    Building b = buildings.get(i).getBuilding();
                    resourcesOwed[resourceType] += b.getNumResources();
                }
            }
        }
        
        if (!resDeck.canRemove(resourcesOwed)) return false;
        resourceCards.add(resDeck.remove(resourcesOwed));
        return true;
    }
    public boolean doPortTrade(Resource r, Resource s, ResourceBundle resDeck) {
        int ratio = findBestRatio(r);
        int[] rRemove = new int[Resource.NUM_TYPES];
        rRemove[r.getResourceType()] = ratio;
        
        if (!resourceCards.canRemove(rRemove) || !resDeck.canRemove(s.getResourceType())) return false; 
        resDeck.add(resourceCards.remove(rRemove));
        resourceCards.add(resDeck.remove(s.getResourceType()));
        return true;
    }
    
    /* Private helpers */
    
    // gets best ratio of r for port trade
    private int findBestRatio(Resource r) {
        ArrayList<Intersection> buildings = new ArrayList<Intersection>();
        buildings.addAll(settlements);
        buildings.addAll(cities);
        
        Port bestPort = buildings.get(0).getPort();
        for (int i = 1; i < buildings.size(); i++) {
            Port curPort = buildings.get(i).getPort();
            if (curPort.compareRatio(bestPort, r) > 0) bestPort = curPort;
        }
        return bestPort.getRatio();
    }
    // returns the length of this player's longest road
    private int findLongestRoad() {
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
        
        int n = iAll.last() + 1;
        boolean[][] iGraph = new boolean[n][n];
        // create intersection graph
        for (int i = 0; i < roads.size(); i++) {
            ArrayList<Integer> both = roads.get(i).both();
            iGraph[both.get(0)][both.get(1)] = true;
            iGraph[both.get(1)][both.get(0)] = true;
        }
        
        TreeSet<Integer> longestRoads = new TreeSet<Integer>();
        for (Integer i : iTermini) {
            ArrayList<Integer> idsVisited = new ArrayList<Integer>();
            idsVisited.add(i);
            longestRoads.add(findLongestRoad(idsVisited, iGraph)); // recursive helper
        }
        
        return longestRoads.last();
    }
    // recursive helper method for findLongestRoad()
    private int findLongestRoad(ArrayList<Integer> idsVisited, boolean[][] iGraph) {
        int iCurrent = idsVisited.get(idsVisited.size() - 1);
        ArrayList<Integer> nextIds = new ArrayList<Integer>((HexShape.NUM_SIDES / 2) - 1);
        for (int k = 0; k < iGraph[iCurrent].length; k++) {
            if (k == iCurrent) continue;
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
                longestRoads.add(findLongestRoad(newIdsVisited, iGraph));
            }
        }
        /* base case: dead-ended (into a loop) */
        if (longestRoads.size() == 0) return idsVisited.size();
        
        /* recursive case: return the longest road (out of 1 or more candidates, 
                           depending on whether or not there is a fork) */
        return longestRoads.last();
    }
    
    // todo: trading functionality (with bank and other players)
    
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
}
