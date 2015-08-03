package team.monroe.org.trafficmanager;

public class FragmentDashboardRouterConfiguration extends FragmentPageScrollablePanel{
    @Override
    protected int getPanelLayoutId() {
        return R.layout.fragment_dashboard_router_configuration;
    }

    @Override
    protected String getHeaderText() {
        return "Router Configuration";
    }

    @Override
    protected boolean isOnTop() {
        return true;
    }
}
