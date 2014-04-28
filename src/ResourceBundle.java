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
	
	/* Operations */
	
	public void add(Resource r) {
		bundle.get(r.getResourceType()).add(r);
	}
	public Resource remove(int resourceType) {
		return bundle.get(resourceType).remove(bundle.get(resourceType).size() - 1);
	}
	public int size(int resourceType) {
		return bundle.get(resourceType).size();
	}
}

