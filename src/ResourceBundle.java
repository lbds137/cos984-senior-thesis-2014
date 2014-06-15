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
    public boolean canRemove(int type) {
        if (type < 0 || type >= bundle.size() || bundle.get(type).size() == 0) return false;
        else return true;
    }
    public boolean canRemove(int[] resourceCounts) {
        if (resourceCounts.length != Resource.NUM_TYPES) return false; // lengths must match
        
        boolean canRemove = true;
        for (int i = 0; i < resourceCounts.length; i++) {
            if (resourceCounts[i] > size(i)) canRemove = false;
        }
        if (!canRemove) return false;
        else return true;
    }
	public Resource remove(int resourceType) {
		if (!canRemove(resourceType)) return null;
		return bundle.get(resourceType).remove(bundle.get(resourceType).size() - 1);
	}
	public ResourceBundle remove(int[] resourceCounts) {
        if (!canRemove(resourceCounts)) return null;
        
        ResourceBundle b = new ResourceBundle();
        for (int i = 0; i < resourceCounts.length; i++) {
            for (int j = 0; j < resourceCounts[i]; j++) {
                // don't need to check return value of add(), since we're removing from an existing bundle
                b.add(remove(i));
            }
        }
        return b;
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

