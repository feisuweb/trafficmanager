package team.monroe.org.trafficmanager;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.utils.AndroidLogImplementation;
import org.monroe.team.android.box.utils.ExceptionsUtils;
import org.monroe.team.corebox.log.L;
import org.monroe.team.corebox.utils.P;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import team.monroe.org.trafficmanager.entities.BandwidthLimit;
import team.monroe.org.trafficmanager.entities.BandwidthLimitRule;
import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.entities.ConnectionConfiguration;
import team.monroe.org.trafficmanager.entities.DeviceAlias;
import team.monroe.org.trafficmanager.entities.DeviceInfo;
import team.monroe.org.trafficmanager.entities.IpReservation;
import team.monroe.org.trafficmanager.exceptions.NoBandwidthProfileIssue;
import team.monroe.org.trafficmanager.uc.BandwidthLimitActivate;
import team.monroe.org.trafficmanager.uc.BandwidthProfileAddNew;
import team.monroe.org.trafficmanager.uc.BandwidthProfileDelete;
import team.monroe.org.trafficmanager.uc.BandwidthProfileGetAll;
import team.monroe.org.trafficmanager.uc.BandwidthProfileUpdate;
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
    public Data<List<BandwidthLimit>> data_bandwidthLimits;

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

        data_bandwidthLimits = new Data<List<BandwidthLimit>>(model()) {
            @Override
            protected List<BandwidthLimit> provideData() {
                try {

                    List<BandwidthProfile> bandwidthProfiles = data_bandwidthProfiles.fetch();
                    if (bandwidthProfiles.isEmpty()){
                        throw new NoBandwidthProfileIssue();
                    }

                    Set<String> matchedRuleSet = new HashSet<>();
                    List<BandwidthLimitRule> bandwidthLimitRules = data_bandwidthLimitRules.fetch();
                    List<DeviceInfo> deviceInfos = data_devicesInfo.fetch();
                    List<BandwidthLimit> answer = new ArrayList<>(deviceInfos.size());

                    for (BandwidthLimit.Target target : deviceInfos) {
                         if (target.getAlias() != null){
                             BandwidthLimitRule rule = findRule(target, bandwidthLimitRules);
                             BandwidthProfile profile = findProfile(rule, bandwidthProfiles);
                             if (profile == null && rule != null){
                                 profile = rule.asProfile("Unknown Profile", "This profile created outside application");
                             }
                             answer.add(new BandwidthLimit(target, profile, rule));
                             if (rule != null) {
                                 matchedRuleSet.add(rule.id);
                             }
                         }
                    }

                    for (BandwidthLimitRule bandwidthLimitRule : bandwidthLimitRules) {
                         if (!matchedRuleSet.contains(bandwidthLimitRule.id)){
                             answer.add(new BandwidthLimit(null,null,bandwidthLimitRule));
                         }
                    }

                    return answer;

                } catch (FetchException e) {
                    Throwable throwable = ExceptionsUtils.resolveDataFetchException(e);
                    throw ExceptionsUtils.asRuntime(throwable);
                }
            }

            private BandwidthProfile findProfile(BandwidthLimitRule rule, List<BandwidthProfile> bandwidthProfiles) {
                if (rule == null) return null;
                for (BandwidthProfile bandwidthProfile : bandwidthProfiles) {
                    if (rule.matchProfile(bandwidthProfile)){
                        return bandwidthProfile;
                    }
                }
                return null;
            }

            private BandwidthLimitRule findRule(BandwidthLimit.Target target, List<BandwidthLimitRule> bandwidthLimitRules) {
                for (BandwidthLimitRule bandwidthLimitRule : bandwidthLimitRules) {
                    if (bandwidthLimitRule.isForTarget(target))return bandwidthLimitRule;
                }
                return null;
            }
        };
        data_devicesInfo.dependsOn(data_ipReservation);
        data_bandwidthLimits.dependsOn(data_bandwidthProfiles, data_bandwidthLimitRules, data_devicesInfo);

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

    public void function_updateBandwidthProfile(BandwidthProfile oldBandwidthProfile, BandwidthProfile newBandwidthProfile, ValueObserver<BandwidthProfile> observer) {
        fetchValue(BandwidthProfileUpdate.class, new P<BandwidthProfile, BandwidthProfile>(oldBandwidthProfile,newBandwidthProfile), new NoOpValueAdapter<BandwidthProfile>(){
            @Override
            public BandwidthProfile adapt(BandwidthProfile value) {
                data_bandwidthProfiles.invalidate();
                return value;
            }
        }, observer);
    }

    public void function_deleteBandwidthProfile(BandwidthProfile profile, ValueObserver<Void> observer) {
        fetchValue(BandwidthProfileDelete.class, profile, new ValueAdapter<BandwidthProfile, Void>() {
            @Override
            public Void adapt(BandwidthProfile value) {
                data_bandwidthProfiles.invalidate();
                return null;
            }
        }, observer);
    }

    public void function_limitActivate(BandwidthLimit.Target target, BandwidthProfile bandwidthProfile, ValueObserver<Boolean> observer) {
        fetchValue(BandwidthLimitActivate.class,
                new BandwidthLimitActivate.ActivationRequest(target, bandwidthProfile),
                new NoOpValueAdapter<Boolean>(){
                    @Override
                    public Boolean adapt(Boolean value) {
                        data_bandwidthLimitRules.invalidate();
                        return super.adapt(value);
                    }
                },observer);
    }
}
