import java.util.ArrayList;

public class Intersection {
    
    /* Fields */
    
    private int id;
    private Player player;
    private Building building;
    private Port port;
    private ArrayList<Hex> hexes;

    /* Constructors */
    
    public Intersection(int id, Port port, ArrayList<Hex> hexes) {
        this.id = id;
        this.player = null;
        this.building = new Building(Building.OPEN);
        this.port = port;
        this.hexes = hexes;
    }

    /* Getters */
    
    public int getId() {
        return id;
    }
    public Player getPlayer() {
        return player;
    }
    public Building getBuilding() {
        return building;
    }
    public Port getPort() {
        return port;
    }
    public ArrayList<Hex> getHexes() {
        return hexes;
    }
    
    /* Inherits / overrides */
    
    @Override
    public String toString() {
		String sPlayer = "Unowned";
		if (player != null) { sPlayer = player.toString(); }
        return id + " : " + sPlayer + ", " + building.toString() + ", " + port.toString();
    }
    
    /* Operations */
    
    public boolean upgrade(Player player) {
        if (this.player == null) { this.player = player; }
        return this.player == player && this.building.upgrade();
    }
}

