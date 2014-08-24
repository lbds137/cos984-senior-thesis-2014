import java.util.ArrayList;
import java.util.TreeSet;
import java.awt.Color;

public class Player {
    
    /* Constants */
    
    public static final int BLUE = 0;
    public static final int ORANGE = 1;
    public static final int RED = 2;
    public static final int WHITE = 3;
    public static final Color[] COLORS = {StdDraw.BLUE,StdDraw.ORANGE,
                                          StdDraw.RED,StdDraw.WHITE};
    public static final String[] NAMES = {"Blue Player","Orange Player",
                                          "Red Player","White Player"};
    
    /* Fields */
    
    private int id;
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
    private int publicVP;
    private int privateVP;
    private boolean hasLongestRoad;
    private boolean hasLargestArmy;
    
    /* Constructors */
    
    public Player(int id) {
        if (id < 0) { System.exit(1); }
        this.id = id;
        roads = new ArrayList<Road>(Rules.getMaxRoads());
        settlements = new ArrayList<Intersection>(Rules.getMaxSettlements());
        cities = new ArrayList<Intersection>(Rules.getMaxCities());
        freeRoads = Rules.INITIAL_FREE_ROADS;
        freeSettlements = Rules.INITIAL_FREE_SETTLEMENTS;
        freeCities = 0; // used for debugging
        resourceCards = new ResourceBundle();
        devCards = new DevCardBundle();
        playedDevCards = new DevCardBundle();
        publicVP = 0;
        privateVP = 0;
        hasLongestRoad = false;
        hasLargestArmy = false;
    }
    
    /* Getters */
    
    public int getId() { return id; }
    public ArrayList<Road> getRoads() { return roads; }
    public ArrayList<Intersection> getSettlements() { return settlements; }
    public ArrayList<Intersection> getCities() { return cities; }
    public ResourceBundle getResourceCards() { return resourceCards; }
    public DevCardBundle getDevCards() { return devCards; }
    public DevCardBundle getPlayedDevcards() { return playedDevCards; }
    public int getLongestRoad() { return findLongestRoad(); }
    public int getLargestArmy() { return playedDevCards.size(DevCard.KNIGHT); }
    public int getPublicVP() { return publicVP; }
    public int getVP() { return publicVP + privateVP; }
    
    /* Verification methods */
    
    public boolean canBuildRoad() {
        return (roads.size() < Rules.getMaxRoads()) && 
               (freeRoads > 0 || resourceCards.canRemove(Rules.ROAD_COST));
    }
    public boolean canBuildRoad(Road r) {
        // is the player able to build ANY road? is the given road already owned?
        if (!canBuildRoad() || !r.canBuild()) { return false; }
        boolean isValid = false;
        // the first road must be adjacent to the first settlement and the second road to the second settlement
        if (roads.size() < Rules.INITIAL_FREE_ROADS) {
            if (r.other(settlements.get(roads.size()).getId()) != Constants.INVALID) { isValid = true; }
        }
        // non-special roads must be adjacent to another road
        else {
            for (int i = 0; !isValid && i < roads.size(); i++) {
                if (roads.get(i).isNeighbor(r)) { isValid = true; }
            }
        }
        return isValid;
    }
    public boolean canBuildSettlement() {
        return (settlements.size() < Rules.getMaxSettlements()) && 
               (freeSettlements > 0 || resourceCards.canRemove(Rules.SETTLEMENT_COST));
    }
    public boolean canBuildSettlement(Intersection i) {
        // is the player able to build ANY settlement? is the given location viable?
        if (!canBuildSettlement() || !i.canBuildSettlement(this)) { return false; }
        // must check if settlement is adjacent to a road owned by this player
        /* NOTE: it is the responsibility of the Game class to check whether 
           settlement location is valid (i.e. not adjacent to another settlement) */
        int id = i.getId();
        boolean isValid = false;
        // first two settlements are special
        if (settlements.size() < Rules.INITIAL_FREE_SETTLEMENTS) { isValid = true; }
        else {
            for (int k = 0; k < roads.size(); k++) {
                if (roads.get(k).other(id) != Constants.INVALID) {
                    isValid = true;
                    break;
                }
            }
        }
        return isValid;
    }
    public boolean canBuildCity() {
        return (cities.size() < Rules.getMaxCities()) && (settlements.size() > 0) &&
               (freeCities > 0 || resourceCards.canRemove(Rules.CITY_COST));
    }
    public boolean canBuildCity(Intersection i) {
        // is the player able to build ANY city? is the given location viable?
        if (!canBuildCity() || !i.canBuildCity(this)) { return false; }
        // must check if there exists a settlement owned by this player
        if (settlements.indexOf(i) == -1) { return false; }
        else { return true; }
    }
    
    /* Operations */
    
