package team.monroe.org.trafficmanager;

import android.content.Context;

import org.monroe.team.android.box.app.AndroidModel;
import org.monroe.team.android.box.services.HttpManager;
import org.monroe.team.corebox.services.ServiceRegistry;

import team.monroe.org.trafficmanager.manage.ObjectManager;
import team.monroe.org.trafficmanager.manage.RouterManager;

public class AppModel extends AndroidModel {
    public AppModel(String appName, Context context) {
        super(appName, context);
    }

    @Override
    protected void constructor(String appName, Context context, ServiceRegistry serviceRegistry) {
        HttpManager httpManager = new HttpManager();

        RouterManager routerManager = new RouterManager(httpManager);
        serviceRegistry.registrate(RouterManager.class, routerManager);

        serviceRegistry.registrate(ObjectManager.class, new ObjectManager(context));
    }
}
