package team.monroe.org.trafficmanager;

import org.monroe.team.android.box.app.ApplicationSupport;

public class App extends ApplicationSupport<AppModel> {
    @Override
    protected AppModel createModel() {
        return new AppModel("route_manager", this);
    }

    public boolean function_hasRouterConfiguration() {
        return false;
    }
}
