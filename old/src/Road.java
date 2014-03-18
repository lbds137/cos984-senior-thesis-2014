
public class Road {

    private Intersection v1;
    private Intersection v2;
    private int owner;
    
    public Road(Intersection v1, Intersection v2) {
        this.v1 = v1;
        this.v2 = v2;
        if (v1.get_id() > v2.get_id()) {
            this.v1 = v2;
            this.v2 = v1;
        }
        this.owner = Constants.GAIA;
    }
    
    public Intersection other(Intersection v) {
        if (v.get_id() == v2.get_id()) return v1;
        return v2;
    }
    
    public String toString() {
        return v1.get_id() + "->" + v2.get_id();
    }
    
}