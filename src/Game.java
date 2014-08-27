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
            UserInput.doPrivacy();
            doInitialTurn(players.get(i)); 
        }
        // prompt to place second settlements and roads in reverse order
        for (int i = players.size() - 1; i >= 0; i--) { 
            UserInput.doPrivacy();
            doInitialTurn(players.get(i)); 
        }
    }
    /* MAIN GAME LOOP - WHERE EVERYTHING HAPPENS */
    private void gameLoop() {
        Player pCurrent = players.get(0);
        while (getWinner() == null) {
            UserInput.doPrivacy();
            int diceRoll = getDiceRoll();
            System.out.println("Dice roll was " + diceRoll + ".");
            if (diceRoll == 7) { 
                moveRobber(pCurrent);
                // because trading among players is not yet implemented, players need to
                // stockpile many cards and trade with the bank, so we do not enforce
                // the usual "discard half of your hand if you have more than 7 cards" rule
                //for (Player p : players) { p.discard(resDeck); }
            }
            else {
                for (Player p : players) { p.collectResources(diceRoll, resDeck); }
            }
            doTurn(pCurrent);
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
    private void moveRobber(Player p) {
        int robberIndex = board.getRobberIndex();
        int targetIndex = robberIndex;
        while (targetIndex == robberIndex) { targetIndex = getHex(p); }
        Player victim = board.moveRobber(targetIndex);
        if (victim != null) { victim.stealResource(p); }
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
            longestRoads.add(findLongestRoad(idsVisited, iGraph, Constants.INVALID)); // recursive helper
        }
        return longestRoads.last();
    }
    // recursive helper method for findLongestRoad()
    private int findLongestRoad(ArrayList<Integer> idsVisited, boolean[][] iGraph, int prevId) {
        int iCurrent = idsVisited.get(idsVisited.size() - 1);
        ArrayList<Integer> nextIds = new ArrayList<Integer>((HexShape.NUM_SIDES / 2) - 1);
        for (int k = 0; k < iGraph[iCurrent].length; k++) {
            if (k == iCurrent) continue;
            if (iGraph[iCurrent][k] && k != prevId) nextIds.add(k);
        }
        /* base case: dead-ended (not into a loop) */
        if (nextIds.size() == 0) { return idsVisited.size() - 1; }
        TreeSet<Integer> longestRoads = new TreeSet<Integer>();
        for (int j = 0; j < nextIds.size(); j++) {
            ArrayList<Integer> newIdsVisited = new ArrayList<Integer>(idsVisited);
            // continue only to intersections not visited yet
            if (idsVisited.indexOf(nextIds.get(j)) == -1) {
                newIdsVisited.add(nextIds.get(j));
                longestRoads.add(findLongestRoad(newIdsVisited, iGraph, iCurrent));
            }
        }
        /* base case: dead-ended (into a loop) */
        if (longestRoads.size() == 0) { return idsVisited.size(); }
        /* recursive case: return the longest road (out of 1 or more candidates, 
                           depending on whether or not there is a fork) */
        return longestRoads.last();
    }
    
    /* User interaction */
    
    public int getHex(Player p) {
        boolean valid = true;
        String result = UserInput.ROBBER_REQUEST;
        int location;
        do {
            String input;
            // check for valid integer input
            do {
                input = UserInput.getNewInput(p, result);
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
    public Road getRoad(Player p) {
        Road r = null;
        // if player has exceeded maximum allowance or doesn't have enough resources, abort
        if (!p.canBuildRoad()) {
            System.out.println(p.toString() + UserInput.CANNOT_BUILD_ROAD);
            return r;
        }
        int numIntersections = roads.length;
        boolean valid = true;
        String result = UserInput.ROAD_REQUEST;
        do {
            String input;
            String[] sInts;
            String sIntOne = "";
            String sIntTwo = "";
            String resultOne;
            String resultTwo;
            // check for valid integer input
            do {
                input = UserInput.getNewInput(p, result);
                sInts = input.split("\\s");
                if (sInts.length < 2) {
                    result = UserInput.INVALID_INTEGER;
                    continue;
                }
                sIntOne = sInts[0];
                sIntTwo = sInts[1];
                resultOne = UserInput.validateInteger(sIntOne);
                resultTwo = UserInput.validateInteger(sIntTwo);
                if (!resultOne.equals(UserInput.VALID) || !resultTwo.equals(UserInput.VALID)) {
                    if (resultOne.equals(UserInput.VALID)) { result = resultTwo; }
                    else { result = resultOne; }
                }
                else { result = UserInput.VALID; }
            } while (!result.equals(UserInput.VALID));
            valid = true;
            int iOne = Integer.parseInt(sIntOne);
            int iTwo = Integer.parseInt(sIntTwo);
            // throw out locations outside the range of intersections[], and 
            // reject roads that aren't on the board (i.e. connecting wrong intersections)
            if (iOne < 0 || iTwo < 0 || iOne >= numIntersections || iTwo >= numIntersections || 
                roads[iOne][iTwo] == null) {
                valid = false;
                result = UserInput.INVALID_ROAD;
                continue;
            }
            r = roads[iOne][iTwo];
            // verify if player p can build a road at the given location
            valid = p.canBuildRoad(r);
            if (!valid) { 
                result = UserInput.INVALID_ROAD;
                r = null;
            }
        } while (!valid);
        return r;
    }
    public Intersection getSettlement(Player p) {
        Intersection inter = null;
        // if player has exceeded maximum allowance or doesn't have enough resources, abort
        if (!p.canBuildSettlement()) {
            System.out.println(p.toString() + UserInput.CANNOT_BUILD_SETTLEMENT);
            return inter;
        }
        int numIntersections = intersections.length;
        boolean valid = true;
        String result = UserInput.SETTLEMENT_REQUEST;
        do {
            String input;
            // check for valid integer input
            do {
                input = UserInput.getNewInput(p, result);
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
            // verify if player p can build a settlement at the given location
            valid = p.canBuildSettlement(inter);
            if (!valid) { 
                result = UserInput.INVALID_SETTLEMENT;
                inter = null;
            }
        } while (!valid);
        return inter;
    }
    public Intersection getCity(Player p) {
        Intersection inter = null;
        // if player has exceeded maximum allowance or doesn't have enough resources, abort
        if (!p.canBuildCity()) {
            System.out.println(p.toString() + UserInput.CANNOT_BUILD_CITY);
            return inter;
        }
        int numIntersections = intersections.length;
        boolean valid = true;
        String result = UserInput.CITY_REQUEST;
        do {
            String input;
            // check for valid integer input
            do {
                input = UserInput.getNewInput(p, result);
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
            // verify if player p can build a city at the given location
            valid = p.canBuildCity(inter);
            if (!valid) { 
                result = UserInput.INVALID_CITY;
                inter = null;
            }
        } while (!valid);
        return inter;
    }
    public void doPortTrade(Player p) {
        boolean success = true;
        String result = UserInput.PORT_TRADE_REQUEST;
        do {
            String input;
            String[] sRes;
            String sResOne = "";
            String sResTwo = "";
            String resultOne;
            String resultTwo;
            // check for valid resource input
            do {
                input = UserInput.getNewInput(p, result);
                sRes = input.split("\\s");
                if (sRes.length < 2) {
                    result = UserInput.INVALID_RESOURCE;
                    continue;
                }
                sResOne = sRes[0];
                sResTwo = sRes[1];
                resultOne = UserInput.validateResource(sResOne);
                resultTwo = UserInput.validateResource(sResTwo);
                if (!resultOne.equals(UserInput.VALID) || !resultTwo.equals(UserInput.VALID)) {
                    if (resultOne.equals(UserInput.VALID)) { result = resultTwo; }
                    else { result = resultOne; }
                }
                else { result = UserInput.VALID; }
            } while (!result.equals(UserInput.VALID));
            // verify if player p can do the trade
            success = p.doPortTrade(Resource.getResourceType(sResOne), 
                                    Resource.getResourceType(sResTwo), resDeck);
            if (!success) { result = UserInput.INVALID_TRADE; }
        } while (!success);
    }
    public void doTurn(Player p) {
        boolean done = false;
        do {
            System.out.println();
            p.printResourceCards();
            p.printVP();
            String input = UserInput.getNewInput(p, UserInput.TURN_REQUEST);
            String result = UserInput.validateTurnCommand(input);
            while (!result.equals(UserInput.VALID)) {
                input = UserInput.getNewInput(p, result);
                result = UserInput.validateTurnCommand(input);
            }
            switch (input.toLowerCase()) {
                case UserInput.BUILD_ROAD:
                    Road r = getRoad(p);
                    if (r != null) { p.buildRoad(r, resDeck); }
                    break;
                case UserInput.BUILD_SETTLEMENT:
                    Intersection i = getSettlement(p);
                    if (i != null) { p.buildSettlement(i, resDeck); }
                    break;
                case UserInput.BUILD_CITY:
                    Intersection j = getCity(p);
                    if (j != null) { p.buildCity(j, resDeck); }
                    break;
                case UserInput.TRADE_PORT:
                    doPortTrade(p);
                    break;
                case UserInput.BUILD_DEV_CARD: case UserInput.TRADE_PLAYER: case UserInput.PLAY_DEV_CARD: 
                    System.out.println("The desired functionality is not yet implemented. Please try a different command.");
                    break;
                case UserInput.PRINT_RESOURCE_CARDS:
                    p.printResourceCards();
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
    public void doInitialTurn(Player p) {
        System.out.println();
        System.out.println(p.toString() + UserInput.BEGINNING_INFO);
        Intersection i = getSettlement(p);
        p.buildSettlement(i, resDeck);
        bDraw.draw();
        Road r = getRoad(p);
        p.buildRoad(r, resDeck);
        bDraw.draw();
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
