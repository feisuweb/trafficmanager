package team.monroe.org.trafficmanager.uc;

import org.monroe.team.android.box.json.JsonBuilder;
import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.P;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.entities.DeviceAlias;
import team.monroe.org.trafficmanager.manage.DeviceAliasManager;

public class ConfigurationBackup extends UserCaseSupport<File, Void> {

    public ConfigurationBackup(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void executeImpl(File request) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(request);
            outputStream.write(getConfigurationAsJsonString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e1) {}
            }
            throw new IllegalStateException(e);
        }
        return null;
    }

    private String getConfigurationAsJsonString() {
        final JsonBuilder.Array deviceArrayJson = getDeviceAliasJson();
        final JsonBuilder.Array profileArrayJson = getProfileArrayJson();
        JsonBuilder.Object answer = JsonBuilder.object();
        answer.field("device_aliases", deviceArrayJson);
        answer.field("profiles", profileArrayJson);
        return JsonBuilder.build(answer).toJsonString();
    }

    private JsonBuilder.Array getProfileArrayJson() {
        List<BandwidthProfile> bandwidthProfileList = using(Model.class).execute(BandwidthProfileGetAll.class, null);
        final JsonBuilder.Array profileArrayJson = JsonBuilder.array();
        for (BandwidthProfile bandwidthProfile : bandwidthProfileList) {
            JsonBuilder.Object profile = JsonBuilder.object();
            profile.field("title", bandwidthProfile.title);
            profile.field("description", bandwidthProfile.description);
            profile.field("in", bandwidthProfile.inLimit);
            profile.field("out", bandwidthProfile.outLimit);
            profileArrayJson.add(profile);

        }
        return profileArrayJson;
    }

    private JsonBuilder.Array getDeviceAliasJson() {
        final JsonBuilder.Array deviceArrayJson = JsonBuilder.array();
        using(DeviceAliasManager.class).forEach(new Closure<P<String, DeviceAlias>, Void>() {
            @Override
            public Void execute(P<String, DeviceAlias> arg) {
                JsonBuilder.Object device = JsonBuilder.object();
                device.field("alias", arg.second.alias);
                device.field("icon", arg.second.icon);
                device.field("mac", arg.first);
                deviceArrayJson.add(device);
                return null;
            }
        });
        return deviceArrayJson;
    }
}
