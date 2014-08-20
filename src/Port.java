
public class Port {
    
    /* Constants */
    
    public static final int INLAND = 4; // not a port
    public static final int GENERIC = 3;
    public static final int SPECIFIC = 2;
    public static final String INLAND_NAME = "Inland";
    public static final String GENERIC_NAME = "Generic";

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
        if (portType == SPECIFIC) { this.portType = INLAND; }
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
        else { this.resource = null; }
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
    public String getStringRatio() {
        return getRatio() + ":1";
    }

    /* Inherits / overrides */
    
    //@Override
    public boolean equals(Port p) {
        if (p == null) { return false; }
        if (portType == p.getPortType()) {
            if (portType == SPECIFIC) { return resource.equals(p.getResource()); }
            else { return true; }
        }
        else { return false; }
    }
    // "greater" means that this port offers a better trade ratio for specified resource r
    public int compareRatio(Port p, Resource r) {
        if (this.equals(p)) { return 0; }
        int thisRatio = portType;
        int pRatio = p.getRatio();
        if (portType == SPECIFIC && !resource.equals(r)) { thisRatio = INLAND; }
        if (p.getRatio() == SPECIFIC && !p.getResource().equals(r)) { pRatio = INLAND; }
        return pRatio - thisRatio;
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
        return s + " (" + getStringRatio() + ")";
    }
}
