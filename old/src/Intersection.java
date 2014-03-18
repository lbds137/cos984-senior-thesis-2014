
public class Intersection {
    
    private int id;
    private int owner; // does a player have a settlement / city here? Default Player is Gaia (i.e. nobody)
    private int building;

    public Intersection(int id) {
        this.id = id;
        this.owner = Constants.GAIA;
        this.building = Constants.OPEN;
    }

    public int get_id() {
        return id;
    }
    public int get_owner() {
        return owner;
    }
    public int get_building() {
        return building;
    }
    
}