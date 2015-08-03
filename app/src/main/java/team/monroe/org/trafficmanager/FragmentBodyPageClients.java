package team.monroe.org.trafficmanager;

public class FragmentBodyPageClients extends FragmentBodyPageScrollablePanel {
    @Override
    protected int getPanelLayoutId() {
        return R.layout.page_bandwidth_limits;
    }

    @Override
    protected String getHeaderText() {
        return "Bandwidth Clients";
    }
}
