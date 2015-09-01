package team.monroe.org.trafficmanager.entities;

import java.io.Serializable;

public class BandwidthProfile implements Serializable{

    public final String title;
    public final String description;

    public final String startIp;
    public final String endIp;
    public final int outLimit;
    public final int inLimit;

    public BandwidthProfile(String title, String description, String startIp, String endIp, int outLimit, int inLimit) {
        this.title = title;
        this.description = description;
        this.startIp = startIp;
        this.endIp = endIp;
        this.outLimit = outLimit;
        this.inLimit = inLimit;
    }
}
