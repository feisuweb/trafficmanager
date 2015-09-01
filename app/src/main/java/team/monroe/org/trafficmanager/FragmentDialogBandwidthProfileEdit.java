package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.monroe.team.android.box.app.ApplicationSupport;

import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.entities.DeviceInfo;
import team.monroe.org.trafficmanager.exceptions.IssuesCodes;

public class FragmentDialogBandwidthProfileEdit extends FragmentDashboardDialog {

    private BandwidthProfile mProfile;

    @Override
    protected int getDialogContent() {
        return R.layout.dialog_edit_bandwidth_profile;
    }

    @Override
    protected String getDialogCaption() {
        return "Bandwidth Profile";
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        visibility_actionClose(true);
        if (savedInstanceState == null){
            savedInstanceState = getArguments();
        }
        mProfile = (BandwidthProfile) savedInstanceState.get(BandwidthProfile.class.getName());
        if (mProfile != null){
            view(R.id.edit_title, TextView.class).setText(mProfile.title);
            view(R.id.edit_description, TextView.class).setText(mProfile.description);
            view(R.id.edit_in_speed, TextView.class).setText(String.valueOf(mProfile.inLimit));
            view(R.id.edit_out_speed, TextView.class).setText(String.valueOf(mProfile.outLimit));
        }
        view(R.id.action_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveChanges();
            }
        });

    }

    private void onSaveChanges() {
        String title = view(R.id.edit_title, TextView.class).getText().toString();
        String description = view(R.id.edit_description, TextView.class).getText().toString();
        if (title.isEmpty()){
            Toast.makeText(getActivity(), "Please specify profile title", Toast.LENGTH_SHORT).show();
            return;
        }

        String inLimit = view(R.id.edit_in_speed, TextView.class).getText().toString();
        if (inLimit.isEmpty()) inLimit = "0";
        String outLimit = view(R.id.edit_out_speed, TextView.class).getText().toString();
        if (inLimit.isEmpty()) inLimit = "0";

        visibility_actionClose(false);
        showLoading();
        BandwidthProfile bandwidthProfile = new BandwidthProfile(title, description, Integer.parseInt(inLimit), Integer.parseInt(outLimit));
        application().function_addBandwidthProfile(bandwidthProfile,
                new ApplicationSupport.ValueObserver<BandwidthProfile>() {
                    @Override
                    public void onSuccess(BandwidthProfile alias) {
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
        outState.putSerializable(DeviceInfo.class.getName(), mProfile);
    }

    @Override
    protected void onDialogClose() {
        dashboard().dialog_close();
    }

    @Override
    protected String customizeIssueActionText(int issueCode) {
        if (issueCode == IssuesCodes.INVALID_ENTITY){
            return "Edit profile";
        }
        return super.customizeIssueActionText(issueCode);
    }

    @Override
    protected boolean onIssueAction(int issueCode) {
        if (issueCode == IssuesCodes.INVALID_ENTITY){
            showContent();
            return true;
        }
        return super.onIssueAction(issueCode);
    }
}
