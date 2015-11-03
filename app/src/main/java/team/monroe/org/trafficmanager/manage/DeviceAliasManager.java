package team.monroe.org.trafficmanager.manage;

import android.content.Context;

import org.monroe.team.android.box.utils.SerializationMap;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.P;

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

    public void forEach(Closure<P<String,DeviceAlias>,Void> action){
        for (String s : serializationMap.keys()) {
            action.execute(new P<String, DeviceAlias>(s, serializationMap.get(s)));
        }

    }
}
