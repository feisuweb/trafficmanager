package team.monroe.org.trafficmanager.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.List;

import team.monroe.org.trafficmanager.entities.BandwidthLimitRule;
import team.monroe.org.trafficmanager.exceptions.NoConfigurationIssue;

public class GetBandwidthRules extends UserCaseSupport<Void, List<BandwidthLimitRule>> {

    public GetBandwidthRules(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected List<BandwidthLimitRule> executeImpl(Void request) {
        throw new NoConfigurationIssue();
    }
}
