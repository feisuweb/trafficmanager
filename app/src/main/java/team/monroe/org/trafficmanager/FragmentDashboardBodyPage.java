package team.monroe.org.trafficmanager;

import android.os.Bundle;

public abstract class FragmentDashboardBodyPage extends FragmentDashboardContentSupport {

    final public void onPageSelect(){
        updateHeader();
        onPageOnTop();
    }

    protected void onPageOnTop() {}

    protected abstract ActivityDashboard.BodyPageId getPageId();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateHeader();
    }

    private void updateHeader() {
        if(isOnTop()) {
            dashboard().updateHeader(getPageId(), isStepBackSupport());
        }
    }

    protected boolean isStepBackSupport() {
        return false;
    }

    protected boolean isOnTop() {
        return dashboard().getCurrentPage() == this;
    }
}
