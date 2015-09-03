package team.monroe.org.trafficmanager.uc;

import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.List;

import team.monroe.org.trafficmanager.entities.BandwidthLimit;
import team.monroe.org.trafficmanager.entities.BandwidthLimitRule;
import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.entities.ConnectionConfiguration;
import team.monroe.org.trafficmanager.exceptions.NoConfigurationIssue;
import team.monroe.org.trafficmanager.manage.ObjectManager;
import team.monroe.org.trafficmanager.manage.RouterManager;


public class BandwidthLimitDelete extends UserCaseSupport<String, Boolean> {

    public BandwidthLimitDelete(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Boolean executeImpl(String id) {
        ConnectionConfiguration configuration = using(ObjectManager.class).getConnectionConfiguration();
        if (configuration == null) {
            throw new NoConfigurationIssue();
        }
        using(RouterManager.class).deleteBandwidthLimitRule(
                    configuration, id);
        return true;
    }

    public static class ActivationRequest{

        public final BandwidthLimit.Target target;
        public final BandwidthProfile profile;

        public ActivationRequest(BandwidthLimit.Target target, BandwidthProfile profile) {
            this.target = target;
            this.profile = profile;
        }
    }
}
