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
		if (devDeck.size() == 0) return null;
		
		int rand = (int) (Math.random() * devDeck.size());
		int rowIndex = 0;
		int temp = 0;
		for ( ; temp < rand; rowIndex++) temp += devDeck.size(rowIndex);
		if (temp > rand) rowIndex--; // whoops, we overshot
		while (devDeck.size(rowIndex) == 0) rowIndex++;
		
		return devDeck.remove(rowIndex);
    }
    public Resource drawResourceCard(int resourceType) {
        return resourceDecks.remove(resourceType);
    }
    public boolean putResourceCard(Resource resource) {
        return resourceDecks.add(resource);
    }
    
    /* Debug */
    
    public void printResources() {
        System.out.println(Resource.BRICK_NAME + ": " + resourceDecks.size(Resource.BRICK));
        System.out.println(Resource.GRAIN_NAME + ": " + resourceDecks.size(Resource.GRAIN));
        System.out.println(Resource.LUMBER_NAME + ": " + resourceDecks.size(Resource.LUMBER));
        System.out.println(Resource.ORE_NAME + ": " + resourceDecks.size(Resource.ORE));
        System.out.println(Resource.WOOL_NAME + ": " + resourceDecks.size(Resource.WOOL));
    }
    public void printDevCards() { // simulate drawing cards
        for (DevCard d = drawDevCard(); d != null; d = drawDevCard()) {
			System.out.println(d);
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