    // build the road and return a boolean status (true if success, false if failure)
    public boolean buildRoad(Road r, ResourceBundle resDeck) {
        if (!canBuildRoad(r)) { return false; }
        r.build(this);
        roads.add(r);
        if (freeRoads == 0) { resDeck.add(resourceCards.remove(Rules.ROAD_COST)); }
        else { freeRoads--; }
        return true;
    }
    // build the settlement and return a boolean status (true if success, false if failure)
    public boolean buildSettlement(Intersection i, ResourceBundle resDeck) {
        if (!canBuildSettlement(i)) { return false; }
        i.upgrade(this);
        settlements.add(i);
        if (freeSettlements == 0) { resDeck.add(resourceCards.remove(Rules.SETTLEMENT_COST)); }
        else { freeSettlements--; }
        publicVP++;
        return true;
    }
    // build the city and return a boolean status (true if success, false if failure)
    public boolean buildCity(Intersection i, ResourceBundle resDeck) {
        if (!canBuildCity(i)) { return false; }
        i.upgrade(this);
        settlements.remove(i);
        cities.add(i);
        if (freeCities == 0) { resDeck.add(resourceCards.remove(Rules.CITY_COST)); }
        publicVP++;
        return true;
    }
    // build a dev card and return a boolean status (true if success, false if failure)
    public boolean buildDevCard(DevCardBundle devDeck, ResourceBundle resDeck) {
        // must check that this player has enough resources to build a dev card
        if (!resourceCards.canRemove(Rules.DEV_CARD_COST)) { return false; }
        // attempt to draw a dev card
        DevCard card = devDeck.removeRandom();
        if (card == null) { return false; }
        // draw card
        devCards.add(card);
        resDeck.add(resourceCards.remove(Rules.DEV_CARD_COST));
        // if VP card is drawn, save VP
        switch (card.getCardType()) {
            case DevCard.CHAPEL: case DevCard.UNIVERSITY: case DevCard.PALACE: 
            case DevCard.LIBRARY: case DevCard.MARKET:
                privateVP++;
                break;
        }
        return true;
    }
    // 
    public boolean collectStartingResources(ResourceBundle resDeck) {
        int[] resourcesOwed = new int[Resource.NUM_TYPES];
        Intersection secondSettlement = settlements.get(1);
        ArrayList<Hex> hexes = secondSettlement.getHexes();
        for (Hex h : hexes) {
            int resourceType = h.getResource().getResourceType();
            Building b = secondSettlement.getBuilding();
            resourcesOwed[resourceType] += b.getNumResources();
        }
        if (!resDeck.canRemove(resourcesOwed)) { return false; }
        resourceCards.add(resDeck.remove(resourcesOwed));
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
                if (h.hasRobber() || h.getResource().getResourceType() == Resource.DESERT) { continue; }
                if (h.getDiceRoll() == diceRoll) {
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
    public boolean discard(int[] cardsToRemove, ResourceBundle resDeck) {
        if (!resourceCards.canRemove(cardsToRemove)) { return false; }
        resDeck.add(resourceCards.remove(cardsToRemove));
        return true;
    }
    // discard random cards if hand size is larger than 7
    public void discard(ResourceBundle resDeck) {
        int numToDiscard = 0;
        if (resourceCards.size() > 7) { numToDiscard = resourceCards.size() / 2; }
        while (numToDiscard > 0) { 
            resDeck.add(resourceCards.removeRandom());
            numToDiscard--;
        }
    }
    // random card stolen by robber
    public void giveResource(Player other) {
        Resource r = resourceCards.removeRandom();
        if (r == null) { return; }
        other.receiveResource(r);
        System.out.println(other + " stole a card (" + r + ") from " + this + "!"); 
    }
    // receive stolen card
    public void receiveResource(Resource r) {
        resourceCards.add(r);
    }
    // trade X cards of r for one card of s
    public boolean doPortTrade(int r, int s, ResourceBundle resDeck) {
        int ratio = findBestRatio(new Resource(r));
        int[] rRemove = new int[Resource.NUM_TYPES];
        rRemove[r] = ratio;
        
        if (!resourceCards.canRemove(rRemove) || !resDeck.canRemove(s)) { return false; }
        resDeck.add(resourceCards.remove(rRemove));
        resourceCards.add(resDeck.remove(s));
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
        publicVP += Rules.LONGEST_ROAD_VP;
    }
    public void takeLongestRoad() {
        hasLongestRoad = false;
        publicVP -= Rules.LONGEST_ROAD_VP;
    }
    public void giveLargestArmy() {
        hasLargestArmy = true;
        publicVP += Rules.LARGEST_ARMY_VP;
    }
    public void takeLargestArmy() {
        hasLargestArmy = false;
        publicVP -= Rules.LARGEST_ARMY_VP;
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
            case BLUE: case ORANGE: case RED: case WHITE:
                return NAMES[id];
            default:
                return "Player " + (id + 1);
        }
    }
    
    /* Printing */
    public void printResourceCards() {
        System.out.println("Your hand is: " + resourceCards);
    }
    // prints *the sum of public and private* VP
    public void printVP() {
        System.out.println("Your VP score is: " + getVP() + " (out of " + Rules.getMaxVP() + ")");
    }
    
    /* Static methods */
    
    public static Color getPlayerColor(Player p) {
        switch (p.getId()) {
            case BLUE: case ORANGE: case RED: case WHITE:
                return COLORS[p.getId()];
            default: return StdDraw.BLACK;
        }
    }
}
