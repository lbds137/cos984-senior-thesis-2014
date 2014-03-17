
public class Game {

    private Board board;
    private ArrayList<ArrayList<Integer>> resources;
    private ArrayList<Player> players;
    private int turnNumber;
    
    public Game() {
        
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