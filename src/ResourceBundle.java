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
        bundle.get(r.getResourceType()).add(r);
        return true;
    }
    public boolean add(ResourceBundle b) {
        for (int i = 0; i < Resource.NUM_TYPES; i++) {
            Resource r = b.remove(i);
            while (r != null) {
                add(r);
                r = b.remove(i);
            }
        }
        return true;
    }
    public boolean canRemove(int type) { return !isEmpty(type); }
    public boolean canRemove(int[] resourceCounts) {
        if (resourceCounts.length != Resource.NUM_TYPES) { return false; } // lengths must match
        boolean canRemove = true;
        for (int i = 0; i < resourceCounts.length; i++) {
            if (resourceCounts[i] > size(i)) { canRemove = false; }
        }
        return canRemove;
    }
    public Resource remove(int resourceType) {
        if (!canRemove(resourceType)) return null;
        return bundle.get(resourceType).remove(bundle.get(resourceType).size() - 1);
    }
    public ResourceBundle remove(int[] resourceCounts) {
        if (!canRemove(resourceCounts)) return null;
        ResourceBundle b = new ResourceBundle();
        for (int i = 0; i < resourceCounts.length; i++) {
            for (int j = 0; j < resourceCounts[i]; j++) { b.add(remove(i)); }
        }
        return b;
    }
    // remove a random resource card
    public Resource removeRandom() {
        if (size() == 0) { return null; }
        int size = size();
        int rand = (int) (Math.random() * size);
        int i = 0;
        int temp = 0;
        for (; i < DevCard.NUM_TYPES; i++) {
            if (temp > rand) { break; }
            temp += size(i);
        }
        return remove(i - 1);
    }
    public int size(int resourceType) {
        int test = new Resource(resourceType).getResourceType();
        if (test != resourceType) return 0; // size is always 0 for invalid resource types
        return bundle.get(resourceType).size();
    }
    public int size() {
        int size = 0;
        for (int i = 0; i < bundle.size(); i++) { size += bundle.get(i).size(); }
        return size;
    }
    public boolean isEmpty(int type) { 
        return type < 0 || type >= bundle.size() || bundle.get(type).size() == 0;
    }
    public boolean isEmpty() { return size() == 0; }
    
    /* Inherits / overrides */
    
    @Override
    public String toString() {
        if (isEmpty()) { return "(empty)"; }
        String s = "";
        for (int i = 0; i < Resource.NUM_TYPES; i++) {
            if (size(i) == 0) { continue; }
            String res = "" + Resource.NAMES.get(i + 1);
            int count = size(i);
            s += res + ": x" + count + "; "; 
        }
        return s;
    }
}

