package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.monroe.team.android.box.data.Data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import team.monroe.org.trafficmanager.entities.DeviceInfo;
import team.monroe.org.trafficmanager.poc.ListPanelPresenter;

public class FragmentBodyPageBandwidthProfiles extends FragmentBodyPageDefault {


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
    }



    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
