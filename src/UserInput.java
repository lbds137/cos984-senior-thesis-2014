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
    
    private static Intersection[] intersections; 
    private static Road[][] iGraph;
    private static ResourceBundle resDeck;
    private static Scanner sc;
    private static BoardDraw bd;

    public static void init(Intersection[] intersections, Road[][] iGraph, 
                            ResourceBundle resDeck, BoardDraw bd) {
        UserInput.intersections = intersections;
        UserInput.iGraph = iGraph;
        UserInput.resDeck = resDeck;
        sc = new Scanner(System.in);
        UserInput.bd = bd;
    }
    public static Road getRoad(Player p) {
        Road r = null;
        // if player has exceeded maximum allowance or doesn't have enough resources, abort
        if (!p.canBuildRoad()) {
            System.out.println(p.toString() + CANNOT_BUILD_ROAD);
            return r;
        }
        int numIntersections = iGraph.length;
        boolean valid = true;
        String result = ROAD_REQUEST;
        do {
            String input;
            String[] sInts;
            String sIntOne = "";
            String sIntTwo = "";
            String resultOne;
            String resultTwo;
            // check for valid integer input
            do {
                input = getNewInput(p, result);
                sInts = input.split("\\s");
                if (sInts.length < 2) {
                    result = INVALID_INTEGER;
                    continue;
                }
                sIntOne = sInts[0];
                sIntTwo = sInts[1];
                resultOne = validateInteger(sIntOne);
                resultTwo = validateInteger(sIntTwo);
                if (!resultOne.equals(VALID) || !resultTwo.equals(VALID)) {
                    if (resultOne.equals(VALID)) { result = resultTwo; }
                    else { result = resultOne; }
                }
                else { result = VALID; }
            } while (!result.equals(VALID));
            valid = true;
            int iOne = Integer.parseInt(sIntOne);
            int iTwo = Integer.parseInt(sIntTwo);
            // throw out locations outside the range of intersections[], and 
            // reject roads that aren't on the board (i.e. connecting wrong intersections)
            if (iOne < 0 || iTwo < 0 || iOne >= numIntersections || iTwo >= numIntersections || 
                iGraph[iOne][iTwo] == null) {
                valid = false;
                result = INVALID_ROAD;
                continue;
            }
            r = iGraph[iOne][iTwo];
            // verify if player p can build a road at the given location
            valid = p.canBuildRoad(r);
            if (!valid) { 
                result = INVALID_ROAD;
                r = null;
            }
        } while (!valid);
        return r;
    }
    public static Intersection getSettlement(Player p) {
        Intersection inter = null;
        // if player has exceeded maximum allowance or doesn't have enough resources, abort
        if (!p.canBuildSettlement()) {
            System.out.println(p.toString() + CANNOT_BUILD_SETTLEMENT);
            return inter;
        }
        int numIntersections = intersections.length;
        boolean valid = true;
        String result = SETTLEMENT_REQUEST;
        do {
            String input;
            // check for valid integer input
            do {
                input = getNewInput(p, result);
                result = validateInteger(input);
            } while (!result.equals(VALID));
            valid = true;
            int location = Integer.parseInt(input);
            // throw out locations outside the range of intersections[]
            if (location < 0 || location >= intersections.length) {
                valid = false;
                result = INVALID_SETTLEMENT;
                continue;
            }
            // check that location provided isn't adjacent to other settlements or cities
            ArrayList<Intersection> neighbors = new ArrayList<Intersection>(HexShape.NUM_SIDES / 2);
            for (int i = 0; i < numIntersections; i++) {
                if (i == location) { continue; }
                if (iGraph[location][i] != null) { neighbors.add(intersections[i]); }
            }
            for (Intersection i : neighbors) { 
                if (i.getPlayer() != null) { 
                    valid = false;
                    result = INVALID_SETTLEMENT;
                } 
            }
            if (!valid) { continue; }
            inter = intersections[location];
            // verify if player p can build a settlement at the given location
            valid = p.canBuildSettlement(inter);
            if (!valid) { 
                result = INVALID_SETTLEMENT;
                inter = null;
            }
        } while (!valid);
        return inter;
    }
    public static Intersection getCity(Player p) {
        Intersection inter = null;
        // if player has exceeded maximum allowance or doesn't have enough resources, abort
        if (!p.canBuildCity()) {
            System.out.println(p.toString() + CANNOT_BUILD_CITY);
            return inter;
        }
        int numIntersections = intersections.length;
        boolean valid = true;
        String result = CITY_REQUEST;
        do {
            String input;
            // check for valid integer input
            do {
                input = getNewInput(p, result);
                result = validateInteger(input);
            } while (!result.equals(VALID));
            valid = true;
            int location = Integer.parseInt(input);
            // throw out locations outside the range of intersections[]
            if (location < 0 || location >= intersections.length) {
                valid = false;
                result = INVALID_CITY;
                continue;
            }
            inter = intersections[location];
            // verify if player p can build a city at the given location
            valid = p.canBuildCity(inter);
            if (!valid) { 
                result = INVALID_CITY;
                inter = null;
            }
        } while (!valid);
        return inter;
    }
    public static void doPortTrade(Player p) {
        boolean success = true;
        String result = PORT_TRADE_REQUEST;
        do {
            String input;
            String[] sRes;
            String sResOne = "";
            String sResTwo = "";
            String resultOne;
            String resultTwo;
            // check for valid resource input
            do {
                input = getNewInput(p, result);
                sRes = input.split("\\s");
                if (sRes.length < 2) {
                    result = INVALID_RESOURCE;
                    continue;
                }
                sResOne = sRes[0];
                sResTwo = sRes[1];
                resultOne = validateResource(sResOne);
                resultTwo = validateResource(sResTwo);
                if (!resultOne.equals(VALID) || !resultTwo.equals(VALID)) {
                    if (resultOne.equals(VALID)) { result = resultTwo; }
                    else { result = resultOne; }
                }
                else { result = VALID; }
            } while (!result.equals(VALID));
            // verify if player p can do the trade
            success = p.doPortTrade(Resource.getResourceType(sResOne), 
                                    Resource.getResourceType(sResTwo), resDeck);
            if (!success) { result = INVALID_TRADE; }
        } while (!success);
    }
    public static void doTurn(Player p) {
        boolean done = false;
        do {
            System.out.println();
            p.printResourceCards();
            p.printVP();
            String input = getNewInput(p, TURN_REQUEST);
            String result = validateTurnCommand(input);
            while (!result.equals(VALID)) {
                input = getNewInput(p, result);
                result = validateTurnCommand(input);
            }
            switch (input.toLowerCase()) {
                case BUILD_ROAD:
                    Road r = getRoad(p);
                    if (r != null) { p.buildRoad(r, resDeck); }
                    break;
                case BUILD_SETTLEMENT:
                    Intersection i = getSettlement(p);
                    if (i != null) { p.buildSettlement(i, resDeck); }
                    break;
                case BUILD_CITY:
                    Intersection j = getCity(p);
                    if (j != null) { p.buildCity(j, resDeck); }
                    break;
                case TRADE_PORT:
                    doPortTrade(p);
                    break;
                case BUILD_DEV_CARD: case TRADE_PLAYER: case PLAY_DEV_CARD: 
                    System.out.println("The desired functionality is not yet implemented. Please try a different command.");
                    break;
                case PRINT_RESOURCE_CARDS:
                    p.printResourceCards();
                    break;
                case END_TURN:
                    done = true;
                    break;
                default:
                    //
            }
            bd.draw();
        } while (!done);
    }
    public static void doInitialTurn(Player p) {
        System.out.println();
        System.out.println(p.toString() + BEGINNING_INFO);
        Intersection i = getSettlement(p);
        p.buildSettlement(i, resDeck);
        bd.draw();
        Road r = getRoad(p);
        p.buildRoad(r, resDeck);
        bd.draw();
    }
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
        if (Resource.getResourceType(sResource) != Constants.INVALID) {
            return VALID;
        }
        else { return INVALID_RESOURCE; }
    }
    public static String validateDevCard(String sDevCard) {
        switch (sDevCard.toLowerCase()) {
            case DevCard.KNIGHT_NAME:
            case DevCard.ROAD_NAME:
            case DevCard.PLENTY_NAME:
            case DevCard.MONOPOLY_NAME:
            case DevCard.CHAPEL_NAME:
            case DevCard.UNIVERSITY_NAME:
            case DevCard.PALACE_NAME:
            case DevCard.LIBRARY_NAME:
            case DevCard.MARKET_NAME:
                return VALID;
            default: return INVALID_DEV_CARD;
        }
    }
    // clear screen so other players can't peek
    public static void doPrivacy() {
        for (int i = 0; i < 100; i++) { System.out.println(); }
    }
}
