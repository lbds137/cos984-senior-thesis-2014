
public class Hex {
    
    /* Fields */
    
    private int id;
    private Resource resource;
    private int diceRoll;
    private boolean hasRobber;
    
    /* Constructors */
    
    public Hex(int id, Resource resource, int diceRoll) {
        this.id = id;
        this.resource = resource;
        this.diceRoll = diceRoll;
        hasRobber = false;
    }
    
    /* Getters */
    
    public int getId() {
        return id;
    }
    public Resource getResource() {
        return resource;
    }
    public int getDiceRoll() {
        return diceRoll;
    }
    public boolean hasRobber() {
        return hasRobber;
    }
    
    /* Operations */
    
    public void placeRobber() {
        hasRobber = true;
    }
    public void removeRobber() {
        hasRobber = false;
    }
    
    /* Inherits / overrides */
    
    @Override
    public String toString() {
        return "Hex " + id + " " + resource.toString() + " " + diceRoll;
    }
    
    /* Testing */
    
    public static void main(String[] args) {
        Hex h = new Hex(1, new Resource(Resource.BRICK), 8);
        System.out.println(h.toString());
    }
}
