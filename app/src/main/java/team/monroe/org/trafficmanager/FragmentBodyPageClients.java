package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.monroe.team.android.box.data.Data;

import java.util.List;

import team.monroe.org.trafficmanager.entities.DeviceInfo;
import team.monroe.org.trafficmanager.poc.ListPanelPresenter;

public class FragmentBodyPageClients extends FragmentBodyPageDefault {

    private Data.DataChangeObserver<List<DeviceInfo>> mStaticIpClientObserver;
    private ListPanelPresenter<DeviceInfo> mSingleClientPresenter;

    @Override
    protected int getPanelLayoutId() {
        return R.layout.page_clients;
    }

    @Override
    protected ActivityDashboard.BodyPageId getPageId() {
        return ActivityDashboard.BodyPageId.CLIENTS;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSingleClientPresenter = new ListPanelPresenter<DeviceInfo>(
                activity().getLayoutInflater(),
                view(R.id.panel_single_instance_client, ViewGroup.class),
                viewResolver_singleClientListView(),
                viewResolver_defaultNoItems());
    }

    private ListPanelPresenter.DataViewResolver<DeviceInfo> viewResolver_singleClientListView() {
        return new ListPanelPresenter.DataViewResolver<DeviceInfo>() {
            @Override
            public View build(final DeviceInfo deviceInfo, ViewGroup parent, LayoutInflater inflater) {
                View view = inflater.inflate(R.layout.item_client_edit, parent, false);
                ((TextView)view.findViewById(R.id.text_caption)).setText(deviceInfo.getAlias(getResources()));
                ((TextView)view.findViewById(R.id.text_description)).setText(deviceInfo.getDescription(getResources()));
                view.findViewById(R.id.action_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dashboard().dialog_editDeviceAlias(deviceInfo);
                    }
                });
                return view;
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        mStaticIpClientObserver = new Data.DataChangeObserver<List<DeviceInfo>>() {
            @Override
            public void onDataInvalid() {
                fetch_staticIpClients();
            }
            @Override
            public void onData(List<DeviceInfo> ipReservationAliases) {}
        };
        application().data_devicesInfo.addDataChangeObserver(mStaticIpClientObserver);
        fetch_staticIpClients();
    }

    @Override
    public void onStop() {
        super.onStop();
        application().data_devicesInfo.removeDataChangeObserver(mStaticIpClientObserver);
    }

    private void fetch_staticIpClients() {
        showLoading();
        application().data_devicesInfo.fetch(true, new Data.FetchObserver<List<DeviceInfo>>() {
            @Override
            public void onFetch(List<DeviceInfo> deviceInfoList) {
                showContent();
                mSingleClientPresenter.updateUI(deviceInfoList);
            }

            @Override
            public void onError(Data.FetchError fetchError) {
                handleFetchError(fetchError);
            }
        });
    }

}
