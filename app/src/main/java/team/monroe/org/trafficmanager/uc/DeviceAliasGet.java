package team.monroe.org.trafficmanager.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.List;

import team.monroe.org.trafficmanager.entities.DeviceAlias;
import team.monroe.org.trafficmanager.entities.DeviceInfo;
import team.monroe.org.trafficmanager.entities.IpReservation;
import team.monroe.org.trafficmanager.manage.DeviceAliasManager;


public class DeviceAliasGet extends UserCaseSupport<String, DeviceAlias> {

    public DeviceAliasGet(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected DeviceAlias executeImpl(String request) {
        DeviceAlias alias = using(DeviceAliasManager.class).get(request);
        return alias;
    }

}
