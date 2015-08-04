package team.monroe.org.trafficmanager.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.trafficmanager.entities.ConnectionConfiguration;
import team.monroe.org.trafficmanager.manage.RouterManager;

public class RouterConnectionConfigurationSave extends UserCaseSupport<ConnectionConfiguration, Void> {

    public RouterConnectionConfigurationSave(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void executeImpl(ConnectionConfiguration request) {
       using(RouterManager.class).checkOnline(request);
       return null;
    }
}
