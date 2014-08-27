import java.util.ArrayList;
import java.awt.Color;

public class Player {
    
    /* Constants */
    
    public static final int BLUE = 0;
    public static final int ORANGE = 1;
    public static final int RED = 2;
    public static final int WHITE = 3;
    public static final Color[] COLORS = {StdDraw.BLUE,StdDraw.ORANGE,
                                          StdDraw.RED,StdDraw.WHITE,
                                          StdDraw.GREEN,StdDraw.LIGHT_GRAY,
                                          StdDraw.MAGENTA,StdDraw.PINK};
    public static final String[] NAMES = {"Blue Player","Orange Player",
                                          "Red Player","White Player",
                                          "Green Player","Light Gray Player",
                                          "Magenta Player","Pink Player"};
    
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
        if (id < 0) { this.id = id * Integer.signum(id); }
        else { this.id = id; }
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
    // collect the starting resources
    public boolean collectResources(ResourceBundle resDeck) {
        int[] resourcesOwed = new int[Resource.NUM_TYPES];
        Intersection secondSettlement = settlements.get(1);
        ArrayList<Hex> hexes = secondSettlement.getHexes();
        for (Hex h : hexes) {
            int resourceType = h.getResource().getResourceType();
            if (resourceType == Resource.DESERT) { continue; }
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
    public void stealResource(Player other) {
        Resource r = resourceCards.removeRandom();
        if (r == null) { return; }
        other.stealResource(r);
        System.out.println(other + " stole a card (" + r + ") from " + this + "!"); 
    }
    // receive stolen card
    public void stealResource(Resource r) {
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
    
    // todo: trading functionality with other players
    
    /* Inherits / overrides */
    
    @Override
    public String toString() {
        if (id < NAMES.length) { return NAMES[id]; }
        else { return "Player " + (id + 1); }
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
        if (p.getId() < COLORS.length) { return COLORS[p.getId()]; }
        else { return StdDraw.BLACK; }
    }
}
