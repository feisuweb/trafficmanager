package team.monroe.org.trafficmanager.uc;

import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;
import org.monroe.team.corebox.utils.P;

import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.exceptions.InvalidEntityIssue;
import team.monroe.org.trafficmanager.exceptions.Issue;
import team.monroe.org.trafficmanager.manage.BandwidthProfileManager;

public class BandwidthProfileUpdate extends UserCaseSupport<P<BandwidthProfile,BandwidthProfile>, BandwidthProfile> {

    public BandwidthProfileUpdate(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected BandwidthProfile executeImpl(P<BandwidthProfile, BandwidthProfile> request) {
        //Todo: check if its not in use
        using(BandwidthProfileManager.class).delete(request.first);
        try {
            using(Model.class).execute(BandwidthProfileAddNew.class, request.second);
        }catch (Issue e){
            using(Model.class).execute(BandwidthProfileAddNew.class, request.first);
            throw e;
        }
        return request.second;
    }

}
