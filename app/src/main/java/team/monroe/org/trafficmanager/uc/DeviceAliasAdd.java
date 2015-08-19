package team.monroe.org.trafficmanager.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;
import org.monroe.team.corebox.utils.P;

import team.monroe.org.trafficmanager.entities.DeviceAlias;
import team.monroe.org.trafficmanager.entities.IpReservation;
import team.monroe.org.trafficmanager.manage.DeviceAliasManager;

public class DeviceAliasAdd extends UserCaseSupport<P<String, DeviceAlias>, DeviceAlias>{

    public DeviceAliasAdd(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected DeviceAlias executeImpl(P<String, DeviceAlias> request) {
        using(DeviceAliasManager.class).put(request.first, request.second);
        return request.second;
    }
}
