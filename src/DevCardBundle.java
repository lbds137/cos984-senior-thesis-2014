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

	/* Operations */

	public void add(DevCard d) {
		bundle.get(d.getCardType()).add(d);
	}
	public DevCard remove(int devCardType) {
		return bundle.get(devCardType).remove(bundle.get(devCardType).size() - 1);
	}
	public int size(int devCardType) {
		return bundle.get(devCardType).size();
	}
}

