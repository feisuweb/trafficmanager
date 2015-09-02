package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.monroe.team.android.box.data.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import team.monroe.org.trafficmanager.entities.BandwidthLimit;
import team.monroe.org.trafficmanager.entities.BandwidthLimitRule;
import team.monroe.org.trafficmanager.poc.ListPanelPresenter;

public class FragmentBodyPageBandwidthLimits extends FragmentBodyPageDefault {

    private ListPanelPresenter<BandwidthLimit> mLimitListPresenter;

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
                View view = inflater.inflate(R.layout.item_default, parent, false);
                ((TextView)view.findViewById(R.id.text_caption)).setText(bandwidthLimit.target.getAlias().alias);
                return view;
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        application().data_bandwidthLimits.addDataChangeObserver(new Data.DataChangeObserver<List<BandwidthLimit>>() {
            @Override
            public void onDataInvalid() {
                fetch_bandwidthLimits();
            }

            @Override
            public void onData(List<BandwidthLimit> bandwidthLimits) {

            }
        });
        fetch_bandwidthLimits();
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
                        return lhs.target.getIpSet()[0].compareTo(rhs.target.getIpSet()[0]);
                    }
                });

                updateUi_limits(limits);
                updateUi_unusedRules(unusedLimits);
            }

            @Override
            public void onError(Data.FetchError fetchError) {
                handleFetchError(fetchError);
            }
        });
    }

    private void updateUi_unusedRules(List<BandwidthLimitRule> bandwidthLimits) {

    }

    private void updateUi_limits(List<BandwidthLimit> bandwidthLimits) {
        mLimitListPresenter.updateUI(bandwidthLimits);
        showContent();
    }
}
