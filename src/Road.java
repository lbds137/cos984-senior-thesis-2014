
public class Road {

    /* Private fields */

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
        else return iTwo;
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
        return " [" + iOne + "->" + iTwo + "] ";
    }
	
	/* Testing */
	
	public static void main(String[] args) {
		
	}
}
