
public class Road {

	/* Constants */
	
	public static final int[] ROAD_COST = {0,0,1,1,0};
	
    /* Fields */

    private int iOne;
    private int iTwo;
    private Player player;
    
    /* Constructors */
    
    public Road(int iOne, int iTwo) {
        this.iOne = iOne;
        this.iTwo = iTwo;
        if (iOne > iTwo) {
            this.iOne = iTwo;
            this.iTwo = iOne;
        }
        this.player = null;
    }
    
    /* Getters */
    
    public Player getPlayer() {
        return player;
    }
    public int other(int i) {
        if (i == iTwo) return iOne;
        else if (i == iOne) return iTwo;
        else return Constants.INVALID;
    }
    public boolean isNeighbor(Road r) {
        if (r.other(iOne) == Constants.INVALID && 
            r.other(iTwo) == Constants.INVALID) return false;
        else return true;
    }
    
    /* Operations */
    
    public boolean build(Player player) {
        if (this.player == null) {
            this.player = player;
            return true;
        }
        else {
            return false;
        }
    }
    
    /* Inherits / overrides */
    
    @Override
    public String toString() {
        return " [" + iOne + "->" + iTwo + "] " + " owned by player " + player.toString();
    }
    
    /* Testing */
    
    public static void main(String[] args) {
        
    }
}
