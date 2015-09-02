package team.monroe.org.trafficmanager.entities;

import android.content.res.Resources;

import java.io.Serializable;

public class DeviceInfo implements Serializable, BandwidthLimit.Target {

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

    public int getImageResourceId() {
        return DeviceType.by(deviceAlias == null ? 0 : deviceAlias.icon).drawableId;
    }

    @Override
    public String[] getIpSet() {
        return new String[]{ipReservation.ip, ipReservation.ip};
    }


    @Override
    public DeviceAlias getAlias() {
        return deviceAlias;
    }
}
