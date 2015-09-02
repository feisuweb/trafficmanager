package team.monroe.org.trafficmanager.entities;

import java.io.Serializable;

public class BandwidthLimit implements Serializable {

    public final Target target;
    public final BandwidthProfile profile;
    public final BandwidthLimitRule source;

    public BandwidthLimit(Target target, BandwidthProfile profile, BandwidthLimitRule source) {
        this.target = target;
        this.profile = profile;
        this.source = source;
    }

    public static interface Target extends Serializable {
        public String[] getIpSet();
        public DeviceAlias getAlias();
    }

}
