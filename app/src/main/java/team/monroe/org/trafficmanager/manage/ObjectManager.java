package team.monroe.org.trafficmanager.manage;

import android.content.Context;

import org.monroe.team.android.box.utils.SerializationMap;

import java.io.Serializable;

import team.monroe.org.trafficmanager.entities.ConnectionConfiguration;

public class ObjectManager {

    private final SerializationMap<String, Serializable> serializationMap;

    public ObjectManager(Context context) {
        this.serializationMap = new SerializationMap<>("setting_manager.map", context);
    }

    public void putConnectionConfiguration(ConnectionConfiguration configuration){
        serializationMap.put("connection_details", configuration);
    }
    public ConnectionConfiguration getConnectionConfiguration(){
        return (ConnectionConfiguration) serializationMap.get("connection_details");
    }
}
