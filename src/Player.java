import java.util.ArrayList;
import java.util.TreeSet;

public class Player {
    
    /* Constants */
    
    public static final int NUM_PLAYERS = 4;
    public static final int BLUE = 0;
    public static final int ORANGE = 1;
    public static final int RED = 2;
    public static final int WHITE = 3;
    public static final String BLUE_NAME = "Blue Player";
    public static final String ORANGE_NAME = "Orange Player";
    public static final String RED_NAME = "Red Player";
    public static final String WHITE_NAME = "White Player";
    public static final int INITIAL_FREE_ROADS = 2;
    public static final int INITIAL_FREE_SETTLEMENTS = 2;
    public static final int LONGEST_ROAD_VP = 2;
    public static final int LARGEST_ARMY_VP = 2;
    
    /* Fields */
    
    private int id;
    private int maxRoads;
    private int maxSettlements;
    private int maxCities;
    private ArrayList<Road> roads;
    private ArrayList<Intersection> settlements;
    private ArrayList<Intersection> cities;
    // free means "without cost" (it doesn't mean "available")
    private int freeRoads;
    private int freeSettlements;
    private int freeCities; // used for debugging
    private ResourceBundle resourceCards;
    private DevCardBundle devCards;
    private DevCardBundle playedDevCards;
    private int targetVP;
    private int publicVP;
    private int privateVP;
    private boolean hasLongestRoad;
    private boolean hasLargestArmy;
    
    /* Constructors */
    
