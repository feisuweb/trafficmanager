package team.monroe.org.trafficmanager.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.exceptions.InvalidEntityIssue;
import team.monroe.org.trafficmanager.manage.BandwidthProfileManager;

public class BandwidthProfileDelete extends UserCaseSupport<BandwidthProfile, BandwidthProfile> {

    public BandwidthProfileDelete(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected BandwidthProfile executeImpl(BandwidthProfile request) {
        //Todo: check if it`s not in use
        return using(BandwidthProfileManager.class).delete(request);
    }
}
