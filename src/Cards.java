import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Cards {

    private ArrayList<Integer> devCards;
    private int[] resourceCards;
    
    /* Constructors */
    
    public Cards() {
        devCards = new ArrayList<Integer>(Arrays.asList(Constants.DEV_CARDS));
        Collections.shuffle(devCards);
        resourceCards = new int[Constants.NUM_RESOURCES];
        for (int i = 0; i < resourceCards.length; i++) {
            resourceCards[i] = Constants.LAND.length; // 19 resource cards of each type
        }
    }
    
    /* Operations */
    
    public int drawDevCard() {
        if (devCards.isEmpty()) return Constants.NOTHING;
        else return devCards.remove(devCards.size() - 1);
    }
    public int drawResourceCard(int resource) {
        if (resourceCards[resource] > 0) {
            resourceCards[resource]--;
            return resource;
        }
        else {
            return Constants.NOTHING;
        }
    }
    public void putResourceCard(int resource) {
        if (resourceCards[resource] < Constants.LAND.length) {
            resourceCards[resource]++; // only add if deck isn't full
        }
    }
    
    /* Debug */
    
    public void printResources() {
        System.out.println("Brick: " + resourceCards[Constants.BRICK]);
        System.out.println("Grain: " + resourceCards[Constants.GRAIN]);
        System.out.println("Lumber: " + resourceCards[Constants.LUMBER]);
        System.out.println("Ore: " + resourceCards[Constants.ORE]);
        System.out.println("Wool: " + resourceCards[Constants.WOOL]);
    }
    public void printDevCards() {
        for (Integer card : devCards) {
            System.out.println(card);
        }
    }
    
    /* Testing */
    
    public static void main(String args[]) {
        Cards c = new Cards();
        c.printDevCards();
        //c.printResources();
    }
}