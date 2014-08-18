import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Board {

    /* Fields */
    
    private Hex[] hexes;
    private ArrayList<Integer> portLocations;
    private Intersection[] intersections;
    private Road[][] roads;
    private int robberIndex; // location of the robber in hexes[]
    
    /* Constructors */
    
    public Board() {
        initHexes();
        initIntersections();
        initRoads();
    }
    
    private void initHexes() {
        int n = Rules.getNumHexes();
        ArrayList<Integer> shuffledLand = new ArrayList<Integer>(Rules.getHexTiles());
        Collections.shuffle(shuffledLand);
        ArrayList<Integer> desertIndices = new ArrayList<Integer>();
        ArrayList<Integer> partialLand = shuffledLand;
        while (partialLand.indexOf(Resource.DESERT) != -1) {
            int index = partialLand.lastIndexOf(Resource.DESERT); // starting from the end
            desertIndices.add(index);
            partialLand = new ArrayList<Integer>(partialLand.subList(0, index));
        }
        robberIndex = desertIndices.get(desertIndices.size() - 1); // robber starts in the first desert
        ArrayList<Integer> shuffledDiceRolls = getDiceRolls(desertIndices);
        
        hexes = new Hex[n];
        for (int i = 0; i < n; i++) {
            hexes[i] = new Hex(i, new Resource(shuffledLand.get(i)), shuffledDiceRolls.get(i));
        }
        hexes[robberIndex].placeRobber();
    }
    private void initIntersections() {
        int n = Rules.getNumIntersections();
        ArrayList<Port> ports = randomizePorts();
        
        intersections = new Intersection[n];
        for (int i = 0; i < n; i++) {
            ArrayList<Integer> hexList = Rules.getIHMapping().get(i);
            ArrayList<Hex> iHexes = new ArrayList<Hex>(hexList.size());
            for (Integer hexId : hexList) { iHexes.add(hexes[hexId]); }
            intersections[i] = new Intersection(i, ports.get(i), iHexes);
        }
    }
    private void initRoads() {
        boolean[][] g = Rules.getIGraph();
        int n = Rules.getNumIntersections();
        roads = new Road[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (g[i][j]) { 
                    roads[i][j] = new Road(i, j);
                    roads[j][i] = roads[i][j];
                }
            }
        }
    }
    private ArrayList<Integer> getDiceRolls(ArrayList<Integer> desertIndices) {
        boolean[][] g = Rules.getHGraph();
        int n = Rules.getNumHexes();
        int radius = Rules.getRadius();
        boolean conflict;
        ArrayList<Integer> shuffledDiceRolls;
        // the rule that 6 and 8 cannot neighbor each other only applies when radius = DEFAULT_RADIUS
        do {
            conflict = false;
            shuffledDiceRolls = randomizeDiceRolls(desertIndices);
            // test generation for 6 and 8 adjacencies; regenerate if 6 and 8 end up adjacent
            for (int i = 0; i < n; i++) {
                if (conflict) { break; }
                for (int j = i + 1; j < n; j++) {
                    if (conflict) { break; }
                    if (!g[i][j]) { continue; }
                    if ((shuffledDiceRolls.get(i) == 6 || shuffledDiceRolls.get(i) == 8) && 
                        (shuffledDiceRolls.get(j) == 6 || shuffledDiceRolls.get(j) == 8)) {
                        conflict = true;
                    }
                }
            }
        } while (radius == Rules.DEFAULT_RADIUS && conflict);
        return shuffledDiceRolls;
    }
    private ArrayList<Integer> randomizeDiceRolls(ArrayList<Integer> desertIndices) {
        ArrayList<Integer> diceRolls = new ArrayList<Integer>(Rules.getDiceRolls());
        Collections.shuffle(diceRolls);
        // remove sevens
        while (diceRolls.indexOf(7) != -1) {
            int sevenIndex = diceRolls.indexOf(7);
            diceRolls.remove(sevenIndex);
        }
        // re-add sevens at proper indices (where the deserts are)
        for (int i = desertIndices.size() - 1; i >= 0; i--) { 
            diceRolls.add(desertIndices.get(i), new Integer(7));
        }
        return diceRolls;
    }
    private ArrayList<Port> randomizePorts() {
        int radius = Rules.getRadius();
        ArrayList<ArrayList<Integer>> iRings = Rules.getIRings();
        int numIntersections = Rules.getNumIntersections();
        int numI = iRings.get(radius - 1).size(); // # intersections in last ring
        int numPortsI = (int) (numI * Rules.PORT_RATIO); // # intersections with ports
        numPortsI -= numPortsI % 2; // must be even
        int numPortsL = numPortsI / 2; // # logical ports
        // we want the number of specific ports to be about half of the ports, but also
        // it needs to be a multiple of Resource.NUM_TYPES
        int numSpecificL = (numPortsL / 2) + (Resource.NUM_TYPES - ((numPortsL / 2) % Resource.NUM_TYPES));
        int numGenericL = numPortsL - numSpecificL;
        /* Initialize logical ports */
        ArrayList<Port> portsL = new ArrayList<Port>(Rules.getPorts());
        Collections.shuffle(portsL);
        /* Randomize port locations */
        int numInlandI = numI - numPortsI; 
        int numAvailable = numInlandI; // how many inland ports are left to distribute
        Port inland = new Port(Port.INLAND); // use one inland port for all references
        ArrayList<Port> portsI = new ArrayList<Port>(numIntersections);
        // add inland port references to all intersections not in last ring
        for (int i = 0; i < numIntersections - numI; i++) { portsI.add(inland); }
        for (int i = 0; i < numPortsL; i++) {
            // separate real ports with one inland port
            portsI.add(inland);
            numAvailable--;
            portsI.add(portsL.get(i));
            portsI.add(portsL.get(i));
        }
        int portsISize = portsI.size(); // need to keep track of this because size will change
        // distribute remaining inland ports
        while (numAvailable > 0) {
            for (int i = numIntersections - numI; numAvailable > 0 && i < portsISize; i++) {
                double rand = Math.random();
                if (portsI.get(i).getPortType() == Port.INLAND && rand > 0.5) {
                    portsI.add(i, inland);
                    portsISize++;
                    i++;
                    numAvailable--;
                }
            }
        }
        // initialize portLocations field (used by BoardDraw class)
        portLocations = new ArrayList<Integer>(numPortsI);
        for (int i = numIntersections - numI; i < portsISize; i++) {
            if (portsI.get(i).getPortType() != Port.INLAND) { portLocations.add(i); }
        }
        return portsI;
    }
    
    /* Getters */
    
    public Hex[] getHexes() { return hexes; }
    public Intersection[] getIntersections() { return intersections; }
    public Road[][] getRoads() { return roads; }
    // get the locations of ONLY the maritime ports (i.e. GENERIC and SPECIFIC but not INLAND)
    public ArrayList<Integer> getPortLocations() { return portLocations; }
    
    /* Operations */
    
    // move the robber and pick a random player whose buildings border the chosen hex
    public Player moveRobber(int index) {
        ArrayList<ArrayList<Integer>> hIMapping = Rules.getHIMapping();
        hexes[robberIndex].removeRobber();
        robberIndex = index;
        hexes[robberIndex].placeRobber();
        // since we're not using a set, if a player has more settlements s/he is more likely to be picked
        ArrayList<Player> candidatePlayers = new ArrayList<Player>();
        for (int i = 0; i < hIMapping.get(index).size(); i++) {
            Intersection inter = intersections[hIMapping.get(index).get(i)];
            Player p = inter.getPlayer();
            if (p != null && p.getResourceCards().size() != 0) { candidatePlayers.add(p); }
        }
        if (candidatePlayers.size() == 0) { return null; }
        int randIndex = (int) (Math.random() * candidatePlayers.size());
        return candidatePlayers.get(randIndex);
    }
}
