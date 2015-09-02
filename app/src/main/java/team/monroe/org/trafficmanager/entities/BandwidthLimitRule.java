package team.monroe.org.trafficmanager.entities;

import java.io.Serializable;

public class BandwidthLimitRule implements Serializable {

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
         String[] targetIps = target.getIpSet();
         return targetIps[0].equals(startIp) && targetIps[1].equals(endIp);
    }

    public boolean matchProfile(BandwidthProfile bandwidthProfile) {
        return (minInLimit == maxInLimit && maxInLimit == bandwidthProfile.inLimit) &&
               (minOutLimit == maxOutLimit && maxOutLimit == bandwidthProfile.outLimit)&&
               ((startPort == 1 && endPort == 65535) /*||(startPort == 0 && endPort == 0)*/);
    }

    public BandwidthProfile asProfile(String caption, String description) {
        return new BandwidthProfile(caption,description,maxOutLimit,maxInLimit);
    }
}
