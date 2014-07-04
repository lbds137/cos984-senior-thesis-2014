import java.util.ArrayList;

public class DevCardBundle {
	
	/* Fields */

	private ArrayList<ArrayList<DevCard>> bundle;

	/* Constructors */

	// initialize empty bundle
	public DevCardBundle() {
		bundle = new ArrayList<ArrayList<DevCard>>(DevCard.NUM_TYPES);
		for (int i = 0; i < DevCard.NUM_TYPES; i++) {
			bundle.add(new ArrayList<DevCard>());
		}
	}
	
	/* Getters */
	
	public ArrayList<ArrayList<DevCard>> getBundle() {
		return bundle;
	}

	/* Operations */

	public boolean add(DevCard d) {
		if (bundle.get(d.getCardType()).size() >= DevCard.MAX_CARDS[d.getCardType()]) { return false; }
		bundle.get(d.getCardType()).add(d);
		return true;
	}
	public boolean add(DevCardBundle b) {
        // check that the maximum number of cards for each type isn't exceeded
        for (int i = 0; i < DevCard.NUM_TYPES; i++) {
            if ((bundle.get(i).size() + b.size(i)) > DevCard.MAX_CARDS[i]) { return false; }
        }
        // perform the actual addition
        for (int i = 0; i < DevCard.NUM_TYPES; i++) {
            DevCard d = b.remove(i);
            while (d != null) {
                add(d);
                d = b.remove(i);
            }
        }
        return true;
    }
	public DevCard remove(int devCardType) {
		if (devCardType >= bundle.size() || bundle.get(devCardType).size() == 0) { return null; }
		return bundle.get(devCardType).remove(bundle.get(devCardType).size() - 1);
	}
	public DevCard removeRandom() { // simulate drawing from a shuffled deck
        if (size() == 0) { return null; }
        
        int rand = (int) (Math.random() * size());
        int rowIndex = 0;
        int temp = 0;
        for ( ; temp < rand; rowIndex++) { temp += size(rowIndex); }
        if (temp > rand) { rowIndex--; } // whoops, we overshot
        while (size(rowIndex) == 0) { rowIndex++; }
        
        return remove(rowIndex);
    }
	public int size(int devCardType) {
		int test = new DevCard(devCardType).getCardType();
		if (test != devCardType) { return 0; } // size is always 0 for invalid dev card types
		return bundle.get(devCardType).size();
	}
	public int size() {
		int size = 0;
		for (int i = 0; i < bundle.size(); i++) { size += bundle.get(i).size(); }
		return size;
	}
	public boolean isEmpty(int devCardType) {
		return size(devCardType) == 0;
	}
	public boolean isEmpty() {
		return size() == 0;
	}
}

