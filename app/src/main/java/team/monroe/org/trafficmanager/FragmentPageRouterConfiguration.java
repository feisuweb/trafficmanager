package team.monroe.org.trafficmanager;

public class FragmentPageRouterConfiguration extends FragmentPageScrollablePanel{
    @Override
    protected int getPanelLayoutId() {
        return R.layout.page_router_configuration;
    }

    @Override
    protected String getHeaderText() {
        return "Router Configuration";
    }
}
