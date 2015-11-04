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


public class BandwidthLimitActivate extends UserCaseSupport<BandwidthLimitActivate.ActivationRequest, Boolean> {

    public BandwidthLimitActivate(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Boolean executeImpl(ActivationRequest request) {

        List<BandwidthLimitRule> existingRules = using(Model.class).execute(BandwidthRulesGetAll.class, null);

        ConnectionConfiguration configuration = using(ObjectManager.class).getConnectionConfiguration();

        BandwidthLimitRule eRule = null;
        for (BandwidthLimitRule existingRule : existingRules) {
            if (existingRule.isForTarget(request.target)) {
                eRule = existingRule;
                break;
            }
        }

        String ruleId = "0";
        if (eRule != null){
            ruleId = eRule.id;
        }

        using(RouterManager.class).updateBandwidthLimitRule(
                configuration,
                ruleId,
                request.target.getIpSet()[0],
                request.target.getIpSet()[1],
                request.profile.getStartPort(),
                request.profile.getEndPort(),
                request.profile.getInLimit(),
                request.profile.getOutLimit());
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
