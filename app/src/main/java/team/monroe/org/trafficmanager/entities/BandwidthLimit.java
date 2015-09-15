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
        public String getId();
        public String[] getIpSet();
        public DeviceAlias getAlias();
        public boolean isFavorite();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BandwidthLimit)) return false;

        BandwidthLimit limit = (BandwidthLimit) o;

        if (profile != null ? !profile.equals(limit.profile) : limit.profile != null) return false;
        if (source != null ? !source.equals(limit.source) : limit.source != null) return false;
        if (target != null ? !target.equals(limit.target) : limit.target != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = target != null ? target.hashCode() : 0;
        result = 31 * result + (profile != null ? profile.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        return result;
    }
}
