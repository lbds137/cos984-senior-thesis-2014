
public class Road {

    /* Private fields */

    private int i1;
    private int i2;
    private int player;
    
    /* Constructors */
    
    public Road(int i1, int i2) {
        this.i1 = i1;
        this.i2 = i2;
        if (i1 > i2) {
            this.i1 = i2;
            this.i2 = i1;
        }
        this.player = Constants.GAIA;
    }
    
    /* Getters */
    
    public int other(int i) {
        if (i == i2) return i1;
        return i2;
    }
    
    /* Operations */
    
    public int build(int player) {
        if (this.player == Constants.GAIA) {
            this.player = player;
            return 0;
        }
        else {
            return -1;
        }
    }
    
    public String toString() {
        return i1 + "->" + i2;
    }
    
}