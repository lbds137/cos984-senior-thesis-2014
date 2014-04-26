import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Decks {

    /* Constants */
    
    
    /* Fields */
    
    private ArrayList<DevCard> devDeck;
    private ArrayList<ArrayList<Resource>> resourceDecks;
    
    /* Constructors */
    
    public Decks() {
        devDeck = new ArrayList<DevCard>(DevCard.DECK.length);
        resourceDecks = new ArrayList<ArrayList<Resource>>(Resource.NUM_TYPES);
        
        for (int i = 0; i < DevCard.DECK.length; i++) {
            devDeck.add(new DevCard(DevCard.DECK[i]));
        }
        Collections.shuffle(devDeck);
        
        for (int i = Resource.NUM_TYPES; i > Resource.DESERT; i--) {
            // there are 19 cards for each type of resource
            ArrayList<Resource> resourceDeck = new ArrayList<Resource>(Resource.TILES.length);
            for (int j = 0; j < Resource.TILES.length; j++) {
                resourceDeck.add(new Resource(i));
            }
            resourceDecks.add(resourceDeck);
        }
    }
    
    /* Operations */
    
    public DevCard drawDevCard() {
        if (devDeck.isEmpty()) return null;
        else return devDeck.remove(devDeck.size() - 1);
    }
    public Resource drawResourceCard(int resourceType) {
        ArrayList<Resource> resourceDeck = resourceDecks.get(resourceType);
        if (resourceDeck.isEmpty()) return null;
        else return resourceDeck.remove(resourceDeck.size() - 1);
    }
    public boolean putResourceCard(Resource resource) {
        ArrayList<Resource> resourceDeck = resourceDecks.get(resource.getResourceType());
        if (resourceDeck.size() < Resource.TILES.length) {
            resourceDeck.add(resource);
            return true;
        }
        // if deck is full, we cannot add any more cards
        else return false;
    }
    
    /* Debug */
    
    public void printResources() {
        System.out.println(Resource.BRICK_NAME + ": " + resourceDecks.get(Resource.BRICK).size());
        System.out.println(Resource.GRAIN_NAME + ": " + resourceDecks.get(Resource.GRAIN).size());
        System.out.println(Resource.LUMBER_NAME + ": " + resourceDecks.get(Resource.LUMBER).size());
        System.out.println(Resource.ORE_NAME + ": " + resourceDecks.get(Resource.ORE).size());
        System.out.println(Resource.WOOL_NAME + ": " + resourceDecks.get(Resource.WOOL).size());
    }
    public void printDevCards() {
        for (DevCard card : devDeck) {
            System.out.println(card);
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
