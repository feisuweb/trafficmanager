package team.monroe.org.trafficmanager.uc;

import org.json.JSONException;
import org.monroe.team.android.box.json.Json;
import org.monroe.team.android.box.json.JsonBuilder;
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

import team.monroe.org.trafficmanager.entities.DeviceAlias;
import team.monroe.org.trafficmanager.manage.DeviceAliasManager;

public class ConfigurationLoadDeviceAliasList extends UserCaseSupport<FileDescriptor, Void> {

    public ConfigurationLoadDeviceAliasList(ServiceRegistry serviceRegistry) {
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

    private void loadDeviceAliases(Json.JsonArray device_aliases) {
        for (int i = 0; i< device_aliases.size(); i++){
            Json.JsonObject device = device_aliases.asObject(i);
            using(DeviceAliasManager.class).put(device.asString("mac"),
                    new DeviceAlias(
                            device.asString("alias"),
                            device.value("icon", Integer.class)
                            ));
        }
    }
}
