package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.data.Data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.poc.ListPanelPresenter;

public class FragmentBodyPageBandwidthProfiles extends FragmentBodyPageDefault {


    private ListPanelPresenter<BandwidthProfile> mProfileListPresenter;

    @Override
    protected int getPanelLayoutId() {
        return R.layout.page_bandwith_profiles;
    }

    @Override
    protected ActivityDashboard.BodyPageId getPageId() {
        return ActivityDashboard.BodyPageId.BANDWIDTH_PROFILES;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProfileListPresenter = new ListPanelPresenter<BandwidthProfile>(
                activity().getLayoutInflater(),
                view(R.id.panel_single_instance_client, ViewGroup.class),
                viewResolver_profile(),
                viewResolver_defaultNoItems());
        view(R.id.action_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboard().dialog_editBandwidthProfile(null);
            }
        });
    }

    private ListPanelPresenter.DataViewResolver<BandwidthProfile> viewResolver_profile() {
        return new ListPanelPresenter.DataViewResolver<BandwidthProfile>() {
            @Override
            public View build(final BandwidthProfile profile, ViewGroup parent, LayoutInflater inflater) {
                View view = inflater.inflate(R.layout.item_bandwidth_profile, parent, false);
                ((TextView)view.findViewById(R.id.text_caption)).setText(profile.title);
                ((TextView)view.findViewById(R.id.text_description)).setText(profile.description);
                ((TextView)view.findViewById(R.id.text_in_limit)).setText(Integer.toString(profile.inLimit)+" kbps");
                ((TextView)view.findViewById(R.id.text_out_limit)).setText(Integer.toString(profile.outLimit)+" kbps");
                view.findViewById(R.id.action_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dashboard().dialog_editBandwidthProfile(profile);
                    }
                });
                view.findViewById(R.id.action_trash).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoading();
                        application().function_deleteBandwidthProfile(profile,new ApplicationSupport.ValueObserver<Void>() {
                            @Override
                            public void onSuccess(Void value) {
                            }

                            @Override
                            public void onFail(Throwable exception) {
                               handleException(exception);
                            }
                        });
                    }
                });
                return view;
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        application().data_bandwidthProfiles.addDataChangeObserver(new Data.DataChangeObserver<List<BandwidthProfile>>() {
            @Override
            public void onDataInvalid() {
                fetch_profiles();
            }

            @Override
            public void onData(List<BandwidthProfile> bandwidthProfiles) {

            }
        });
        fetch_profiles();
    }

    private void fetch_profiles() {
        showLoading();
        application().data_bandwidthProfiles.fetch(true, new Data.FetchObserver<List<BandwidthProfile>>() {
            @Override
            public void onFetch(List<BandwidthProfile> bandwidthProfiles) {
                update_profileUI(bandwidthProfiles);
            }

            @Override
            public void onError(Data.FetchError fetchError) {
                handleFetchError(fetchError);
            }
        });
    }

    private void update_profileUI(List<BandwidthProfile> bandwidthProfiles) {
        Collections.sort(bandwidthProfiles, new Comparator<BandwidthProfile>() {
            @Override
            public int compare(BandwidthProfile lhs, BandwidthProfile rhs) {
                return new Integer(lhs.inLimit).compareTo(rhs.inLimit);
            }
        });
        mProfileListPresenter.updateUI(bandwidthProfiles);
        showContent();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
