import java.util.ArrayList;
import java.util.Collections;

public class Game {

    /* Constants */
    
    public static final int DEFAULT_VP = 10;

    /* Fields */

    private int numPlayers;
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
    public Game(int numPlayers) {
        if (numPlayers < 3) { this.numPlayers = 3; }
        else if (numPlayers > 4) { this.numPlayers = 4; }
        else { this.numPlayers = numPlayers; }
        startGame();
    }
    
    private void startGame() {
        setUpPlayers();
        setUpBoard();
        setUpDecks();
        UserInput.init(board.getIntersections(), board.getIGraph(), resDeck, bDraw);
        firstMoves();
        gameLoop();
    }
    // if a certain constructor is called, resume the game (i.e. saved game state read from file on disk)
    private void resumeGame(String state) {
        // todo
    }
    /* MAIN GAME LOOP - WHERE EVERYTHING HAPPENS */
    private void gameLoop() {
        Player pCurrent = players.get(0);
        while (true) {
            UserInput.doPrivacy();
            int diceRoll = getDiceRoll();
            System.out.println("Dice roll was " + diceRoll + ".");
            if (diceRoll == 7) { moveRobber(pCurrent); }
            else {
                for (Player p : players) {
                    p.collectResources(diceRoll, resDeck);
                }
            }
            System.out.println("Your hand is: " + pCurrent.getResourceCards());
            System.out.println("Your VP score is: " + pCurrent.getVP());
            UserInput.doTurn(pCurrent);
            // todo: remainder of game turn logic
            
            pCurrent = getNextPlayer(pCurrent);
        }
    }
    private void setUpPlayers() {
        players = new ArrayList<Player>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player(i, DEFAULT_VP));
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
            for (int j = 0; j < DevCard.MAX_CARDS[i]; j++) {
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
    private Player getNextPlayer(Player p) {
        int index = players.indexOf(p);
        int nextIndex;
        if (index == -1) { return null; }
        else if (index == (players.size() - 1)) { nextIndex = 0; }
        else { nextIndex = index + 1; }
        return players.get(nextIndex);
    }
    private int getDiceRoll() {
        int yellowDie = (int) (Math.random() * Constants.DIE_SIDES) + 1;
        int redDie = (int) (Math.random() * Constants.DIE_SIDES) + 1;
        return yellowDie + redDie;
    }
    private void moveRobber(Player p) {
        // todo: player who rolled moves robber and steals from one of the players 
        // whose settlements/cities border the chosen hex
    }
    
    /* Testing */
    
    public static void main(String args[]) {
        try {
            Game g = new Game(4);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
