package team.monroe.org.trafficmanager.entities;

import java.io.Serializable;

public class BandwidthLimitRule implements Serializable {

    public static final int PORT_MAX_VALUE = 65535;

    public final String id;
    public final String startIp;
    public final String endIp;
    public final int startPort;
    public final int endPort;
    public final boolean enabled;
    public final int maxInLimit;
    public final int minInLimit;
    public final int maxOutLimit;
    public final int minOutLimit;
    public final ProtocolClass protocol;

    public BandwidthLimitRule(String id, String startIp, String endIp, int startPort, int endPort, boolean enabled, int maxInLimit, int minInLimit, int maxOutLimit, int minOutLimit, ProtocolClass protocol) {
        this.id = id;
        this.startIp = startIp;
        this.endIp = endIp;
        this.startPort = startPort;
        this.endPort = endPort;
        this.enabled = enabled;
        this.maxInLimit = maxInLimit;
        this.minInLimit = minInLimit;
        this.maxOutLimit = maxOutLimit;
        this.minOutLimit = minOutLimit;
        this.protocol = protocol;
    }

    public boolean isForTarget(BandwidthLimit.Target target) {
         if (!isValid()) return false;
         String[] targetIps = target.getIpSet();
         return targetIps[0].equals(startIp) && targetIps[1].equals(endIp);
    }

    public boolean matchProfile(BandwidthProfile bandwidthProfile) {
        if (bandwidthProfile.isTrafficDisabled() && isPortDisabled()){
            return true;
        }
        return (minInLimit == maxInLimit && maxInLimit == bandwidthProfile.inLimit) &&
               (minOutLimit == maxOutLimit && maxOutLimit == bandwidthProfile.outLimit);
    }

    private boolean isPortDisabled() {
        return endPort == 0 && startPort == 0;
    }

    public BandwidthProfile asProfile(String caption, String description) {
        return new BandwidthProfile(caption,description,maxOutLimit,maxInLimit);
    }

    public boolean isValid() {
        return (isAllPorts() || isPortDisabled()) && protocol == ProtocolClass.ALL;
    }

    private boolean isAllPorts() {
        return startPort == 1 && endPort == PORT_MAX_VALUE;
    }
}
