package team.monroe.org.trafficmanager;

public class FragmentPageClients extends FragmentPageScrollablePanel{
    @Override
    protected int getPanelLayoutId() {
        return R.layout.page_bandwidth_limits;
    }

    @Override
    protected String getHeaderText() {
        return "Bandwidth Clients";
    }
}
