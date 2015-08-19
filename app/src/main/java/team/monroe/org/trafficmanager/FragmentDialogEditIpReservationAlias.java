package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.monroe.team.android.box.app.ApplicationSupport;

import team.monroe.org.trafficmanager.entities.DeviceAlias;
import team.monroe.org.trafficmanager.entities.DeviceInfo;

public class FragmentDialogEditIpReservationAlias extends FragmentDashboardDialog {

    private DeviceInfo mDeviceInfo;

    @Override
    protected int getDialogContent() {
        return R.layout.dialog_edit_static_ip_alias;
    }

    @Override
    protected String getDialogCaption() {
        return "Edit device alias";
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        visibility_actionClose(true);
        if (savedInstanceState == null){
            savedInstanceState = getArguments();
        }
        mDeviceInfo = (DeviceInfo) savedInstanceState.get(DeviceInfo.class.getName());
        view(R.id.text_mac, TextView.class).setText(mDeviceInfo.ipReservation.mac);
        view(R.id.text_ip, TextView.class).setText(mDeviceInfo.ipReservation.ip);
       // view(R.id.edit_alias, EditText.class).setText(mDeviceInfo.humanName);
        view(R.id.action_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveChanges();
            }
        });
    }

    private void onSaveChanges() {
        String alias = view(R.id.edit_alias, EditText.class).getText().toString();
        if (alias == null || alias.isEmpty()){
            Toast.makeText(getActivity(), "Please specify alias", Toast.LENGTH_SHORT).show();
            return;
        }
        visibility_actionClose(false);
        showLoading();
        application().function_updateDeviceAlias(mDeviceInfo.ipReservation.mac,
                new DeviceAlias(alias, 0),
                new ApplicationSupport.ValueObserver<DeviceAlias>() {
            @Override
            public void onSuccess(DeviceAlias alias) {
                dashboard().dialog_close();
            }

            @Override
            public void onFail(Throwable exception) {
                visibility_actionClose(true);
                handleException(exception);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DeviceInfo.class.getName(), mDeviceInfo);
    }

    @Override
    protected void onDialogClose() {
        dashboard().dialog_close();
    }

}
