package team.monroe.org.trafficmanager.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.Collections;
import java.util.List;

import team.monroe.org.trafficmanager.entities.BandwidthLimitRule;
import team.monroe.org.trafficmanager.entities.ConnectionConfiguration;
import team.monroe.org.trafficmanager.exceptions.NoConfigurationIssue;
import team.monroe.org.trafficmanager.manage.ObjectManager;

public class GetBandwidthRules extends UserCaseSupport<Void, List<BandwidthLimitRule>> {

    public GetBandwidthRules(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected List<BandwidthLimitRule> executeImpl(Void request) {
        ConnectionConfiguration configuration = using(ObjectManager.class).getConnectionConfiguration();
        if (configuration == null) {
            throw new NoConfigurationIssue();
        }
        return Collections.emptyList();
    }
}
