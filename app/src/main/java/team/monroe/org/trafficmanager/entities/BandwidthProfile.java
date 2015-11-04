package team.monroe.org.trafficmanager.entities;

import java.io.Serializable;

public class BandwidthProfile implements Serializable {

    public final String title;
    public final String description;
    public final int outLimit;
    public final int inLimit;

    public BandwidthProfile(String title, String description, int outLimit, int inLimit) {
        this.title = title;
        this.description = description;
        this.outLimit = outLimit;
        this.inLimit = inLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BandwidthProfile)) return false;

        BandwidthProfile that = (BandwidthProfile) o;

        if (inLimit != that.inLimit) return false;
        if (outLimit != that.outLimit) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (!title.equals(that.title)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + outLimit;
        result = 31 * result + inLimit;
        return result;
    }

    public int getStartPort() {
        return isTrafficDisabled() ? 0:1;
    }

    public boolean isTrafficDisabled() {
        return (inLimit == outLimit && inLimit == 0);
    }

    public int getEndPort() {
        return isTrafficDisabled() ? 0:BandwidthLimitRule.PORT_MAX_VALUE;
    }

    public int getInLimit() {
        return isTrafficDisabled() ? 1:inLimit;
    }

    public int getOutLimit() {
        return isTrafficDisabled() ? 1:outLimit;
    }
}
