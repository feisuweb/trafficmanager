package team.monroe.org.trafficmanager.manage;

import android.content.Context;

import org.monroe.team.android.box.utils.SerializationMap;

public class FavoriteManager {
    private final SerializationMap<String, Boolean> serializationMap;


    public FavoriteManager(Context context) {
        this.serializationMap = new SerializationMap<>("favs.map",context);
    }

    public void updateFavoriteTarget(String id, boolean favorite) {
        serializationMap.put(id+"_target",favorite);
    }

    public boolean isFavoriteTarget(String id) {
       Boolean answer = serializationMap.get(id+"_target");
       if (answer == null){
           answer = false;
       }
       return answer;
    }
}
