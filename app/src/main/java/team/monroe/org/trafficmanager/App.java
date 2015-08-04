package team.monroe.org.trafficmanager;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.data.Data;

import java.util.List;

import team.monroe.org.trafficmanager.entities.BandwidthLimitRule;
import team.monroe.org.trafficmanager.uc.GetBandwidthRules;

public class App extends ApplicationSupport<AppModel> {

    public Data<List<BandwidthLimitRule>> data_bandwidthLimitRules;

    @Override
    protected AppModel createModel() {
        return new AppModel("route_manager", this);
    }

    @Override
    protected void onPostCreate() {
        super.onPostCreate();
        data_bandwidthLimitRules = new Data<List<BandwidthLimitRule>>(model()) {
            @Override
            protected List<BandwidthLimitRule> provideData() {
                return model().execute(GetBandwidthRules.class, null);
            }
        };
    }

    public boolean function_hasRouterConfiguration() {
        return false;
    }
}
