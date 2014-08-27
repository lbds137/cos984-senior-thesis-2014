import java.util.ArrayList;
import java.util.Scanner;

public final class UserInput {
    
    /* Constants */
    
    public static final String COMMA = ", ";
    public static final String TRY_AGAIN = "Please try again. ";
    public static final String ABORTING = "Aborting. ";
    
    public static final String BUILD_ROAD = "build road";
    public static final String BUILD_SETTLEMENT = "build settlement";
    public static final String BUILD_CITY = "build city";
    public static final String BUILD_DEV_CARD = "build dev card";
    public static final String TRADE_PLAYER = "trade player";
    public static final String TRADE_PORT = "trade port";
    public static final String PLAY_DEV_CARD = "play dev card";
    public static final String PRINT_RESOURCE_CARDS = "print resource cards";
    public static final String END_TURN = "end turn";
    
    public static final String BEGINNING_INFO = COMMA + "you will be prompted to build a settlement and a road this turn. ";
    public static final String ROBBER_REQUEST = COMMA +"a 7 was rolled on your turn. Please enter a hex to which to move the robber. ";
    public static final String TURN_REQUEST = COMMA + "what would you like to do? Valid options are: " + BUILD_ROAD + COMMA + 
                                              BUILD_SETTLEMENT + COMMA + BUILD_CITY + COMMA + BUILD_DEV_CARD + COMMA + 
                                              TRADE_PLAYER + COMMA + TRADE_PORT + COMMA + PLAY_DEV_CARD + COMMA + 
                                              PRINT_RESOURCE_CARDS + COMMA + END_TURN;
    public static final String ROAD_REQUEST = COMMA + "please enter a start and end location for your road. ";
    public static final String SETTLEMENT_REQUEST = COMMA + "please enter a location for your settlement. ";
    public static final String CITY_REQUEST = COMMA + "please enter a location for your city. ";
    public static final String PORT_TRADE_REQUEST = COMMA + "please enter a resource for port trade, followed by the resource to trade for. ";
    public static final String CANNOT_BUILD_ROAD = COMMA + "you are unable to build a road. " + ABORTING;
    public static final String CANNOT_BUILD_SETTLEMENT = COMMA + "you are unable to build a settlement. " + ABORTING;
    public static final String CANNOT_BUILD_CITY = COMMA + "you are unable to build a city. " + ABORTING;
    public static final String CANNOT_BUILD_DEV_CARD = COMMA + "you are unable to build a dev card. " + ABORTING;
    
    public static final String VALID = "valid";
    public static final String INVALID_TURN_COMMAND = COMMA + "you entered an invalid command. " + TRY_AGAIN;
    public static final String INVALID_INTEGER = COMMA + "you did not enter a valid integer. " + TRY_AGAIN;
    public static final String INVALID_RESOURCE = COMMA + "you entered an invalid resource. " + TRY_AGAIN;
    public static final String INVALID_ROAD = COMMA + "you cannot build a road there. " + TRY_AGAIN;
    public static final String INVALID_SETTLEMENT = COMMA + "you cannot build a settlement there. " + TRY_AGAIN;
    public static final String INVALID_CITY = COMMA + "you cannot build a city there. " + TRY_AGAIN;
    public static final String INVALID_DEV_CARD = COMMA + "you entered an invalid dev card. " + TRY_AGAIN;
    public static final String INVALID_TRADE = COMMA + "you cannot afford that trade. " + TRY_AGAIN;
    
    /* Static fields */
    
    private static Scanner sc;

    public static void init() { sc = new Scanner(System.in); }
    // prompt player p for new input, where str is the prompt string
    public static String getNewInput(Player p, String str) {
        System.out.println(p.toString() + str);
        return sc.nextLine();
    }
    public static String validateTurnCommand(String sCommand) {
        switch (sCommand.toLowerCase()) {
            case BUILD_ROAD: case BUILD_SETTLEMENT:
            case BUILD_CITY: case BUILD_DEV_CARD:
            case TRADE_PLAYER: case TRADE_PORT:
            case PLAY_DEV_CARD: case PRINT_RESOURCE_CARDS:
            case END_TURN:
                return VALID;
            default: return INVALID_TURN_COMMAND;
        }
    }
    public static String validateInteger(String sInteger) {
        try {
            int integer = Integer.parseInt(sInteger);
            return VALID;
        }
        catch (Exception e) { return INVALID_INTEGER; }
    }
    public static String validateResource(String sResource) {
        if (Resource.getResourceType(sResource) != Constants.INVALID) { return VALID; }
        else { return INVALID_RESOURCE; }
    }
    public static String validateDevCard(String sDevCard) {
        if (DevCard.getDevCardType(sDevCard) != Constants.INVALID) { return VALID; }
        else { return INVALID_DEV_CARD; }
    }
    // clear screen so other players can't peek
    public static void doPrivacy() {
        for (int i = 0; i < 100; i++) { System.out.println(); }
    }
}
