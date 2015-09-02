package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;
import org.monroe.team.android.box.data.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import team.monroe.org.trafficmanager.entities.BandwidthLimit;
import team.monroe.org.trafficmanager.entities.BandwidthLimitRule;
import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.entities.DeviceType;
import team.monroe.org.trafficmanager.poc.ListPanelPresenter;

public class FragmentBodyPageBandwidthLimits extends FragmentBodyPageDefault {

    private ListPanelPresenter<BandwidthLimit> mLimitListPresenter;
    private Data.DataChangeObserver<List<BandwidthLimit>> observer_bandwidthLimits;
    private Data.DataChangeObserver<List<BandwidthProfile>> observer_bandwidthProfiles;
    private List<BandwidthLimit> mBandwidthLimits;
    private List<BandwidthProfile> mBandwidthProfiles;

    @Override
    protected int getPanelLayoutId() {
        return R.layout.page_bandwidth_limits;
    }


    @Override
    protected ActivityDashboard.BodyPageId getPageId() {
        return ActivityDashboard.BodyPageId.BANDWIDTH_LIMITS;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLimitListPresenter = new ListPanelPresenter<BandwidthLimit>(
                activity().getLayoutInflater(),
                view(R.id.panel_limits, ViewGroup.class),
                viewResolver_limit(),
                viewResolver_defaultNoItems());
    }

    private ListPanelPresenter.DataViewResolver<BandwidthLimit> viewResolver_limit() {
        return new ListPanelPresenter.DataViewResolver<BandwidthLimit>() {
            @Override
            public View build(BandwidthLimit bandwidthLimit, ViewGroup parent, LayoutInflater inflater) {
                View view = inflater.inflate(R.layout.item_limit, parent, false);
                ((TextView)view.findViewById(R.id.text_caption)).setText(bandwidthLimit.target.getAlias().alias);
                ((TextView)view.findViewById(R.id.text_ip)).setText(ipSetAsString(bandwidthLimit.target.getIpSet()));
                ((ImageView)view.findViewById(R.id.image)).setImageResource(DeviceType.by(bandwidthLimit.target.getAlias().icon).drawableId);
                Spinner profileSpinner = (Spinner) view.findViewById(R.id.spinner);
                initializeSpinner(profileSpinner, bandwidthLimit);
                return view;
            }

            private void initializeSpinner(Spinner profileSpinner, BandwidthLimit limit) {
                GenericListViewAdapter<BandwidthProfile, GetViewImplementation.ViewHolder<BandwidthProfile>> profileSpinnerAdapter
                        = new GenericListViewAdapter<BandwidthProfile, GetViewImplementation.ViewHolder<BandwidthProfile>>(getActivity(),
                        new GetViewImplementation.ViewHolderFactory<GetViewImplementation.ViewHolder<BandwidthProfile>>() {
                            @Override
                            public GetViewImplementation.ViewHolder<BandwidthProfile> create(final View convertView) {
                                return new GetViewImplementation.GenericViewHolder<BandwidthProfile>() {

                                    TextView caption = (TextView) convertView.findViewById(R.id.text_caption);

                                    @Override
                                    public void update(BandwidthProfile profile, int position) {
                                        caption.setText(profile.title);
                                    }
                                };
                            }
                        }, R.layout.item_default);

                profileSpinnerAdapter.addAll(mBandwidthProfiles);
                profileSpinner.setAdapter(profileSpinnerAdapter);
            }
        };
    }

    private String ipSetAsString(String[] ipSet) {
        return (ipSet[0].equals(ipSet[1]))?ipSet[0]:ipSet[0]+" - "+ipSet[1];
    }

    @Override
    public void onStart() {
        super.onStart();
        observer_bandwidthLimits = new Data.DataChangeObserver<List<BandwidthLimit>>() {
            @Override
            public void onDataInvalid() {
                mBandwidthLimits = null;
                fetch_bandwidthLimits();
            }

            @Override
            public void onData(List<BandwidthLimit> bandwidthLimits) {

            }
        };
        application().data_bandwidthLimits.addDataChangeObserver(observer_bandwidthLimits);
        fetch_bandwidthLimits();
        observer_bandwidthProfiles = new Data.DataChangeObserver<List<BandwidthProfile>>() {
            @Override
            public void onDataInvalid() {
                mBandwidthProfiles = null;
                fetch_profiles();
            }

            @Override
            public void onData(List<BandwidthProfile> bandwidthProfiles) {

            }
        };
        application().data_bandwidthProfiles.addDataChangeObserver(observer_bandwidthProfiles);
        fetch_profiles();
    }

    private void fetch_profiles() {
        showLoading();
        application().data_bandwidthProfiles.fetch(true, new Data.FetchObserver<List<BandwidthProfile>>() {
            @Override
            public void onFetch(List<BandwidthProfile> bandwidthProfiles) {
                mBandwidthProfiles = bandwidthProfiles;
                updateUi_limits();
            }

            @Override
            public void onError(Data.FetchError fetchError) {
                handleFetchError(fetchError);
            }
        });
    }

    private void fetch_bandwidthLimits() {
        showLoading();
        application().data_bandwidthLimits.fetch(true, new Data.FetchObserver<List<BandwidthLimit>>() {
            @Override
            public void onFetch(final List<BandwidthLimit> bandwidthLimits) {
                List<BandwidthLimitRule> unusedLimits = new ArrayList<BandwidthLimitRule>();
                List<BandwidthLimit> limits = new ArrayList<BandwidthLimit>();

                for (BandwidthLimit bandwidthLimit : bandwidthLimits) {
                    if (bandwidthLimit.target == null){
                        unusedLimits.add(bandwidthLimit.source);
                    } else {
                        limits.add(bandwidthLimit);
                    }
                }

                Collections.sort(limits, new Comparator<BandwidthLimit>() {
                    @Override
                    public int compare(BandwidthLimit lhs, BandwidthLimit rhs) {
                        return new Integer(lhs.target.getAlias().icon).compareTo(rhs.target.getAlias().icon);
                    }
                });

                mBandwidthLimits = limits;
                updateUi_unusedRules(unusedLimits);
                updateUi_limits();
            }

            @Override
            public void onError(Data.FetchError fetchError) {
                handleFetchError(fetchError);
            }
        });
    }

    private void updateUi_unusedRules(List<BandwidthLimitRule> bandwidthLimits) {

    }

    private void updateUi_limits() {
        if (mBandwidthProfiles == null || mBandwidthLimits == null){
            return;
        }
        showContent();
        mLimitListPresenter.updateUI(mBandwidthLimits);
    }
}
