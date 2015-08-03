package team.monroe.org.trafficmanager;

public class FragmentDashboardBodyPageRouterConfiguration extends FragmentBodyPageScrollablePanel {
    @Override
    protected int getPanelLayoutId() {
        return R.layout.fragment_dashboard_router_configuration;
    }

    @Override
    protected String getHeaderText() {
        return "Router Configuration";
    }

}
