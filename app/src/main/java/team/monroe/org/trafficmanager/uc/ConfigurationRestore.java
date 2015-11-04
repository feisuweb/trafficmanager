package team.monroe.org.trafficmanager.uc;

import org.json.JSONException;
import org.monroe.team.android.box.json.Json;
import org.monroe.team.android.box.json.JsonBuilder;
import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.P;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.entities.DeviceAlias;
import team.monroe.org.trafficmanager.manage.BandwidthProfileManager;
import team.monroe.org.trafficmanager.manage.DeviceAliasManager;

public class ConfigurationRestore extends UserCaseSupport<FileDescriptor, Void> {

    public ConfigurationRestore(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void executeImpl(FileDescriptor request) {
        StringBuilder builder = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(request));
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            br.close();
            Json.JsonObject json = Json.createFromString(builder.toString()).asObject();
            loadDeviceAliases(json.asArray("device_aliases"));
            restoreProfiles(json.asArray("profiles"));
        }
        catch (IOException e) {
            if (br != null){
                try {
                    br.close();
                } catch (IOException e1) {}
            }
            throw new RuntimeException(e);
        } catch (JSONException e) {
           throw new RuntimeException(e);
        }
        return null;
    }

    private void restoreProfiles(Json.JsonArray profilesJson) {
        for (int i = 0; i< profilesJson.size(); i++){
            Json.JsonObject profile = profilesJson.asObject(i);
            using(BandwidthProfileManager.class).updateOrCreate(new BandwidthProfile(
                 profile.asString("title"),
                 profile.asString("description"),
                 profile.value("out", Integer.class),
                    profile.value("in", Integer.class)
                    ));
        }

    }

    private void loadDeviceAliases(Json.JsonArray device_aliases) {
        for (int i = 0; i< device_aliases.size(); i++){
            Json.JsonObject device = device_aliases.asObject(i);
            using(Model.class).execute(DeviceAliasAdd.class,new P<String, DeviceAlias>(device.asString("mac"),
                    new DeviceAlias(
                            device.asString("alias"),
                            device.value("icon", Integer.class)
                            )));
        }
    }

}
