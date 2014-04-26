import java.util.ArrayList;
import java.util.Collections;

public class Game {

    private Board board;
    private Decks cards;
    private Player[] players;
    private int[] turnOrder;
    private int turnNumber;
    private Player longestRoadOwner;
    private int longestRoadLength;
    private Player largestArmyOwner;
    private int largestArmySize;
    
    public Game(int numHumanPlayers, int numCPUPlayers) {
        // default behavior: if too many players specified, assign human players first and 
        // remaining players as CPU (if any space left)
        /*players = new Player[Player.NUM_PLAYERS];
        int numPlayers = numHumanPlayers + numCPUPlayers;
        for (int i = 0; i < Player.NUM_PLAYERS && i < numPlayers; i++) {
            if ((numHumanPlayers - i) > 0) players[i] = new HumanPlayer();
            else players[i] = new CPUPlayer();
        }*/
        
        startGame();
    }
    
    private void startGame() {
        initTurnOrder();
        firstMoves();
        gameLoop();
    }
    // if a certain constructor is called, resume the game (i.e. saved game state read from file on disk)
    private void resumeGame(String state) {
        // todo
    }
    private void firstMoves() {
        // todo
    }
    /* MAIN GAME LOOP - WHERE EVERYTHING HAPPENS */
    private void gameLoop() {
        while (true) {
            // todo
        }
    }
    // currently, ties are broken by the order in which dice were rolled (first goes first, etc.)
    private void initTurnOrder() {
        ArrayList<Integer> diceRolls = new ArrayList<Integer>(Player.NUM_PLAYERS);
        turnOrder = new int[players.length];
        
        for (int i = 0; i < players.length; i++) {
            diceRolls.add(getDiceRoll());
        }
        for (int i = 0; i < players.length; i++) {
            int highestIndex = diceRolls.indexOf(Collections.max(diceRolls));
            turnOrder[i] = highestIndex;
            diceRolls.set(highestIndex, 0);
        }
    }
    private int getDiceRoll() {
        int yellowDie = (int) (Math.random() * Constants.DIE) + 1;
        int redDie = (int) (Math.random() * Constants.DIE) + 1;
        
        return yellowDie + redDie;
    }
    
    /* Testing */
    
    public static void main(String args[]) {
        Game g = new Game(2,2);
    }
}
