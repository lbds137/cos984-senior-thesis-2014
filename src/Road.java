
public class Road {

    /* Private fields */

    private int iOne;
    private int iTwo;
    private int player;
    
    /* Constructors */
    
    public Road(int iOne, int iTwo) {
        this.iOne = iOne;
        this.iTwo = iTwo;
        if (iOne > iTwo) {
            this.iOne = iTwo;
            this.iTwo = iOne;
        }
        this.player = Constants.GAIA;
    }
    
    /* Getters */
    
    public int getPlayer() {
        return player;
    }
    public int other(int i) {
        if (i == iTwo) return iOne;
        else return iTwo;
    }
    
    /* Operations */
    
    public boolean build(int player) {
        if (this.player == Constants.GAIA) {
            this.player = player;
            return true;
        }
        else {
            return false;
        }
    }
    
    /* Debug */
    
    public String toString() {
        return " [" + iOne + "->" + iTwo + "] ";
    }
}
