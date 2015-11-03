package team.monroe.org.trafficmanager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.utils.AndroidLogImplementation;
import org.monroe.team.android.box.utils.ExceptionsUtils;
import org.monroe.team.corebox.log.L;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.P;

import java.io.File;
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
import team.monroe.org.trafficmanager.manage.FavoriteManager;
import team.monroe.org.trafficmanager.uc.BandwidthLimitActivate;
import team.monroe.org.trafficmanager.uc.BandwidthLimitDeactivate;
import team.monroe.org.trafficmanager.uc.BandwidthLimitDelete;
import team.monroe.org.trafficmanager.uc.BandwidthProfileAddNew;
import team.monroe.org.trafficmanager.uc.BandwidthProfileDelete;
import team.monroe.org.trafficmanager.uc.BandwidthProfileGetAll;
import team.monroe.org.trafficmanager.uc.BandwidthProfileUpdate;
import team.monroe.org.trafficmanager.uc.BandwidthRulesGetAll;
import team.monroe.org.trafficmanager.uc.ConfigurationSaveDeviceAliasList;
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
    private Data<Boolean> mPendingExecution;

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
                        //TODO: kind of brain f**ck
                        boolean isFavorite = model().usingService(FavoriteManager.class).isFavoriteTarget(new DeviceInfo(alias, reservation, false).getId());
                        answer.add(new DeviceInfo(alias, reservation, isFavorite));
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
                    if (bandwidthLimitRule.isValid() && bandwidthLimitRule.isForTarget(target))return bandwidthLimitRule;
                }
                return null;
            }
        };
        data_devicesInfo.dependsOn(data_ipReservation);
        data_bandwidthLimits.dependsOn(data_bandwidthProfiles, data_bandwidthLimitRules, data_devicesInfo);

    }

    public boolean function_hasRouterConfiguration(){
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

    public void function_changeFavorite(BandwidthLimit.Target target, boolean favorite) {
         model().usingService(FavoriteManager.class).updateFavoriteTarget(target.getId(), favorite);
         data_devicesInfo.invalidate();
    }

    public Closure<Void, Boolean> function_pending_limitActivate(final BandwidthLimit.Target target, final BandwidthProfile bandwidthProfile) {
        return new Closure<Void, Boolean>() {
            @Override
            public Boolean execute(Void arg) {
                try {
                    return model().execute(BandwidthLimitActivate.class,
                            new BandwidthLimitActivate.ActivationRequest(target, bandwidthProfile));
                } finally {
                    data_bandwidthLimitRules.invalidate();
                }
            }
        };
    }

    public Closure<Void, Boolean> function_pending_limitDeactivate(final BandwidthLimit.Target target) {
        return new Closure<Void, Boolean>() {
            @Override
            public Boolean execute(Void arg) {
                try {
                return model().execute(BandwidthLimitDeactivate.class,
                        target);
                }finally {
                    data_bandwidthLimitRules.invalidate();
                }
            }
        };
    }

    public Closure<Void, Boolean> function_pending_limitDelete(final String limitRuleId) {
        return new Closure<Void, Boolean>() {
            @Override
            public Boolean execute(Void arg) {
                try {
                    return model().execute(BandwidthLimitDelete.class,
                            limitRuleId);
                }finally {
                    data_bandwidthLimitRules.invalidate();
                }
            }
        };
    }

    public void createPendingExecution(final Closure<Void, Boolean> execution) {
        mPendingExecution = new Data<Boolean>(model()) {
            @Override
            protected Boolean provideData() {
                return execution.execute(null);
            }
        };
    }

    public Data<Boolean> getPendingExecution() {
        return mPendingExecution;
    }


    public void function_shareConfigurationDevice(ValueObserver<Uri> observer) {
        File confsPath = new File(getCacheDir(), "confs");
        confsPath.mkdirs();
        final File newFile = new File(confsPath, "devices.tm");
        fetchValue(ConfigurationSaveDeviceAliasList.class, newFile, new ValueAdapter<Void, Uri>() {
            @Override
            public Uri adapt(Void value) {
                Uri contentUri = FileProvider.getUriForFile(App.this, "team.monroe.org.trafficmanager.fileprovider", newFile);
                return contentUri;
            }
        } , observer);
        /*
        },new ValueObserver<Void>() {

            @Override
            public void onSuccess(Void value) {
                Intent shareIntent = new Intent();
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share using"));
            }

            @Override
            public void onFail(final Throwable exception) {
                model().getResponseHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        throw new RuntimeException(exception);
                    }
                });
            }
        });*/
    }
}
