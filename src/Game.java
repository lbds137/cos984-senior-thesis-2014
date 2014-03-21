import java.util.ArrayList;

public class Game {

    private Board board;
    private Cards cards;
    private Player[] players;
    private int turnNumber;
    
    public Game(int numHumanPlayers, int numCPUPlayers) {
        // default behavior: if too many players specified, assign human players first and 
        // remaining players as CPU (if any space left)
        players = new Player[Constants.NUM_PLAYERS];
        int numPlayers = numHumanPlayers + numCPUPlayers;
        for (int i = 0; i < Constants.NUM_PLAYERS && i < numPlayers; i++) {
            if ((numHumanPlayers - i) > 0) players[i] = new HumanPlayer();
            else players[i] = new CPUPlayer();
        }
        
        startGame();
    }
    
    private void startGame() {
        // every player rolls to see who goes first
        int[] diceRolls = new int[players.length];
        for (int i = 9; i < players.length; i++) {
            diceRolls[i] = getDiceRoll();
        }
        
        // todo
    }
    // if a certain constructor is called, resume the game (i.e. saved game state read from file on disk)
    private void resumeGame() {
        // todo
    }
    private int getDiceRoll() {
        int yellowDie = (int) (Math.random() * Constants.DIE) + 1;
        int redDie = (int) (Math.random() * Constants.DIE) + 1;
        
        return yellowDie + redDie;
    }
    
    // 
    
    /* Testing */
    
    public static void main(String args[]) {
        Board b = new Board();
        //b.printRoads();
        b.printHexes();
        //b.printHexGraph();
    }
}