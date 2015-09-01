package team.monroe.org.trafficmanager;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.utils.AndroidLogImplementation;
import org.monroe.team.android.box.utils.ExceptionsUtils;
import org.monroe.team.corebox.log.L;
import org.monroe.team.corebox.utils.P;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import team.monroe.org.trafficmanager.entities.BandwidthLimitRule;
import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.entities.ConnectionConfiguration;
import team.monroe.org.trafficmanager.entities.DeviceAlias;
import team.monroe.org.trafficmanager.entities.DeviceInfo;
import team.monroe.org.trafficmanager.entities.IpReservation;
import team.monroe.org.trafficmanager.uc.BandwidthProfileAddNew;
import team.monroe.org.trafficmanager.uc.BandwidthProfileGetAll;
import team.monroe.org.trafficmanager.uc.BandwidthRulesGetAll;
import team.monroe.org.trafficmanager.uc.DeviceAliasAdd;
import team.monroe.org.trafficmanager.uc.DeviceAliasGet;
import team.monroe.org.trafficmanager.uc.IpReservationGetAll;
import team.monroe.org.trafficmanager.uc.RouterConnectionConfigurationSave;

public class App extends ApplicationSupport<AppModel> {

    static {
        L.setup(new AndroidLogImplementation());
    }

    public Data<List<BandwidthLimitRule>> data_bandwidthLimitRules;
    public Data<List<DeviceInfo>> data_devicesInfo;
    public Data<List<IpReservation>> data_ipReservation;
    public Data<List<BandwidthProfile>> data_bandwidthProfiles;

    @Override
    protected AppModel createModel() {
        return new AppModel("route_manager", this);
    }

    @Override
    protected void onPostCreate() {
        super.onPostCreate();
        data_bandwidthLimitRules = new Data<List<BandwidthLimitRule>>(model()) {
            @Override
            protected List<BandwidthLimitRule> provideData() {
                return model().execute(BandwidthRulesGetAll.class, null);
            }
        };

        data_bandwidthProfiles = new Data<List<BandwidthProfile>>(model()) {
            @Override
            protected List<BandwidthProfile> provideData() {
                return model().execute(BandwidthProfileGetAll.class, null);
            }
        };

        data_ipReservation = new Data<List<IpReservation>>(model()) {
            @Override
            protected List<IpReservation> provideData() {
                return model().execute(IpReservationGetAll.class, null);
            }
        };

        data_ipReservation.addDataChangeObserver(new Data.DataChangeObserver<List<IpReservation>>() {
            @Override
            public void onDataInvalid() {
                data_devicesInfo.invalidate();
            }
            @Override
            public void onData(List<IpReservation> ipReservations) {}
        });

        data_devicesInfo = new Data<List<DeviceInfo>>(model()) {
            @Override
            protected List<DeviceInfo> provideData() {
                try {
                    List<IpReservation> reservations = data_ipReservation.fetch();
                    List<DeviceInfo> answer = new ArrayList<>();
                    for (IpReservation reservation : reservations) {
                        DeviceAlias alias = model().execute(DeviceAliasGet.class, reservation.mac);
                        answer.add(new DeviceInfo(alias, reservation));
                    }
                    return answer;
                } catch (FetchException e) {
                    Throwable throwable = ExceptionsUtils.resolveDataFetchException(e);
                    throw ExceptionsUtils.asRuntime(throwable);
                }
            }
        };
    }

    public boolean function_hasRouterConfiguration() {
        return false;
    }

    public void function_routerConfigurationSave(ConnectionConfiguration connectionConfiguration, ValueObserver<Void> observer) {
       fetchValue(RouterConnectionConfigurationSave.class,connectionConfiguration,new NoOpValueAdapter<Void>(),observer);
    }

    public void function_updateDeviceAlias(String mac, DeviceAlias alias, ValueObserver<DeviceAlias> observer) {
       fetchValue(DeviceAliasAdd.class, new P<String, DeviceAlias>(mac, alias), new NoOpValueAdapter<DeviceAlias>(){
           @Override
           public DeviceAlias adapt(DeviceAlias value) {
               data_devicesInfo.invalidate();
               return super.adapt(value);
           }
       }, observer);
    }

    public void function_addBandwidthProfile(BandwidthProfile bandwidthProfile, ValueObserver<BandwidthProfile> observer) {
                fetchValue(BandwidthProfileAddNew.class, bandwidthProfile, new NoOpValueAdapter<BandwidthProfile>(){
                    @Override
                    public BandwidthProfile adapt(BandwidthProfile value) {
                        data_bandwidthProfiles.invalidate();
                        return value;
                    }
                }, observer);
        }
}
