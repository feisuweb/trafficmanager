package team.monroe.org.trafficmanager;

import android.os.Bundle;

public abstract class FragmentDashboardBodyPage extends FragmentDashboardSupport {

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

    protected boolean isOnTop() {
        return dashboard().getCurrentPage() == this;
    }
}
