package team.monroe.org.trafficmanager.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.exceptions.InvalidEntityIssue;
import team.monroe.org.trafficmanager.manage.BandwidthProfileManager;

public class BandwidthProfileAddNew extends UserCaseSupport<BandwidthProfile, BandwidthProfile> {

    public BandwidthProfileAddNew(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected BandwidthProfile executeImpl(BandwidthProfile request) {
        BandwidthProfile exist = using(BandwidthProfileManager.class).get(request.title);
        if(exist != null){
            throw new InvalidEntityIssue(new IllegalStateException("Duplicate profile title"));
        }
        using(BandwidthProfileManager.class).updateOrCreate(request);
        return request;
    }
}
