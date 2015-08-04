package team.monroe.org.trafficmanager;

import org.monroe.team.android.box.data.Data;

import java.util.List;

import team.monroe.org.trafficmanager.entities.BandwidthLimitRule;

public class FragmentBodyPageBandwidthLimits extends FragmentBodyPageDefault {
    @Override
    protected int getPanelLayoutId() {
        return R.layout.page_bandwidth_limits;
    }


    @Override
    protected ActivityDashboard.BodyPageId getPageId() {
        return ActivityDashboard.BodyPageId.BANDWIDTH_LIMITS;
    }

    @Override
    public void onStart() {
        super.onStart();
        application().data_bandwidthLimitRules.fetch(true, new Data.FetchObserver<List<BandwidthLimitRule>>() {

            @Override
            public void onFetch(List<BandwidthLimitRule> bandwidthLimitRules) {

            }

            @Override
            public void onError(Data.FetchError fetchError) {
                handleFetchError(fetchError);
            }
        });
    }
}
