import java.io.FileReader;
import java.util.Scanner;
import java.util.ArrayList;

public final class FileParser {

    /* Read a file of integers, where each line is in the format {i0 : i1 i2 i3 ...}, returning an ArrayList of ArrayLists,
     * where the ArrayList at index i0 contains the ArrayList containing the integers to the right of the colon. 
     */
    public static ArrayList<ArrayList<Integer>> parseFile(String filename) {
        try {
            Scanner sc = new Scanner(new FileReader(filename));
            ArrayList<ArrayList<Integer>> a = new ArrayList<ArrayList<Integer>>(sc.nextInt());
                
            sc.nextLine();
            while (sc.hasNextLine()) {
                int i = sc.nextInt();
                ArrayList<Integer> al = new ArrayList<Integer>();
                String s = sc.findInLine("\\d+");
                while (s != null) {
                    al.add(Integer.parseInt(s));
                    s = sc.findInLine("\\d+");
                }
                a.add(al);
                if (sc.hasNextLine()) sc.nextLine();
            }
            sc.close();
            
            return a;
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
        
        return null;
    }
  
    private FileParser(){
        throw new AssertionError();
    }
}