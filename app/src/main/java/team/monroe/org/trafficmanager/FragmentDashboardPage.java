package team.monroe.org.trafficmanager;

import android.os.Bundle;

public abstract class FragmentDashboardPage extends AbstractFragmentDashboard {

    final public void onPageSelect(){
        updateHeader();
        onPageOnTop();
    }

    protected void onPageOnTop() {}

    protected abstract String getHeaderText();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateHeader();
    }

    private void updateHeader() {
        if(isOnTop()) {
            dashboard().setHeaderText(getHeaderText());
        }
    }

    private boolean isOnTop() {
        return dashboard().getCurrentPage() == this;
    }
}