    public Player(int id, int VP) {
        if (id < 0) { System.exit(1); }
        this.id = id;
        maxRoads = VP + (VP / 2);
        maxSettlements = maxRoads - VP;
        maxCities = maxSettlements - (VP / (Resource.NUM_TYPES * 2));
        roads = new ArrayList<Road>(maxRoads);
        settlements = new ArrayList<Intersection>(maxSettlements);
        cities = new ArrayList<Intersection>(maxCities);
        freeRoads = INITIAL_FREE_ROADS;
        freeSettlements = INITIAL_FREE_SETTLEMENTS;
        freeCities = 0; // used for debugging
        resourceCards = new ResourceBundle();
        devCards = new DevCardBundle();
        playedDevCards = new DevCardBundle();
        targetVP = VP;
        publicVP = 0;
        privateVP = 0;
        hasLongestRoad = false;
        hasLargestArmy = false;
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
    public int getLargestArmy() {
        return playedDevCards.size(DevCard.KNIGHT);
    }
    public int getPublicVP() {
        return publicVP;
    }
    public int getPrivateVP() {
        return publicVP + privateVP;
    }
    
    /* Operations */
    
    public boolean canBuildRoad() {
        return (roads.size() < maxRoads) && 
               (freeRoads > 0 || resourceCards.canRemove(Road.ROAD_COST));
    }
    public boolean canBuildSettlement() {
        return (settlements.size() < maxSettlements) && 
               (freeSettlements > 0 || resourceCards.canRemove(Building.SETTLEMENT_COST));
    }
    public boolean canBuildCity() {
        return (cities.size() < maxCities) && (settlements.size() > 0) &&
               (freeCities > 0 || resourceCards.canRemove(Building.CITY_COST));
    }
    
    // build the road and return a boolean status (true if success, false if failure)
    public boolean buildRoad(Road r, ResourceBundle resDeck) {
        // is the player able to build ANY road?
        if (!canBuildRoad()) { return false; }
        // must check if road is adjacent to a road owned by this player
        boolean isRValid = false;
        for (int i = 0; i < roads.size(); i++) {
            if (roads.get(i).isNeighbor(r)) {
                isRValid = true;
                break;
            }
        }
        // first two roads are special
        if (roads.size() < INITIAL_FREE_ROADS) { isRValid = true; } 
        if (!isRValid) { return false; }
        // attempt to build
        if (!r.build(this)) { return false; }
        // build
        roads.add(r);
        if (freeRoads == 0) { resDeck.add(resourceCards.remove(Road.ROAD_COST)); }
        else { freeRoads--; }
        return true;
    }
    // build the settlement and return a boolean status (true if success, false if failure)
    public boolean buildSettlement(Intersection i, ResourceBundle resDeck) {
        // is the player able to build ANY settlement?
        if (!canBuildSettlement()) { return false; }
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
        // first two settlements are special
        if (settlements.size() < INITIAL_FREE_SETTLEMENTS) { isSValid = true; } 
        if (!isSValid) { return false; }
        // must check if there exists an unowned and empty space for building
        if (i.getPlayer() != null || i.getBuilding().getBuildingType() != Building.OPEN) { return false; }
        // attempt to build
        if (!i.upgrade(this)) { return false; }
        // build
        settlements.add(i);
        if (freeSettlements == 0) { resDeck.add(resourceCards.remove(Building.SETTLEMENT_COST)); }
        else { freeSettlements--; }
        publicVP++;
        return true;
    }
    // build the city and return a boolean status (true if success, false if failure)
    public boolean buildCity(Intersection i, ResourceBundle resDeck) {
        // is the player able to build ANY city?
        if (!canBuildCity()) { return false; }
        // must check if there exists a settlement owned by this player
        if (settlements.indexOf(i) == -1) { return false; }
        // attempt to build
        if (!i.upgrade(this)) { return false; }
        // build
        settlements.remove(i);
        cities.add(i);
        if (freeCities == 0) { resDeck.add(resourceCards.remove(Building.CITY_COST)); }
        publicVP++;
        return true;
    }
    // build a dev card and return a boolean status (true if success, false if failure)
    public boolean buildDevCard(DevCardBundle devDeck, ResourceBundle resDeck) {
        // must check that this player has enough resources to build a dev card
        if (!resourceCards.canRemove(DevCard.DEV_CARD_COST)) { return false; }
        // attempt to draw a dev card
        DevCard card = devDeck.removeRandom();
        if (card == null) { return false; }
        // draw card
        devCards.add(card);
        resDeck.add(resourceCards.remove(DevCard.DEV_CARD_COST));
        // if VP card is drawn, save VP
        switch (card.getCardType()) {
            case DevCard.CHAPEL: case DevCard.UNIVERSITY: case DevCard.PALACE: 
            case DevCard.LIBRARY: case DevCard.MARKET:
                privateVP++;
                break;
        }
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
        
        if (!resDeck.canRemove(resourcesOwed)) { return false; }
        resourceCards.add(resDeck.remove(resourcesOwed));
        return true;
    }
    // trade X cards of r for one card of s
    public boolean doPortTrade(Resource r, Resource s, ResourceBundle resDeck) {
        int ratio = findBestRatio(r);
        int[] rRemove = new int[Resource.NUM_TYPES];
        rRemove[r.getResourceType()] = ratio;
        
        if (!resourceCards.canRemove(rRemove) || !resDeck.canRemove(s.getResourceType())) { return false; }
        resDeck.add(resourceCards.remove(rRemove));
        resourceCards.add(resDeck.remove(s.getResourceType()));
        return true;
    }
    public DevCard playDevCard(int type) {
        // check whether player owns at least one dev card of given type
        if (devCards.size(type) == 0) { return null; }
        DevCard card = devCards.remove(type);
        playedDevCards.add(card);
        // if VP card played, decrease private VP and increase public VP
        switch (card.getCardType()) {
            case DevCard.CHAPEL: case DevCard.UNIVERSITY: case DevCard.PALACE: 
            case DevCard.LIBRARY: case DevCard.MARKET:
                privateVP--;
                publicVP++;
                break;
        }
        return card;
    }
    // used for Road Building dev card (and debugging)
    public void giveFreeRoads(int n) {
        freeRoads += n;
    }
    // used for debugging
    public void giveFreeSettlements(int n) {
        freeSettlements += n;
    }
    // used for debugging
    public void giveFreeCities(int n) {
        freeCities += n;
    }
    public void giveLongestRoad() {
        hasLongestRoad = true;
        publicVP += LONGEST_ROAD_VP;
    }
    public void takeLongestRoad() {
        hasLongestRoad = false;
        publicVP -= LONGEST_ROAD_VP;
    }
    public void giveLargestArmy() {
        hasLargestArmy = true;
        publicVP += LARGEST_ARMY_VP;
    }
    public void takeLargestArmy() {
        hasLargestArmy = false;
        publicVP -= LARGEST_ARMY_VP;
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
            if (curPort.compareRatio(bestPort, r) > 0) { bestPort = curPort; }
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
                if (common != Constants.INVALID) { iCommon.add(common); }
            }
        }
        // find termini by set minus operation
        TreeSet<Integer> iTermini = new TreeSet<Integer>(iAll);
        iTermini.removeAll(iCommon);
        
