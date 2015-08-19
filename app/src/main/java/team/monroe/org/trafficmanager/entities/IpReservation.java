package team.monroe.org.trafficmanager.entities;

import java.io.Serializable;

public class IpReservation implements Serializable {

    public final String ip;
    public final String mac;

    public IpReservation(String ip, String mac) {
        this.ip = ip;
        this.mac = mac;
    }
}
