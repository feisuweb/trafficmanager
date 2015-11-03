package team.monroe.org.trafficmanager.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.io.File;
import java.io.FileOutputStream;

public class ConfigurationSaveDeviceAliasList extends UserCaseSupport<File, Void> {

    public ConfigurationSaveDeviceAliasList(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void executeImpl(File request) {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(request);
            outputStream.write("Test".getBytes());
            outputStream.close();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return null;
    }
}
