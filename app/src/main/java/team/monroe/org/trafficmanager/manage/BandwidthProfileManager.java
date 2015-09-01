package team.monroe.org.trafficmanager.manage;

import android.content.Context;

import org.monroe.team.android.box.utils.SerializationMap;

import java.util.ArrayList;
import java.util.List;

import team.monroe.org.trafficmanager.entities.BandwidthProfile;

public class BandwidthProfileManager {

    private final SerializationMap<String,BandwidthProfile> serializationMap;

    public BandwidthProfileManager(Context context) {
        this.serializationMap = new SerializationMap<>("profiles.map", context);
    }

    public List<BandwidthProfile> getAll() {
        return new ArrayList<>(serializationMap.values());
    }

    public BandwidthProfile get(String title) {
        return serializationMap.get(title);
    }

    public void updateOrCreate(BandwidthProfile request) {
        serializationMap.put(request.title, request);
    }
}
