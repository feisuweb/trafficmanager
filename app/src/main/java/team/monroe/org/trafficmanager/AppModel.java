package team.monroe.org.trafficmanager;

import android.content.Context;

import org.monroe.team.android.box.app.AndroidModel;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.services.HttpManager;
import org.monroe.team.corebox.services.ServiceRegistry;

import java.util.List;

import team.monroe.org.trafficmanager.entities.IpReservation;
import team.monroe.org.trafficmanager.manage.DeviceAliasManager;
import team.monroe.org.trafficmanager.manage.ObjectManager;
import team.monroe.org.trafficmanager.manage.RouterManager;
import team.monroe.org.trafficmanager.uc.IpReservationGetAll;

public class AppModel extends AndroidModel {


    public AppModel(String appName, Context context) {
        super(appName, context);
    }

    @Override
    protected void constructor(String appName, Context context, ServiceRegistry serviceRegistry) {
        HttpManager httpManager = new HttpManager();

        RouterManager routerManager = new RouterManager(httpManager);
        serviceRegistry.registrate(RouterManager.class, routerManager);
        serviceRegistry.registrate(DeviceAliasManager.class, new DeviceAliasManager(context));
        serviceRegistry.registrate(ObjectManager.class, new ObjectManager(context));


    }
}
