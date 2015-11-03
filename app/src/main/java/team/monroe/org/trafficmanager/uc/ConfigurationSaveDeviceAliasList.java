package team.monroe.org.trafficmanager.uc;

import org.monroe.team.android.box.json.Json;
import org.monroe.team.android.box.json.JsonBuilder;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.P;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import team.monroe.org.trafficmanager.entities.DeviceAlias;
import team.monroe.org.trafficmanager.manage.DeviceAliasManager;

public class ConfigurationSaveDeviceAliasList extends UserCaseSupport<File, Void> {

    public ConfigurationSaveDeviceAliasList(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void executeImpl(File request) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(request);
            outputStream.write(getDeviceAliasJsonString().getBytes());
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

    private String getDeviceAliasJsonString() {
        final JsonBuilder.Array json = JsonBuilder.array();
        using(DeviceAliasManager.class).forEach(new Closure<P<String, DeviceAlias>, Void>() {
            @Override
            public Void execute(P<String, DeviceAlias> arg) {
                JsonBuilder.Object device = JsonBuilder.object();
                device.field("alias", arg.second.alias);
                device.field("icon", arg.second.icon);
                device.field("mac", arg.first);
                json.add(device);
                return null;
            }
        });
        JsonBuilder.Object answer = JsonBuilder.object();
        answer.field("device_aliases", json);
        return JsonBuilder.build(answer).toJsonString();
    }
}
