package team.monroe.org.trafficmanager;

public class FragmentBodyPageClients extends FragmentBodyPageDefault {
    @Override
    protected int getPanelLayoutId() {
        return R.layout.page_bandwidth_limits;
    }


    @Override
    protected ActivityDashboard.BodyPageId getPageId() {
        return ActivityDashboard.BodyPageId.CLIENTS;
    }
}
