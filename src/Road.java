import java.util.ArrayList;

public class Road {

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
    // return the intersection ID shared by both roads
    public int common(Road r) {
        if (r.other(iOne) != Constants.INVALID) return iOne;
        else if (r.other(iTwo) != Constants.INVALID) return iTwo;
        else return Constants.INVALID;
    }
    public ArrayList<Integer> both() {
        ArrayList<Integer> both = new ArrayList<Integer>(2);
        both.add(iOne);
        both.add(iTwo);
        return both;
    }
    public boolean isNeighbor(Road r) {
        if (common(r) == Constants.INVALID) return false;
        else return true;
    }
    public boolean canBuild() {
        if (this.player == null) { return true; }
        else { return false; }
    }
    
    /* Operations */
    
    public boolean build(Player player) {
        if (canBuild()) {
            this.player = player;
            return true;
        }
        else { return false; }
    }
    
    /* Inherits / overrides */
    
    @Override
    public String toString() {
        String pString;
        if (player == null) pString = "nobody";
        else pString = player.toString();
        return " [" + iOne + "->" + iTwo + "] " + " owned by player " + pString;
    }
}
