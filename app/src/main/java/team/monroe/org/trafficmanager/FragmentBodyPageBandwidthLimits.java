package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.data.RefreshableCachedData;

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
    private ApplicationSupport.PeriodicalAction mRefreshAction;
    private ListPanelPresenter<BandwidthLimitRule> mUnsupportedLimitListPresenter;

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
        mUnsupportedLimitListPresenter = new ListPanelPresenter<BandwidthLimitRule>(
                activity().getLayoutInflater(),
                view(R.id.panel_unsupported_limits, ViewGroup.class),
                viewResolver_unsupportedLimitRules(),
                viewResolver_defaultNoItems());
    }

    private ListPanelPresenter.DataViewResolver<BandwidthLimitRule> viewResolver_unsupportedLimitRules() {
        return new ListPanelPresenter.DataViewResolver<BandwidthLimitRule>() {
            @Override
            public View build(final BandwidthLimitRule bandwidthLimitRule, ViewGroup parent, LayoutInflater inflater) {
                View view = inflater.inflate(R.layout.item_limit_rule_unsupported, parent, false);
                String caption = bandwidthLimitRule.startIp+" - " +bandwidthLimitRule.endIp+" : "+bandwidthLimitRule.startPort+" - "+bandwidthLimitRule.endPort;
                ((TextView)view.findViewById(R.id.text_caption)).setText(caption);
                String description = "Protocol: "+bandwidthLimitRule.protocol+" In limit: "+bandwidthLimitRule.minInLimit+" ( max "+bandwidthLimitRule.maxInLimit+" ) kbps";
                description += " Out limit: "+bandwidthLimitRule.minOutLimit+" ( max "+bandwidthLimitRule.maxOutLimit+" ) kbps";
                ((TextView)view.findViewById(R.id.text_description)).setText(description);
                view.findViewById(R.id.action_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dashboard().dialog_execute(application().function_pending_limitDelete(bandwidthLimitRule.id));
                    }
                });
                return view;
            }
        };
    }


    private ListPanelPresenter.DataViewResolver<BandwidthLimit> viewResolver_limit() {
        return new ListPanelPresenter.DataViewResolver<BandwidthLimit>() {
            @Override
            public View build(final BandwidthLimit bandwidthLimit, ViewGroup parent, LayoutInflater inflater) {
                View view = inflater.inflate(R.layout.item_limit, parent, false);
                ((TextView)view.findViewById(R.id.text_caption)).setText(bandwidthLimit.target.getAlias().alias);
                ((TextView)view.findViewById(R.id.text_ip)).setText(ipSetAsString(bandwidthLimit.target.getIpSet()));
                ((ImageView)view.findViewById(R.id.image)).setImageResource(DeviceType.by(bandwidthLimit.target.getAlias().icon).drawableId);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_enabled);
                checkBox.setChecked(bandwidthLimit.source != null && bandwidthLimit.source.enabled);
                final Spinner profileSpinner = (Spinner) view.findViewById(R.id.spinner);
                initializeSpinner(profileSpinner, bandwidthLimit);
                profileSpinner.setEnabled(checkBox.isChecked());
                profileSpinner.setAlpha(checkBox.isChecked()?1f:0.5f);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            BandwidthProfile bandwidthProfile = (BandwidthProfile) profileSpinner.getSelectedItem();
                            dashboard().dialog_execute(application().function_pending_limitActivate(bandwidthLimit.target, bandwidthProfile));
                        }else {
                            dashboard().dialog_execute(application().function_pending_limitDeactivate(bandwidthLimit.target));
                        }

                    }
                });
                profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    boolean isFirstTime = true;
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        BandwidthProfile bandwidthProfile = (BandwidthProfile) profileSpinner.getSelectedItem();
                        if (isFirstTime || bandwidthProfile.equals(bandwidthLimit.profile)) {
                            isFirstTime = false;
                            return;
                        }
                        dashboard().dialog_execute(application().function_pending_limitActivate(bandwidthLimit.target, bandwidthProfile));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
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
                                    TextView description = (TextView) convertView.findViewById(R.id.text_description);
                                    TextView inLimit = (TextView) convertView.findViewById(R.id.text_in_limit);
                                    TextView outLimit = (TextView) convertView.findViewById(R.id.text_out_limit);

                                    @Override
                                    public void update(BandwidthProfile profile, int position) {
                                        caption.setText(profile.title);
                                        description.setText(profile.description);
                                        inLimit.setText(Integer.toString(profile.inLimit)+" kbps");
                                        outLimit.setText(Integer.toString(profile.outLimit)+" kbps");
                                    }

                                };
                            }
                        }, R.layout.item_bandwith_profile_vert);
                int selectionIndex = 0;
                if (limit.profile != null){
                    selectionIndex = mBandwidthProfiles.indexOf(limit.profile);
                    if (selectionIndex == -1){
                        profileSpinnerAdapter.add(limit.profile);
                        selectionIndex = 0;
                    }

                }
                profileSpinnerAdapter.addAll(mBandwidthProfiles);
                profileSpinner.setAdapter(profileSpinnerAdapter);
                profileSpinner.setSelection(selectionIndex);
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
                /*runLastOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBandwidthLimits = null;
                        fetch_bandwidthLimits();
                    }
                });*/
            }

            @Override
            public void onData(List<BandwidthLimit> bandwidthLimits) {

            }
        };
        mRefreshAction = application().preparePeriodicalAction(new Runnable() {
            @Override
            public void run() {
                fetch_bandwidthLimits();
            }
        });

        application().data_bandwidthLimits.addDataChangeObserver(observer_bandwidthLimits);
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
        mRefreshAction.start(0, 2000);
    }

    @Override
    public void onStop() {
        super.onStop();
        mRefreshAction.stop();
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
                updateUi_unusedRules(unusedLimits);
                if (limits.equals(mBandwidthLimits)){
                    return;
                }
                mBandwidthLimits = limits;
                updateUi_limits();
            }

            @Override
            public void onError(Data.FetchError fetchError) {
                handleFetchError(fetchError);
            }
        });
    }

    private void updateUi_unusedRules(List<BandwidthLimitRule> bandwidthLimits) {
        view(R.id.panel_unsupported_limits_content).setVisibility(bandwidthLimits.isEmpty()? View.GONE:View.VISIBLE);
        if (bandwidthLimits.isEmpty()){
            return;
        }
        mUnsupportedLimitListPresenter.updateUI(bandwidthLimits);
    }

    private void updateUi_limits() {
        if (mBandwidthProfiles == null || mBandwidthLimits == null){
            return;
        }
        showContent();
        mLimitListPresenter.updateUI(mBandwidthLimits);
    }
}
