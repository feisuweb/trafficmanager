package team.monroe.org.trafficmanager;

import android.os.Bundle;

import org.monroe.team.android.box.app.ActivitySupport;


public class ActivityDashboard extends ActivitySupport<App> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        if (isFirstRun()){
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.frag_dash_header, new FragmentDashboardHeader())
                    .add(R.id.frag_dash_navigation, new FragmentDashboardNavigation())
                    .add(R.id.frag_dash_body, new FragmentDashboardPager())
                    .commit();
        }
    }

    public void onScreenChanged(int position) {

    }

}
