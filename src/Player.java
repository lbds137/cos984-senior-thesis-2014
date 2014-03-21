import java.util.ArrayList;

public abstract class Player {
    
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
    
    public Player() {
        
    }
    
    public int buildRoad(String location) {
        // return indicates success or error
        return -1;
    }
    public int buildSettlement(String location) {
        // return indicates success or error
        return -1;
    }
    public int buildCity(String location) {
        // return indicates success or error
        return -1;
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
    
    /* Abstract methods */
    
    // cards discarded by player when 7 is rolled. Can be empty if player has 7 or fewer cards 
    public abstract void firstMove();
    public abstract void secondMove();
    public abstract void normalMove();
    public abstract int[][] discard();
}
