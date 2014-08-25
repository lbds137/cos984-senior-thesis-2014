import java.util.ArrayList;
import java.util.Collections;

public class Game {

    /* Fields */

    private int numPlayers;
    private int radius;
    private int dim;
    private ArrayList<Player> players; // ordering = turn order
    private Board board;
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
        UserInput.init(board, resDeck, bDraw);
        firstMoves();
        for (Player p : players) { p.collectResources(resDeck); } // give starting resources
        gameLoop();
    }
    private void setUpPlayers() {
        players = new ArrayList<Player>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player(i));
        }
        // randomize order of play (no point to actually rolling dice)
        Collections.shuffle(players);
    }
    private void setUpBoard() {
        board = new Board();
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
            UserInput.doInitialTurn(players.get(i)); 
        }
        // prompt to place second settlements and roads in reverse order
        for (int i = players.size() - 1; i >= 0; i--) { 
            UserInput.doPrivacy();
            UserInput.doInitialTurn(players.get(i)); 
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
            UserInput.doTurn(pCurrent);
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
        while (targetIndex == robberIndex) { targetIndex = UserInput.getHex(p); }
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
    
    /* Testing */
    
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
