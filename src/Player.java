import java.util.ArrayList;

public class Player {
    
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
    private ArrayList<Road> roads; // roads built (String = location)
    private ArrayList<Building> buildings;
    private ArrayList<ArrayList<Resource>> resourceCards;
    private ArrayList<DevCard> devCards;
    private ArrayList<DevCard> playedDevCards;
    private int roadsFree;
    private int settlementsFree;
    private int citiesFree;
    
    /* Constructors */
    
    public Player() {
        // abstract class cannot be initialized    
    }
    
    /* Getters */
    
    public int getId() {
        return id;
    }
    public ArrayList<Road> getRoads() {
        return roads;
    }
    public ArrayList<ArrayList<Resource>> getResourceCards() {
        return resourceCards;
    }
    public ArrayList<DevCard> getDevCards() {
        return devCards;
    }
    public ArrayList<DevCard> getPlayedDevcards() {
        return playedDevCards;
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
    public boolean drawDevCard() {
        return false;
    }
    public boolean drawResourceCard() {
        return false;
    }
    public boolean acceptTradeRequest() {
        return false;
    }
    public boolean offerTrade() {
        return false;
    }
    public boolean acceptTradeOffer() {
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
    
    // cards discarded by player when 7 is rolled. Can be empty if player has 7 or fewer cards 
    public ArrayList<ArrayList<Resource>> discard() {
        return null;
    }
}