        //System.out.println(iTermini);
        
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
            longestRoads.add(findLongestRoad(idsVisited, iGraph, Constants.INVALID)); // recursive helper
        }
        
        return longestRoads.last();
    }
    // recursive helper method for findLongestRoad()
    private int findLongestRoad(ArrayList<Integer> idsVisited, boolean[][] iGraph, int prevId) {
        int iCurrent = idsVisited.get(idsVisited.size() - 1);
        ArrayList<Integer> nextIds = new ArrayList<Integer>((HexShape.NUM_SIDES / 2) - 1);
        for (int k = 0; k < iGraph[iCurrent].length; k++) {
            if (k == iCurrent) continue;
            if (iGraph[iCurrent][k] && k != prevId) nextIds.add(k);
        }
        /* base case: dead-ended (not into a loop) */
        if (nextIds.size() == 0) { return idsVisited.size() - 1; }
        TreeSet<Integer> longestRoads = new TreeSet<Integer>();
        //System.out.println(idsVisited);
        for (int j = 0; j < nextIds.size(); j++) {
            ArrayList<Integer> newIdsVisited = new ArrayList<Integer>(idsVisited);
            // continue only to intersections not visited yet
            if (idsVisited.indexOf(nextIds.get(j)) == -1) {
                newIdsVisited.add(nextIds.get(j));
                longestRoads.add(findLongestRoad(newIdsVisited, iGraph, iCurrent));
            }
        }
        /* base case: dead-ended (into a loop) */
        if (longestRoads.size() == 0) { return idsVisited.size(); }
        //System.out.println("ever got here?");
        /* recursive case: return the longest road (out of 1 or more candidates, 
                           depending on whether or not there is a fork) */
        return longestRoads.last();
    }
    
    // todo: trading functionality (with bank and other players)
    
    /* Inherits / overrides */
    
    @Override
    public String toString() {
        switch (id) { // only the first four players get fancy names
            case BLUE: 
                return BLUE_NAME;
            case ORANGE:
                return ORANGE_NAME;
            case RED:
                return RED_NAME;
            case WHITE:
                return WHITE_NAME;
            default:
                return "Player " + (id + 1);
        }
    }
    
     /* Testing */
    
    public static void main(String args[]) {
        int VP = 10;
        Board b = new Board();
        int dim = 700;
        Intersection[] intersections = b.getIntersections();
        Road[][] roads = b.getIGraph();
        Player pOne = new Player(0, 10);
        Player pTwo = new Player(1, 10);
        Player pThree = new Player(2, 10);
        Player pFour = new Player(3, 10);
        
        pOne.giveFreeRoads(100);
        pOne.giveFreeSettlements(100);
        pOne.giveFreeCities(100);
        //pOne.buildSettlement(intersections[0], null);
        //pOne.buildSettlement(intersections[3], null);
        /*
        pOne.buildRoad(roads[0][1], null);
        pOne.buildRoad(roads[3][4], null);
        pOne.buildRoad(roads[4][15],null);
        pOne.buildRoad(roads[4][5],null);
        pOne.buildRoad(roads[5][0],null);
        pOne.buildRoad(roads[1][2],null);
        pOne.buildRoad(roads[2][3],null);
        pOne.buildRoad(roads[1][6],null);
        pOne.buildRoad(roads[6][7],null);
        pOne.buildRoad(roads[8][7],null);
        pOne.buildRoad(roads[8][9],null);
        pOne.buildRoad(roads[2][9],null);
        pOne.buildRoad(roads[7][24],null);
        pOne.buildRoad(roads[0][21],null);
        pOne.buildRoad(roads[21][22],null);
        pOne.buildRoad(roads[22][23],null); // fails because max number of roads built
        */
        //pOne.buildCity(intersections[0], null);
        //pOne.buildCity(intersections[2], null); // fails because there is no city there
        BoardDraw bd = new BoardDraw(b, dim);
        bd.draw();
        UserInput.buildSettlements(pOne, null, intersections, roads, bd);
        UserInput.buildSettlements(pTwo, null, intersections, roads, bd);
        UserInput.buildSettlements(pThree, null, intersections, roads, bd);
        UserInput.buildSettlements(pFour, null, intersections, roads, bd);
        //System.out.println(pOne.getLongestRoad());
        bd.save("result.png");
        System.exit(0);
    }
}
