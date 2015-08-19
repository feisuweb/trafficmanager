package team.monroe.org.trafficmanager.entities;

import android.content.res.Resources;

import java.io.Serializable;

public class DeviceInfo implements Serializable {

    public final DeviceAlias deviceAlias;
    public final IpReservation ipReservation;

    public DeviceInfo(DeviceAlias deviceAlias, IpReservation ipReservation) {
        this.deviceAlias = deviceAlias;
        this.ipReservation = ipReservation;
    }

    public String getAlias(Resources resources) {
        return deviceAlias != null ? deviceAlias.alias : "Alias not set";
    }

    public String getDescription(Resources resources) {
        return ipReservation.mac+" ( ip: "+ ipReservation.ip+")";
    }
}
