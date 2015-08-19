package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;

import team.monroe.org.trafficmanager.entities.DeviceAlias;
import team.monroe.org.trafficmanager.entities.DeviceInfo;
import team.monroe.org.trafficmanager.entities.DeviceType;

public class FragmentDialogAliasEdit extends FragmentDashboardDialog {

    private DeviceInfo mDeviceInfo;
    private Spinner mSpinnerDeviceType;
    private GenericListViewAdapter<DeviceType, GetViewImplementation.ViewHolder<DeviceType>> mDeviceTypeAdapter;

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
        mSpinnerDeviceType = view(R.id.spinner_device_type, Spinner.class);
        mDeviceTypeAdapter = new GenericListViewAdapter<DeviceType, GetViewImplementation.ViewHolder<DeviceType>>(
                getActivity(),
                new GetViewImplementation.ViewHolderFactory<GetViewImplementation.ViewHolder<DeviceType>>() {
                    @Override
                    public GetViewImplementation.ViewHolder<DeviceType> create(final View convertView) {
                        return new GetViewImplementation.GenericViewHolder<DeviceType>() {

                            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
                            TextView captionView = (TextView) convertView.findViewById(R.id.text_caption);

                            @Override
                            public void update(DeviceType deviceType, int position) {
                                imageView.setImageResource(deviceType.drawableId);
                                captionView.setText(deviceType.title);
                            }
                        };
                    }
                },
                R.layout.item_device_type);
        mSpinnerDeviceType.setAdapter(mDeviceTypeAdapter);
        mDeviceTypeAdapter.addAll(DeviceType.values());
        mDeviceTypeAdapter.notifyDataSetChanged();
        if (mDeviceInfo.deviceAlias == null){
            mSpinnerDeviceType.setSelection(0);
        }else {
            mSpinnerDeviceType.setSelection(mDeviceInfo.deviceAlias.icon);
            view(R.id.edit_alias, EditText.class).setText(mDeviceInfo.deviceAlias.alias);
        }
    }

    private void onSaveChanges() {
        String alias = view(R.id.edit_alias, EditText.class).getText().toString();
        if (alias == null || alias.isEmpty()){
            Toast.makeText(getActivity(), "Please specify alias", Toast.LENGTH_SHORT).show();
            return;
        }
        visibility_actionClose(false);
        showLoading();
        int selected = mSpinnerDeviceType.getSelectedItemPosition();
        application().function_updateDeviceAlias(mDeviceInfo.ipReservation.mac,
                new DeviceAlias(alias, selected),
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
