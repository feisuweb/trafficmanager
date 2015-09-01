package team.monroe.org.trafficmanager.entities;

import java.io.Serializable;

public class BandwidthProfile implements Serializable{

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
}
