package team.monroe.org.trafficmanager.entities;

import android.content.res.Resources;

import java.io.Serializable;

public class DeviceInfo implements Serializable, BandwidthLimit.Target {

    public final DeviceAlias deviceAlias;
    public final IpReservation ipReservation;
    public final boolean favorite;

    public DeviceInfo(DeviceAlias deviceAlias, IpReservation ipReservation, boolean favorite) {
        this.deviceAlias = deviceAlias;
        this.ipReservation = ipReservation;
        this.favorite = favorite;
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
    public String getId() {
        return ipReservation.mac;
    }

    @Override
    public String[] getIpSet() {
        return new String[]{ipReservation.ip, ipReservation.ip};
    }


    @Override
    public DeviceAlias getAlias() {
        return deviceAlias;
    }

    @Override
    public boolean isFavorite() {
        return favorite;
    }
}
