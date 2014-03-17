
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
    
    private int build(int building, int player) {
        if (this.player == Constants.GAIA) {
            this.player = player;
            this.building = building;
            return 0;
        }
        else if (this.player == player) {
            if (this.building < building) {
                this.building = building;
                return 0;
            }
            else {
                return -1;
            }
        }
        else {
            return -1;
        }
    }
    
    public int buildSettlement(int player) {
        return build(Constants.SETTLEMENT, player);
    }
    public int buildCity(int player) {
        return build(Constants.CITY, player);
    }
    
}