import java.util.ArrayList;
import java.util.Arrays;

public class DevCard {
    
    /* Constants */
    
	public static final int NUM_TYPES = 9;
    public static final int KNIGHT = 0;
    public static final int ROAD = 1;
    public static final int PLENTY = 2;
    public static final int MONOPOLY = 3;
    public static final int CHAPEL = 4;
    public static final int UNIVERSITY = 5;
    public static final int PALACE = 6;
    public static final int LIBRARY = 7;
    public static final int MARKET = 8;
    public static final ArrayList<String> NAMES = 
        new ArrayList<String>(Arrays.asList("Knight","Road Building","Year Of Plenty",
                                            "Monopoly","Chapel","University","Palace",
                                            "Library","Market"));
    
    /* Fields */
    
    private int cardType;
    
    /* Constructors */
    
    public DevCard(int cardType) {
        switch (cardType) {
            case KNIGHT: case ROAD: case PLENTY: case MONOPOLY: case CHAPEL:
            case UNIVERSITY: case PALACE: case LIBRARY: case MARKET:
                this.cardType = cardType;
                break;
            default:
                this.cardType = Constants.INVALID;
                break;
        }
    }
    
    /* Getters */
    
    public int getCardType() {
        return cardType;
    }
    
    /* Inherits / overrides */
    
    @Override
    public String toString() {
        switch (cardType) {
            case KNIGHT: case ROAD: case PLENTY: case MONOPOLY: 
            case CHAPEL: case UNIVERSITY: case PALACE: 
            case LIBRARY: case MARKET: 
                return NAMES.get(cardType);
            default: return "Invalid development card";
        }
    }
    
    /* Static methods */
    
    public static int getDevCardType(String sDevCard) {
        String s = sDevCard.toLowerCase();
        s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
        int i = NAMES.indexOf(s);
        switch (i) {
            case KNIGHT: case ROAD: case PLENTY: case MONOPOLY: case CHAPEL:
            case UNIVERSITY: case PALACE: case LIBRARY: case MARKET:
                return i;
            default: return Constants.INVALID;
        }
    }
}
