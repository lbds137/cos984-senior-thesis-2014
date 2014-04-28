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
    public static final String KNIGHT_NAME = "Knight";
    public static final String ROAD_NAME = "Road Building";
    public static final String PLENTY_NAME = "Year Of Plenty";
    public static final String MONOPOLY_NAME = "Monopoly";
    public static final String CHAPEL_NAME = "Chapel";
    public static final String UNIVERSITY_NAME = "University";
    public static final String PALACE_NAME = "Palace";
    public static final String LIBRARY_NAME = "Library";
    public static final String MARKET_NAME = "Market";
    // max number of each type
	public static final int[] MAX_CARDS = {14,2,2,2,1,1,1,1,1};
    
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
            case KNIGHT: return KNIGHT_NAME;
            case ROAD: return ROAD_NAME;
            case PLENTY: return PLENTY_NAME;
            case MONOPOLY: return MONOPOLY_NAME;
            case CHAPEL: return CHAPEL_NAME;
            case UNIVERSITY: return UNIVERSITY_NAME;
            case PALACE: return PALACE_NAME;
            case LIBRARY: return LIBRARY_NAME;
            case MARKET: return MARKET_NAME;
            default: return "Invalid development card";
        }
    }
}

