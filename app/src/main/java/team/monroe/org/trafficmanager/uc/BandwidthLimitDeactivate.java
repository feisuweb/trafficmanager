package team.monroe.org.trafficmanager.uc;

import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.List;

import team.monroe.org.trafficmanager.entities.BandwidthLimit;
import team.monroe.org.trafficmanager.entities.BandwidthLimitRule;
import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.entities.ConnectionConfiguration;
import team.monroe.org.trafficmanager.exceptions.InvalidStateIssue;
import team.monroe.org.trafficmanager.manage.ObjectManager;
import team.monroe.org.trafficmanager.manage.RouterManager;


public class BandwidthLimitDeactivate extends UserCaseSupport<BandwidthLimit.Target, Boolean> {

    public BandwidthLimitDeactivate(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Boolean executeImpl(BandwidthLimit.Target request) {
        List<BandwidthLimitRule> existingRules = using(Model.class).execute(BandwidthRulesGetAll.class, null);
        ConnectionConfiguration configuration = using(ObjectManager.class).getConnectionConfiguration();
        BandwidthLimitRule eRule = null;
        for (BandwidthLimitRule existingRule : existingRules) {
            if (existingRule.isForTarget(request)) {
                eRule = existingRule;
                break;
            }
        }
        if (eRule == null){
            //No rule and nothing to deactivate
            return true;
        }else {
            using(RouterManager.class).disableBandwidthLimitRule(
                    configuration, eRule.id);
        }
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
