package team.monroe.org.trafficmanager;

import org.monroe.team.android.box.app.FragmentSupport;

public abstract class AbstractFragmentDashboard extends FragmentSupport<App>{


    final public ActivityDashboard dashboard(){
        return (ActivityDashboard) activity();
    }

}
