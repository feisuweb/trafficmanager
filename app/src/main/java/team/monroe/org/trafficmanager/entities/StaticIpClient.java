package team.monroe.org.trafficmanager.entities;

public class StaticIpClient {

    public final String ipAddress;
    public final String humanName;
    public final String mac;


    public StaticIpClient(String ipAddress, String humanName, String mac) {
        this.ipAddress = ipAddress;
        this.humanName = humanName;
        this.mac = mac;
    }
}
