import java.util.ArrayList;

public class ResourceBundle {
	
	/* Fields */
	
	private ArrayList<ArrayList<Resource>> bundle;
	
	/* Constructors */
	
	// initialize empty bundle
	public ResourceBundle() {
		bundle = new ArrayList<ArrayList<Resource>>(Resource.NUM_TYPES);
		for (int i = 0; i < Resource.NUM_TYPES; i++) {
			bundle.add(new ArrayList<Resource>());
		}
	}
	
	/* Getters */
	
	public ArrayList<ArrayList<Resource>> getBundle() {
		return bundle;
	}
	
	/* Operations */
	
	public boolean add(Resource r) {
		if (bundle.get(r.getResourceType()).size() >= Resource.MAX_CARDS[r.getResourceType()]) return false;
		bundle.get(r.getResourceType()).add(r);
		return true;
	}
	public boolean add(ResourceBundle b) {
        // check that the maximum number of cards for each type isn't exceeded
        for (int i = 0; i < Resource.NUM_TYPES; i++) {
            if ((bundle.get(i).size() + b.size(i)) > Resource.MAX_CARDS[i]) return false;
        }
        // perform the actual addition
        for (int i = 0; i < Resource.NUM_TYPES; i++) {
            Resource r = b.remove(i);
            while (r != null) {
                add(r);
                r = b.remove(i);
            }
        }
        return true;
    }
	public Resource remove(int resourceType) {
		if (resourceType >= bundle.size() || bundle.get(resourceType).size() == 0) return null;
		return bundle.get(resourceType).remove(bundle.get(resourceType).size() - 1);
	}
	public int size(int resourceType) {
		int test = new Resource(resourceType).getResourceType();
		if (test != resourceType) return 0; // size is always 0 for invalid resource types
		return bundle.get(resourceType).size();
	}
	public int size() {
		int size = 0;
		for (int i = 0; i < bundle.size(); i++) {
			size += bundle.get(i).size();
		}
		return size;
	}
	public boolean isEmpty(int resourceType) {
		return size(resourceType) == 0;
	}
	public boolean isEmpty() {
		return size() == 0;
	}
}

