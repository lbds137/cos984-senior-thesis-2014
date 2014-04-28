import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Decks {

    /* Constants */
    
	// nothing here
    
    /* Fields */
    
    private DevCardBundle devDeck;
    private ResourceBundle resourceDecks;
    
    /* Constructors */
    
    public Decks() {
        devDeck = new DevCardBundle();
        resourceDecks = new ResourceBundle();
        
		for (int i = 0; i < DevCard.NUM_TYPES; i++) {
			for (int j = 0; j < DevCard.MAX_CARDS[i]; j++){
				devDeck.add(new DevCard(i));
			}
		}
		
		for (int i = 0; i < Resource.NUM_TYPES; i++) {
			for (int j = 0; j < Resource.MAX_CARDS[i]; j++) {
				resourceDecks.add(new Resource(i));
			}
		}
    }
    
    /* Operations */
    
    public DevCard drawDevCard() {
        // TODO
        return null;
    }
    public Resource drawResourceCard(int resourceType) {
        // TODO
        return null;
    }
    public boolean putResourceCard(Resource resource) {
        // TODO
        return false;
    }
    
    /* Debug */
    
    public void printResources() {
        System.out.println(Resource.BRICK_NAME + ": " + resourceDecks.size(Resource.BRICK));
        System.out.println(Resource.GRAIN_NAME + ": " + resourceDecks.size(Resource.GRAIN));
        System.out.println(Resource.LUMBER_NAME + ": " + resourceDecks.size(Resource.LUMBER));
        System.out.println(Resource.ORE_NAME + ": " + resourceDecks.size(Resource.ORE));
        System.out.println(Resource.WOOL_NAME + ": " + resourceDecks.size(Resource.WOOL));
    }
    public void printDevCards() {
        for (ArrayList<DevCard> cards : devDeck.getBundle()) {
            for (DevCard card : cards) {	
	            System.out.println(card);
            }
        }
    }
    
    /* Testing */
    
    public static void main(String args[]) {
        Decks c = new Decks();
        c.printDevCards();
        System.out.println();
        c.printResources();
    }
}
