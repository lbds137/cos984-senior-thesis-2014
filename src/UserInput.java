import java.util.ArrayList;

public final class UserInput {
    
    /* Constants */
    
    public static final String BEGINNING_INFO = ", you will be prompted to build a settlement and a road this turn. ";
    public static final String BUILD_REQUEST = ", would you like to build anything this turn? ";
    public static final String ROAD_REQUEST = ", please enter a start and end location for your road. ";
    public static final String SETTLEMENT_REQUEST = ", please enter a location for your settlement. ";
    public static final String CITY_REQUEST = ", please enter a location for your city. ";
    public static final String INVALID_RESPONSE = ", you entered invalid input. Type \'true\' to try again or \'false\' to abort. ";
    public static final String CANNOT_BUILD_ROAD = ", you are unable to build a road; aborting. ";
    public static final String CANNOT_BUILD_SETTLEMENT = ", you are unable to build a settlement; aborting. ";
    public static final String CANNOT_BUILD_CITY = ", you are unable to build a city; aborting. ";
    public static final String DONE_BUILDING_ROAD = ", are you done building roads?";
    public static final String DONE_BUILDING_SETTLEMENT = ", are you done building settlements? Type \'true\' for yes and \'false\' for no. ";
    public static final String DONE_BUILDING_CITY = ", are you done building cities?";
    
    public static void buildOneSettlement(Player p, ResourceBundle resDeck, Intersection[] intersections, Road[][] iGraph, BoardDraw bd) {
        // if player has exceeded maximum allowance or doesn't have enough resources, abort
        if (!p.canBuildSettlement()) {
            System.out.println(p.toString() + CANNOT_BUILD_SETTLEMENT);
            return;
        }
        int numIntersections = intersections.length;
        boolean success = true;
        boolean tryAgain = true;
        do {
            // display error message when appropriate
            if (!success) {
                while (tryAgain) {
                    // deal with bad input
                    try {
                        System.out.println(p.toString() + INVALID_RESPONSE);
                        tryAgain = StdIn.readBoolean();
                        break;
                    }
                    catch (Exception e) { StdIn.resync(); }
                }
                if (!tryAgain) { break; }
                success = true;
            }
            System.out.println(p.toString() + SETTLEMENT_REQUEST);
            int location;
            // deal with bad input
            try { location = StdIn.readInt(); }
            catch (Exception e) {
                StdIn.resync();
                success = false;
                continue;
            }
            // throw out locations outside the range of intersections[]
            if (location < 0 || location >= intersections.length) {
                success = false;
                continue;
            }
            // check that location provided isn't adjacent to other settlements or cities
            ArrayList<Intersection> neighbors = new ArrayList<Intersection>(HexShape.NUM_SIDES / 2);
            for (int i = 0; i < numIntersections; i++) {
                if (i == location) { continue; }
                if (iGraph[location][i] != null) { neighbors.add(intersections[i]); }
            }
            for (Intersection i : neighbors) { 
                if (i.getPlayer() != null) { success = false; } 
            }
            if (!success) { continue; }
            // try to build
            success = p.buildSettlement(intersections[location], resDeck);
        } while (!success);
        bd.draw();
    }
    
    public static void buildSettlements(Player p, ResourceBundle resDeck, Intersection[] intersections, Road[][] iGraph, BoardDraw bd) {
        boolean doneBuilding = false;
        do {
            buildOneSettlement(p, resDeck, intersections, iGraph, bd);
            while (!doneBuilding) {
                try {
                    System.out.println(p.toString() + DONE_BUILDING_SETTLEMENT);
                    doneBuilding = StdIn.readBoolean();
                    break;
                }
                catch (Exception e) { StdIn.resync(); }
            }
            if (doneBuilding) { break; }
        } while (p.canBuildSettlement());
    }
}
