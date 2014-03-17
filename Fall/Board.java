import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Board {

    private Intersection[] intersections;
    private Road[][] roads;
    private boolean[][] hexGraph;
    private Hex[] hexes;
    
    public Board() {
        ArrayList<ArrayList<Integer>> aa = FileParser.parseFile(Constants.INTERSECTION_GRAPH_FILE);
        ArrayList<ArrayList<Integer>> bb = FileParser.parseFile(Constants.HEX_GRAPH_FILE);
        ArrayList<ArrayList<Integer>> cc = FileParser.parseFile(Constants.HEX_INTERSECTION_MAPPING_FILE);
        
        createIntersectionsAndRoads(aa);
        createHexGraph(bb);
        createHexes(bb, cc);
    }
    
    private void createIntersectionsAndRoads(ArrayList<ArrayList<Integer>> aa) {
        int n = aa.size();
        intersections = new Intersection[n];
        for (int i = 0; i < n; i++) {
            intersections[i] = new Intersection(i);
        }
        this.roads = new Road[n][n];
        
        for (int i = 0; i < n; i++) {
            int id = i;
            for (int j = 0; j < aa.get(i).size(); j++) {
                int destId = aa.get(i).get(j);
                if (roads[id][destId] == null) {
                    roads[id][destId] = new Road(intersections[id], intersections[destId]);
                    roads[destId][id] = roads[id][destId];
                }
            }
        }
    }
    private void createHexGraph(ArrayList<ArrayList<Integer>> aa) {
        int n = aa.size();
        hexGraph = new boolean[n][n];
        
        for (int i = 0; i < n; i++) {
            int id = i;
            for (int j = 0; j < aa.get(i).size(); j++) {
                int destId = aa.get(i).get(j);
                if (!hexGraph[id][destId]) {
                    hexGraph[id][destId] = true;
                    hexGraph[destId][id] = hexGraph[id][destId];
                }
            }
        }
    }
    private void createHexes(ArrayList<ArrayList<Integer>> aa, ArrayList<ArrayList<Integer>> bb) {
        ArrayList<Integer> shuffledLand = getShuffledLand();
        ArrayList<Integer> shuffledDice = getShuffledDice(aa, shuffledLand.indexOf(Constants.DESERT));
        int n = bb.size();
        hexes = new Hex[n];
        
        for (int i = 0; i < n; i++) {
            int id = i;
            int[] ids = new int[bb.get(i).size()];
            for (int j = 0; j < ids.length; j++) {
                ids[j] = bb.get(i).get(j);
            }
            hexes[id] = new Hex(id, this, ids, shuffledLand.get(id), shuffledDice.get(id));
        }
    }
    private ArrayList<Integer> getShuffledLand() {
        ArrayList<Integer> land = new ArrayList<Integer>(Arrays.asList(Constants.LAND));
        ArrayList<Integer> shuffledLand = new ArrayList<Integer>(Constants.LAND.length);
        while (land.size() != 0) {
            int iRand = (int) (Math.random() * land.size());
            shuffledLand.add(land.get(iRand));
            land.remove(iRand);
        }
        
        return shuffledLand;
    }
    // Dice roll chits 6 and 8 cannot be adjacent, so we have to work a bit harder
    private ArrayList<Integer> getShuffledDice(ArrayList<ArrayList<Integer>> aa, int desertIndex) {
        ArrayList<Integer> dice;
        ArrayList<Integer> shuffledDice;
        boolean conflict;
        int n = aa.size();

        do {
            conflict = false;
            dice = new ArrayList<Integer>(Arrays.asList(Constants.DICE));
            shuffledDice = new ArrayList<Integer>(Constants.DICE.length);
            
            while (dice.size() != 0) {
                int iRand = (int) (Math.random() * dice.size());
                shuffledDice.add(dice.get(iRand));
                dice.remove(iRand);
            }
            // Make sure die number 7 is on the desert tile
            shuffledDice.add(desertIndex, shuffledDice.remove(shuffledDice.indexOf(7)));
            
            // test generation for 6 and 8 adjacencies; regenerate if 6 and 8 end up adjacent
            for (int i = 0; i < n; i++) {
                if (conflict) break;
                for (int j = 0; j < aa.get(i).size(); j++) {
                    if (conflict) break;
                    if ((shuffledDice.get(i) == 6 || shuffledDice.get(i) == 8) && 
                        (shuffledDice.get(aa.get(i).get(j)) == 6 || shuffledDice.get(aa.get(i).get(j)) == 8)) {
                        conflict = true;
                    }
                }
            }
        } while (conflict);
        
        return shuffledDice;
    }
    
    public Intersection[] getIntersections() {
        return intersections;
    }
    public Road[][] getRoads() {
        return roads;
    }
    public Hex[] getHexes() {
        return hexes;
    }
    public void printRoads() {
        System.out.println(intersections.length + "\r");
        for (int i = 0; i < intersections.length; i++) {
            System.out.print(i + " :");
            for (int j = 0; j < intersections.length; j++) {
                if (roads[i][j] != null) {
                    System.out.print(" " + j);
                }
            }
            System.out.print("\r\n");
        }
    }
    public void printHexes() {
        System.out.println(hexes.length + "\r");
        for (int i = 0; i < hexes.length; i++) {
            //System.out.print(hexes[i]);
            System.out.print(i + " : " + hexes[i].getStringResourceType() + " " + hexes[i].getDieNumber() + "\n");
        }
    }
    public void printHexGraph() {
        System.out.println(hexGraph.length + "\r");
        for (int i = 0; i < hexGraph.length; i++) {
            System.out.print(i + " :");
            for (int j = 0; j < hexGraph.length; j++) {
                if (hexGraph[i][j]) {
                    System.out.print(" " + j);
                }
            }
            System.out.print("\r\n");
        }
    }
    
    public static void main(String args[]) {
        Board b = new Board();
        //b.printRoads();
        b.printHexes();
        //b.printHexGraph();
    }
}