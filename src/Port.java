import java.util.HashMap;

public class Port {
	
	/* Constants */
	
    public static final int INLAND = 4; // not a port
    public static final int GENERIC = 3;
	public static final int SPECIFIC = 2;
	public static final int NUM_SPECIFIC = 1 * Resource.NUM_TYPES;
	public static final int NUM_GENERIC = LOCATIONS.length - NUM_SPECIFIC;
	public static final String INLAND_NAME = "Inland";
	public static final String GENERIC_NAME = "Generic";
	// there are 9 ports, and each port appears at two intersections
	public static final int[][] LOCATIONS = {{0,1},{3,4},{7,17},
                                             {14,15},{26,37},{28,38},
                                             {45,46},{47,48},{50,51}};

	/* Fields */

	private int portType;
	private Resource resource;
	
	/* Constructors */

	// default port is an INLAND port (i.e. not a port at all)
	public Port() {
		portType = INLAND;
		resource = null;
	}
	// constructor to be used with GENERIC and INLAND port types only
	public Port(int portType) {
		this.portType = portType;
		this.resource = null;
		
		// port cannot be SPECIFIC without any resource type specified
		if (portType == SPECIFIC) {
			this.portType = INLAND;
		}
	}
	// given a resource type we can assume it's a SPECIFIC port
	public Port(Resource resource) {
		this.portType = SPECIFIC;
		this.resource = resource;
	}
	public Port(int portType, Resource resource) {
		this.portType = portType;
		this.resource = resource;
		
		if (portType == SPECIFIC) {
			// if no appropriate resource type given, make port INLAND
			if (resource == null || resource.getResourceType() == resource.DESERT) {
				this.portType = INLAND;
				this.resource = null;
			}
		}
		// if port is not SPECIFIC, resource is irrelevant (i.e. null)
		else this.resource = null;
	}
	
	/* Getters */
	
	public int getPortType() {
		return portType;
	}
	public Resource getResource() {
		return resource;
	}
	// e.g. if 4:1, return 4
	public int getRatio() {
		return portType; // port types map exactly to ratios, but this is not set in stone
	}

	/* Inherits / overrides */
	
	@Override
	public boolean equals(Port p) {
		if (p == null) return false;
		return resource.equals(p.getResource()) && portType == p.getPortType();
	}
	@Override
	public String toString() {
		String s;
		switch (portType) {
			case INLAND: 
				s = INLAND_NAME;
				break;
			case GENERIC:
				s = GENERIC_NAME;
				break;
			case SPECIFIC:
				s = resource.toString();
				break;
			default:
				return "Invalid";
		}
		return s + " (" + portType + ":1)";
	}
	
	/* Testing */

	public static void main(String[] args) {
		Port p = new Port(Port.SPECIFIC, new Resource(Resource.WOOL));
        Port q = new Port(Port.GENERIC);
		Port r = null;
        System.out.println(p.toString());
        System.out.println(q.toString());
		System.out.println(p.equals(q));
		System.out.println(q.equals(r));
	}
}

