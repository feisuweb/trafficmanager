package team.monroe.org.trafficmanager.entities;

import java.io.Serializable;

public class DeviceAlias implements Serializable {

    public final String alias;
    public final int icon;

    public DeviceAlias(String alias, int icon) {
        this.alias = alias;
        this.icon = icon;
    }
}
