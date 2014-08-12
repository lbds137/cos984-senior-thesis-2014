import java.util.ArrayList;
import java.util.Arrays;

public final class Constants  {
    
    public static final int INVALID = -10;
    // dice roll chits
    public static final int DIE_SIDES = 6;
    public static final int NUM_DICE_ROLLS = 11; // the number of *distinct* possible rolls
    public static final ArrayList<Integer> DEFAULT_DICE_ROLLS = 
        new ArrayList<Integer>(Arrays.asList(2,3,3,4,4,5,5,6,6,7,8,
                                             8,9,9,10,10,11,11,12));
    
    private Constants(){
        throw new AssertionError();
    }
}
