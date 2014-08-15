import java.util.ArrayList;
import java.util.Collections;

public class Game {

    /* Fields */

    private int numPlayers;
    private int radius;
    private ArrayList<Player> players; // ordering = turn order
    private Board board;
    private BoardDraw bDraw;
    private ResourceBundle resDeck;
    private DevCardBundle devDeck;
    
    private Player longestRoadOwner;
    private int longestRoadLength;
    private Player largestArmyOwner;
    private int largestArmySize;
    
    public Game(String gameState) {
        resumeGame(gameState);
    }
    public Game(int numPlayers, int radius) {
        if (numPlayers < 3) { this.numPlayers = 3; }
        else if (numPlayers > 4) { this.numPlayers = 4; }
        else { this.numPlayers = numPlayers; }
        this.radius = radius;
        startGame();
    }
    
    private void startGame() {
        Rules.init(radius);
        setUpPlayers();
        setUpBoard();
        setUpDecks();
        UserInput.init(board, resDeck, bDraw);
        firstMoves();
        giveStartingResources();
        gameLoop();
    }
    // if a certain constructor is called, resume the game (i.e. saved game state read from file on disk)
    private void resumeGame(String state) {
        // todo
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
            // todo: remainder of game turn logic
            
            pCurrent = getNextPlayer(pCurrent);
        }
        Player winner = getWinner();
        System.out.println(winner + " has won the game with " + winner.getVP() + " victory points!");
        bDraw.save("final_board_state.png");
        System.exit(0);
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
        bDraw = new BoardDraw(board);
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
    private void giveStartingResources() {
        for (Player p : players) { p.collectStartingResources(resDeck); }
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
        Player victim = board.moveRobber(UserInput.getHex(p));
        if (victim != null) { victim.giveResource(p); }
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
        if (args.length < 2) { 
            System.out.println("Usage: \"java Game NUM_PLAYERS BOARD_SIZE\"");
        }
        int numPlayers;
        int radius;
        try {
            numPlayers = Integer.parseInt(args[0]);
            radius = Integer.parseInt(args[1]);
        }
        catch (Exception e) {
            System.out.println("One or more of the arguments entered was invalid. Using default values instead.");
            numPlayers = Rules.MIN_PLAYERS;
            radius = Rules.DEFAULT_RADIUS;
        }
        Game g = new Game(numPlayers, radius);
    }
}
