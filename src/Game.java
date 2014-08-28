import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class Game {

    /* Fields */

    private int numPlayers;
    private int radius;
    private int dim;
    private ArrayList<Player> players; // ordering = turn order
    private Board board;
    private Hex[] hexes;
    private Intersection[] intersections;
    private Road[][] roads;
    private BoardDraw bDraw;
    private ResourceBundle resDeck;
    private DevCardBundle devDeck;
    
    private Player longestRoadOwner;
    private int longestRoadLength;
    private Player largestArmyOwner;
    private int largestArmySize;
    private Player pCurrent;
    
    public Game(int numPlayers, int radius, int dim) {
        if (numPlayers < 2) { this.numPlayers = 2; }
        else { this.numPlayers = numPlayers; }
        if (radius < 3) { this.radius = 3; }
        else { this.radius = radius; }
        if (dim < BoardDraw.MIN_DIM) { this.dim = BoardDraw.MIN_DIM; }
        else { this.dim = dim; }
        startGame();
    }
    
    private void startGame() {
        Rules.init(radius);
        setUpPlayers();
        setUpBoard();
        setUpDecks();
        UserInput.init();
        firstMoves();
        for (Player p : players) { p.collectResources(resDeck); } // give starting resources
        gameLoop();
    }
    private void setUpPlayers() {
        players = new ArrayList<Player>(numPlayers);
        for (int i = 0; i < numPlayers; i++) { players.add(new Player(i)); }
        Collections.shuffle(players); // randomize order of play (no point to actually rolling dice)
    }
    private void setUpBoard() {
        board = new Board();
        hexes = board.getHexes();
        intersections = board.getIntersections();
        roads = board.getRoads();
        bDraw = new BoardDraw(board, dim);
        bDraw.draw();
    }
    private void setUpDecks() {
        resDeck = new ResourceBundle();
        for (int i = 0; i < Resource.NUM_TYPES; i++) {
            for (int j = 0; j < board.getHexes().length; j++) {
                resDeck.add(new Resource(i));
            }
        }
        devDeck = new DevCardBundle();
        for (int i = 0; i < DevCard.NUM_TYPES; i++) {
            for (int j = 0; j < Rules.MAX_DEV_CARDS[i]; j++) {
                devDeck.add(new DevCard(i));
            }
        }
    }
    private void firstMoves() {
        // prompt to place first settlements and roads in normal order
        for (int i = 0; i < players.size(); i++) { 
            pCurrent = players.get(i);
            UserInput.doPrivacy();
            System.out.println(pCurrent + UserInput.WELCOME);
            doInitialTurn(); 
        }
        // prompt to place second settlements and roads in reverse order
        for (int i = players.size() - 1; i >= 0; i--) { 
            pCurrent = players.get(i);
            UserInput.doPrivacy();
            doInitialTurn(); 
        }
    }
    /* MAIN GAME LOOP - WHERE EVERYTHING HAPPENS */
    private void gameLoop() {
        pCurrent = players.get(0);
        while (getWinner() == null) {
            UserInput.doPrivacy();
            int diceRoll = getDiceRoll();
            System.out.println("Dice roll was " + diceRoll + ".");
            if (diceRoll == 7) { 
                moveRobber();
                // because trading among players is not yet implemented, players need to
                // stockpile many cards and trade with the bank, so we do not enforce
                // the usual "discard half of your hand if you have more than 7 cards" rule
                //for (Player p : players) { p.discard(resDeck); }
            }
            else {
                for (Player p : players) { p.collectResources(diceRoll, resDeck); }
            }
            doTurn();
            pCurrent = getNextPlayer(pCurrent);
        }
        Player winner = getWinner();
        System.out.println(winner + " has won the game with " + winner.getVP() + " victory points!");
        bDraw.save("final_board_state.png");
        System.exit(0);
    }
    private Player getNextPlayer(Player p) {
        int index = players.indexOf(p);
        int nextIndex;
        if (index == -1) { return null; }
        else if (index == (players.size() - 1)) { nextIndex = 0; }
        else { nextIndex = index + 1; }
        return players.get(nextIndex);
    }
    private int getDiceRoll() {
        int yellowDie = (int) (Math.random() * Rules.DIE_SIDES) + 1;
        int redDie = (int) (Math.random() * Rules.DIE_SIDES) + 1;
        return yellowDie + redDie;
    }
    private void moveRobber() {
        int robberIndex = board.getRobberIndex();
        int targetIndex = robberIndex;
        while (targetIndex == robberIndex) { targetIndex = getHex(); }
        Player victim = board.moveRobber(targetIndex);
        if (victim != null) { victim.stealResource(pCurrent); }
        bDraw.draw();
    }
    // returns null if no winner yet
    private Player getWinner() {
        Player winner = null;
        for (Player p : players) {
            if (p.getVP() >= Rules.getMaxVP()) { winner = p; }
        }
        return winner;
    }
    private void findLongestRoad() {
        // take away longest road until roads are counted again
        if (longestRoadOwner != null) { longestRoadOwner.takeLongestRoad(); }
        longestRoadLength = 0;
        longestRoadOwner = null;
        for (Player p : players) { 
            int longestRoad = findLongestRoad(p);
            if (longestRoad >= Rules.getMinLongestRoad() && 
                longestRoad > longestRoadLength) {
                longestRoadLength = longestRoad;
                longestRoadOwner = p;
            }
        }
        if (longestRoadOwner != null) { longestRoadOwner.giveLongestRoad(); }
    }
    // returns the length of this player's longest road
    private int findLongestRoad(Player p) {
        ArrayList<Road> roads = p.getRoads();
        TreeSet<Integer> iAll = new TreeSet<Integer>();
        // get all intersection IDs touched by this player's roads
        for (int i = 0; i < roads.size(); i++) {
            ArrayList<Integer> both = roads.get(i).both();
            iAll.add(both.get(0));
            iAll.add(both.get(1));
        }
        TreeSet<Integer> iCommon = new TreeSet<Integer>();
        // find vertices that are shared by two or more roads
        for (int i = 0; i < roads.size(); i++) {
            for (int j = i + 1; j < roads.size(); j++) {
                int common = roads.get(i).common(roads.get(j));
                if (common != Constants.INVALID) { iCommon.add(common); }
            }
        }
        // find termini by set minus operation
        TreeSet<Integer> iTermini = new TreeSet<Integer>(iAll);
        iTermini.removeAll(iCommon);
        int n = iAll.last() + 1;
        boolean[][] iGraph = new boolean[n][n];
        // create (partial) intersection graph
        for (int i = 0; i < roads.size(); i++) {
            ArrayList<Integer> both = roads.get(i).both();
            iGraph[both.get(0)][both.get(1)] = true;
            iGraph[both.get(1)][both.get(0)] = true;
        }
        TreeSet<Integer> longestRoads = new TreeSet<Integer>();
        for (Integer i : iTermini) {
            ArrayList<Integer> idsVisited = new ArrayList<Integer>();
            idsVisited.add(i);
            longestRoads.add(findLongestRoad(p, idsVisited, iGraph, Constants.INVALID)); // recursive helper
        }
        return longestRoads.last();
    }
    // recursive helper method for findLongestRoad()
    private int findLongestRoad(Player p, ArrayList<Integer> idsVisited, boolean[][] iGraph, int prevId) {
        int iCurrent = idsVisited.get(idsVisited.size() - 1);
        ArrayList<Integer> nextIds = new ArrayList<Integer>((HexShape.NUM_SIDES / 2) - 1);
        for (int k = 0; k < iGraph[iCurrent].length; k++) {
            if (k == iCurrent) continue;
            if (iGraph[iCurrent][k] && k != prevId) nextIds.add(k);
        }
        /* base case: road broken up by enemy player building */
        Player q = intersections[iCurrent].getPlayer();
        if (q != null && q != p) { return idsVisited.size() - 1; }
        /* base case: dead-ended (not into a loop) */
        if (nextIds.size() == 0) { return idsVisited.size() - 1; }
        TreeSet<Integer> longestRoads = new TreeSet<Integer>();
        for (int j = 0; j < nextIds.size(); j++) {
            ArrayList<Integer> newIdsVisited = new ArrayList<Integer>(idsVisited);
            // continue only to intersections not visited yet
            if (idsVisited.indexOf(nextIds.get(j)) == -1) {
                newIdsVisited.add(nextIds.get(j));
                longestRoads.add(findLongestRoad(p, newIdsVisited, iGraph, iCurrent));
            }
        }
        /* base case: dead-ended (into a loop) */
        if (longestRoads.size() == 0) { return idsVisited.size(); }
        /* recursive case: return the longest road (out of 1 or more candidates, 
                           depending on whether or not there is a fork) */
        return longestRoads.last();
    }
    private void playDevCard(int devCardType) {
        switch (devCardType) {
            case DevCard.KNIGHT:
                moveRobber();
                break;
            case DevCard.ROAD:
                if (pCurrent.getRoads().size() < (Rules.getMaxRoads() - Rules.DEV_CARD_FREE_ROADS)) {
                    System.out.println(pCurrent + UserInput.CANNOT_BUILD_ROAD);
                    return; // road limit would be exceeded
                }
                pCurrent.giveFreeRoads(Rules.DEV_CARD_FREE_ROADS);
                pCurrent.buildRoad(getRoad(), resDeck);
                pCurrent.buildRoad(getRoad(), resDeck);
                break;
            case DevCard.PLENTY:
                boolean success = true;
                String result = UserInput.PLENTY_REQUEST;
                int typeOne;
                int typeTwo;
                do {
                    String input;
                    // check for valid resource input
                    do {
                        input = UserInput.getNewInput(pCurrent, result);
                        result = UserInput.validateTwoResources(input);
                    } while (!result.equals(UserInput.VALID));
                    String[] sRes = input.split("\\s");
                    String sResOne = sRes[0];
                    String sResTwo = sRes[1];
                    // verify if requested cards can be drawn
                    typeOne = Resource.getResourceType(sResOne);
                    typeTwo = Resource.getResourceType(sResTwo);
                    success = resDeck.canRemove(typeOne) && resDeck.canRemove(typeTwo);
                    if (!success) { result = UserInput.CANNOT_DRAW_RESOURCE_CARD; }
                } while (!success);
                // actually draw the cards (you thief!)
                pCurrent.stealResource(resDeck.remove(typeOne));
                pCurrent.stealResource(resDeck.remove(typeTwo));
                break;
            case DevCard.MONOPOLY:
                result = UserInput.MONOPOLY_REQUEST;
                String input;
                // check for valid resource input
                do {
                    input = UserInput.getNewInput(pCurrent, result);
                    result = UserInput.validateResource(input);
                } while (!result.equals(UserInput.VALID));
                int resType = Resource.getResourceType(input);
                for (Player p : players) {
                    if (p == pCurrent) { continue; }
                    ResourceBundle pHand = p.getResourceCards();
                    while (pHand.size(resType) > 0) { pCurrent.stealResource(pHand.remove(resType)); }
                }
                break;
            case DevCard.CHAPEL: case DevCard.UNIVERSITY: case DevCard.PALACE: 
            case DevCard.LIBRARY: case DevCard.MARKET:
                // the Player class handles the VP given by these cards
                break;
        }
        pCurrent.playDevCard(devCardType);
    }
    
    /* User interaction */
    
    private int getHex() {
        boolean valid = true;
        String result = UserInput.ROBBER_REQUEST;
        int location;
        do {
            String input;
            // check for valid integer input
            do {
                input = UserInput.getNewInput(pCurrent, result);
                result = UserInput.validateInteger(input);
            } while (!result.equals(UserInput.VALID));
            valid = true;
            location = Integer.parseInt(input);
            // throw out locations outside the range of hexes[]
            if (location < 0 || location >= hexes.length) {
                valid = false;
                result = UserInput.INVALID_SETTLEMENT;
            }
        } while (!valid);
        return location;
    }
    private Road getRoad() {
        Road r = null;
        // if player has exceeded maximum allowance or doesn't have enough resources, abort
        if (!pCurrent.canBuildRoad()) {
            System.out.println(pCurrent.toString() + UserInput.CANNOT_BUILD_ROAD);
            return r;
        }
        int numIntersections = roads.length;
        boolean valid = true;
        String result = UserInput.ROAD_REQUEST;
        do {
            String input;
            // check for valid integer input
            do {
                input = UserInput.getNewInput(pCurrent, result);
                result = UserInput.validateTwoIntegers(input);
            } while (!result.equals(UserInput.VALID));
            String[] sInts = input.split("\\s");
            int iOne = Integer.parseInt(sInts[0]);
            int iTwo = Integer.parseInt(sInts[1]);
            valid = true;
            // throw out locations outside the range of intersections[], and 
            // reject roads that aren't on the board (i.e. connecting wrong intersections)
            if (iOne < 0 || iTwo < 0 || iOne >= numIntersections || iTwo >= numIntersections || 
                roads[iOne][iTwo] == null) {
                valid = false;
                result = UserInput.INVALID_ROAD;
                continue;
            }
            r = roads[iOne][iTwo];
            // verify if player can build a road at the given location
            valid = pCurrent.canBuildRoad(r);
            if (!valid) { 
                result = UserInput.INVALID_ROAD;
                r = null;
            }
        } while (!valid);
        return r;
    }
    private Intersection getSettlement() {
        Intersection inter = null;
        // if player has exceeded maximum allowance or doesn't have enough resources, abort
        if (!pCurrent.canBuildSettlement()) {
            System.out.println(pCurrent.toString() + UserInput.CANNOT_BUILD_SETTLEMENT);
            return inter;
        }
        int numIntersections = intersections.length;
        boolean valid = true;
        String result = UserInput.SETTLEMENT_REQUEST;
        do {
            String input;
            // check for valid integer input
            do {
                input = UserInput.getNewInput(pCurrent, result);
                result = UserInput.validateInteger(input);
            } while (!result.equals(UserInput.VALID));
            valid = true;
            int location = Integer.parseInt(input);
            // throw out locations outside the range of intersections[]
            if (location < 0 || location >= intersections.length) {
                valid = false;
                result = UserInput.INVALID_SETTLEMENT;
                continue;
            }
            // check that location provided isn't adjacent to other settlements or cities
            ArrayList<Intersection> neighbors = new ArrayList<Intersection>(HexShape.NUM_SIDES / 2);
            for (int i = 0; i < numIntersections; i++) {
                if (i == location) { continue; }
                if (roads[location][i] != null) { neighbors.add(intersections[i]); }
            }
            for (Intersection i : neighbors) { 
                if (i.getPlayer() != null) { 
                    valid = false;
                    result = UserInput.INVALID_SETTLEMENT;
                } 
            }
            if (!valid) { continue; }
            inter = intersections[location];
            // verify if player can build a settlement at the given location
            valid = pCurrent.canBuildSettlement(inter);
            if (!valid) { 
                result = UserInput.INVALID_SETTLEMENT;
                inter = null;
            }
        } while (!valid);
        return inter;
    }
    private Intersection getCity() {
        Intersection inter = null;
        // if player has exceeded maximum allowance or doesn't have enough resources, abort
        if (!pCurrent.canBuildCity()) {
            System.out.println(pCurrent.toString() + UserInput.CANNOT_BUILD_CITY);
            return inter;
        }
        int numIntersections = intersections.length;
        boolean valid = true;
        String result = UserInput.CITY_REQUEST;
        do {
            String input;
            // check for valid integer input
            do {
                input = UserInput.getNewInput(pCurrent, result);
                result = UserInput.validateInteger(input);
            } while (!result.equals(UserInput.VALID));
            valid = true;
            int location = Integer.parseInt(input);
            // throw out locations outside the range of intersections[]
            if (location < 0 || location >= intersections.length) {
                valid = false;
                result = UserInput.INVALID_CITY;
                continue;
            }
            inter = intersections[location];
            // verify if player can build a city at the given location
            valid = pCurrent.canBuildCity(inter);
            if (!valid) { 
                result = UserInput.INVALID_CITY;
                inter = null;
            }
        } while (!valid);
        return inter;
    }
    private int getDevCard() {
        int card = Constants.INVALID;
        // if player has no dev cards, abort
        if (pCurrent.getDevCards().size() == 0) {
            System.out.println(pCurrent.toString() + UserInput.CANNOT_PLAY_DEV_CARD);
            return card;
        }
        boolean valid = true;
        String result = UserInput.DEV_CARD_REQUEST;
        do {
            String input;
            // check for valid dev card input
            do {
                input = UserInput.getNewInput(pCurrent, result);
                result = UserInput.validateDevCard(input);
            } while (!result.equals(UserInput.VALID));
            valid = true;
            card = DevCard.getCardType(input);
            if (pCurrent.getDevCards().size(card) == 0) {
                valid = false;
                result = UserInput.INVALID_DEV_CARD;
                continue;
            }
        } while (!valid);
        return card;
    }
    private void doPortTrade() {
        boolean success = true;
        String result = UserInput.PORT_TRADE_REQUEST;
        do {
            String input;
            // check for valid resource input
            do {
                input = UserInput.getNewInput(pCurrent, result);
                result = UserInput.validateTwoResources(input);
            } while (!result.equals(UserInput.VALID));
            String[] sRes = input.split("\\s");
            String sResOne = sRes[0];
            String sResTwo = sRes[1];
            // verify if player can do the trade
            success = pCurrent.doPortTrade(Resource.getResourceType(sResOne), 
                                           Resource.getResourceType(sResTwo), resDeck);
            if (!success) { result = UserInput.INVALID_TRADE; }
        } while (!success);
    }
    private void doTurn() {
        boolean done = false;
        do {
            printTurnInfo();
            String input = UserInput.getNewInput(pCurrent, UserInput.TURN_REQUEST);
            String result = UserInput.validateTurnCommand(input);
            while (!result.equals(UserInput.VALID)) {
                input = UserInput.getNewInput(pCurrent, result);
                result = UserInput.validateTurnCommand(input);
            }
            switch (input.toLowerCase()) {
                case UserInput.BUILD_ROAD:
                    Road r = getRoad();
                    if (r != null) { 
                        pCurrent.buildRoad(r, resDeck);
                        findLongestRoad();
                    }
                    break;
                case UserInput.BUILD_SETTLEMENT:
                    Intersection i = getSettlement();
                    if (i != null) { 
                        pCurrent.buildSettlement(i, resDeck);
                        findLongestRoad(); // what if a newly build settlement breaks a road?
                    }
                    break;
                case UserInput.BUILD_CITY:
                    Intersection j = getCity();
                    if (j != null) { pCurrent.buildCity(j, resDeck); }
                    break;
                case UserInput.TRADE_PORT:
                    doPortTrade();
                    break;
                case UserInput.BUILD_DEV_CARD:
                    if (!pCurrent.canBuildDevCard()) {
                        System.out.println(pCurrent + UserInput.CANNOT_BUILD_DEV_CARD);
                    }
                    pCurrent.buildDevCard(devDeck, resDeck);
                    break;
                case UserInput.PLAY_DEV_CARD:
                    int card = getDevCard();
                    if (card != Constants.INVALID) { playDevCard(card); }
                    break;
                case UserInput.TRADE_PLAYER:  
                    System.out.println("The desired functionality is not yet implemented. Please try a different command.");
                    break;
                case UserInput.END_TURN:
                    done = true;
                    break;
                default:
                    //
            }
            bDraw.draw();
        } while (!done);
    }
    private void doInitialTurn() {
        System.out.println();
        System.out.println(pCurrent + UserInput.BEGINNING_INFO);
        Intersection i = getSettlement();
        pCurrent.buildSettlement(i, resDeck);
        bDraw.draw();
        Road r = getRoad();
        pCurrent.buildRoad(r, resDeck);
        bDraw.draw();
    }
    private void printTurnInfo() {
        /* note: dice roll printed in gameLoop() */
        System.out.println();
        System.out.println("----------------------------------------------------------------------");
        System.out.println();
        System.out.println("Resource decks: " + resDeck);
        System.out.println("Dev cards available: " + devDeck.size());
        System.out.println();
        System.out.println("Roads available: " + (Rules.getMaxRoads() - pCurrent.getRoads().size()));
        System.out.println("Settlements available: " + 
                           (Rules.getMaxSettlements() - pCurrent.getSettlements().size()));
        System.out.println("Cities available: " + (Rules.getMaxCities() - pCurrent.getCities().size()));
        System.out.println();
        System.out.print("Longest road owner: ");
        if (longestRoadOwner != null) {
            System.out.print(longestRoadOwner + "\n");
        }
        else { System.out.print("(nobody)\n"); }
        System.out.print("Largest army owner: ");
        if (largestArmyOwner != null) {
            System.out.print(largestArmyOwner + "\n");
        }
        else { System.out.print("(nobody)\n"); }
        System.out.println();
        System.out.println("Your VP score is: " + pCurrent.getVP() + " (out of " + Rules.getMaxVP() + ")");
        System.out.println("Your hand is: " + pCurrent.getResourceCards());
        System.out.println("Unplayed dev cards: " + pCurrent.getDevCards());
        System.out.println("Played dev cards: " + pCurrent.getPlayedDevCards());
        System.out.println();
    }
    
    /* PROGRAM ENTRY POINT */
    
    public static void main(String args[]) {
        // First argument is number of players, second argument number of rings for board size
        if (args.length < 3) { 
            System.out.println("Usage: \"java Game NUM_PLAYERS BOARD_RADIUS BOARD_DIMENSIONS\"");
        }
        int numPlayers;
        int radius;
        int dim;
        try {
            numPlayers = Integer.parseInt(args[0]);
            radius = Integer.parseInt(args[1]);
            dim = Integer.parseInt(args[2]);
        }
        catch (Exception e) {
            System.out.println("One or more arguments entered were invalid, or one or more arguments were missing. Using default values instead.");
            numPlayers = Rules.MIN_PLAYERS;
            radius = Rules.DEFAULT_RADIUS;
            dim = BoardDraw.DEFAULT_DIM;
        }
        Game g = new Game(numPlayers, radius, dim);
    }
}
