package team.monroe.org.trafficmanager.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.List;

import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.manage.BandwidthProfileManager;

public class BandwidthProfileGetAll extends UserCaseSupport<Void, List<BandwidthProfile>>{

    public BandwidthProfileGetAll(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected List<BandwidthProfile> executeImpl(Void request) {
        return using(BandwidthProfileManager.class).getAll();
    }
}
