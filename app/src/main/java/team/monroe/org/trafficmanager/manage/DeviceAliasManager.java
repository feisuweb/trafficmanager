package team.monroe.org.trafficmanager.manage;

import android.content.Context;

import org.monroe.team.android.box.utils.SerializationMap;

import team.monroe.org.trafficmanager.entities.DeviceAlias;

public class DeviceAliasManager {

    private SerializationMap<String, DeviceAlias> serializationMap;

    public DeviceAliasManager(Context context) {
        this.serializationMap = new SerializationMap<>("aliases.map", context);
    }

    public DeviceAlias get(String mac) {
        return serializationMap.get(mac);
    }

    public void put(String mac, DeviceAlias alias) {
        serializationMap.put(mac, alias);
    }
}
