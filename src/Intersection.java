
public class Intersection {
    
    /* Private fields */
    
    private int id;
    private int player;
    private int building;
    private int port;

    public Intersection(int id, int port) {
        this.id = id;
        this.player = Constants.GAIA;
        this.building = Constants.OPEN;
        this.port = port;
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
    public int getPort() {
        return port;
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
