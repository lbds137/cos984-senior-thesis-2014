import java.util.ArrayList;
import java.util.Collections;

public class Game {

    /* Fields */

    private int numPlayers;
    private ArrayList<Player> players; // ordering = turn order
    private Board board;
    private ResourceBundle resDeck;
    private DevCardBundle devDeck;
    
    private Player longestRoadOwner;
    private int longestRoadLength;
    private Player largestArmyOwner;
    private int largestArmySize;
    
    public Game(String gameState) {
        resumeGame(gameState);
    }
    public Game(int numPlayers) throws Exception {
        if (numPlayers < 3 || numPlayers > 4) {
            throw new Exception();
        }
        this.numPlayers = numPlayers;
        
        startGame();
    }
    
    private void startGame() {
        setUpPlayers();
        setUpBoard();
        setUpDecks();
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
            int diceRoll = getDiceRoll();
            if (diceRoll == 7) { moveRobber(); }
            else {
                for (Player p : players) {
                    p.collectResources(diceRoll, resDeck);
                }
            }
            
            // todo: remainder of game turn logic
            
            pCurrent = getNextPlayer(pCurrent);
        }
    }
    
    private void setUpPlayers() {
        players = new ArrayList<Player>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player(i, 10));
        }
        
        // randomize order of play (no point to actually rolling dice)
        Collections.shuffle(players);
    }
    private void setUpBoard() {
        board = new Board();
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
        // todo: players choose initial two settlement and two road locations
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
    private void moveRobber() {
        // todo: player who rolled moves robber and steals from one of the players 
        // whose settlements/cities border the chosen hex
    }
    
    /* Testing */
    
    public static void main(String args[]) {
        try {
            Game g = new Game(3);
        }
        catch (Exception e) {
            System.out.println("Invalid number of players");
        }
    }
}
