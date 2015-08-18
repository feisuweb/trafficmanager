package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.monroe.team.android.box.data.Data;

import java.util.List;

import team.monroe.org.trafficmanager.entities.StaticIpClient;
import team.monroe.org.trafficmanager.poc.ListPanelPresenter;

public class FragmentBodyPageClients extends FragmentBodyPageDefault {

    private Data.DataChangeObserver<List<StaticIpClient>> mStaticIpClientObserver;
    private ListPanelPresenter<StaticIpClient> mSingleClientPresenter;

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
        mSingleClientPresenter = new ListPanelPresenter<StaticIpClient>(
                activity().getLayoutInflater(),
                view(R.id.panel_single_instance_client, ViewGroup.class),
                viewResolver_singleClientListView(),
                viewResolver_defaultNoItems());
    }

    private ListPanelPresenter.DataViewResolver<StaticIpClient> viewResolver_singleClientListView() {
        return new ListPanelPresenter.DataViewResolver<StaticIpClient>() {
            @Override
            public View build(StaticIpClient staticIpClient, ViewGroup parent, LayoutInflater inflater) {
                View view = inflater.inflate(R.layout.item_default, parent, false);
                return view;
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        mStaticIpClientObserver = new Data.DataChangeObserver<List<StaticIpClient>>() {
            @Override
            public void onDataInvalid() {
                fetch_staticIpClients();
            }
            @Override
            public void onData(List<StaticIpClient> staticIpClients) {}
        };
        application().data_staticIpClients.addDataChangeObserver(mStaticIpClientObserver);
        fetch_staticIpClients();
    }

    @Override
    public void onStop() {
        super.onStop();
        application().data_staticIpClients.removeDataChangeObserver(mStaticIpClientObserver);
    }

    private void fetch_staticIpClients() {
        application().data_staticIpClients.fetch(true, new Data.FetchObserver<List<StaticIpClient>>() {
            @Override
            public void onFetch(List<StaticIpClient> staticIpClients) {
                ui_updateClientList(staticIpClients);
            }

            @Override
            public void onError(Data.FetchError fetchError) {
                handleFetchError(fetchError);
            }
        });
    }

    private void ui_updateClientList(List<StaticIpClient> staticIpClients) {
        mSingleClientPresenter.updateUI(staticIpClients);
    }
}
