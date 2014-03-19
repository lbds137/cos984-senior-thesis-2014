
public class Intersection {
    
    /* Private fields */
    
    private int id;
    private int player;
    private int building;

    public Intersection(int id) {
        this.id = id;
        this.player = Constants.GAIA;
        this.building = Constants.OPEN;
    }

    /* Getters */
    
    public int getId() {
        return id;
    }
    public int getPlayer() {
        return player;
    }
    public int getBuilding() {
        return building;
    }
    
    /* Operations */
    
    public boolean build(int building, int player) {
        if ((this.player == Constants.GAIA || this.player == player) && this.building < building) {
            this.player = player;
            this.building = building;
            return true;
        }
        else {
            return false;
        }
    }
}
