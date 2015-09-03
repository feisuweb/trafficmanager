package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.data.Data;

import team.monroe.org.trafficmanager.entities.BandwidthProfile;
import team.monroe.org.trafficmanager.entities.DeviceInfo;
import team.monroe.org.trafficmanager.exceptions.IssuesCodes;

public class FragmentDialogExecute extends FragmentDashboardDialog {

    @Override
    protected int getDialogContent() {
        return R.layout.dialog_execute;
    }

    @Override
    protected String getDialogCaption() {
        return "Execute in progress";
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        visibility_actionClose(false);
        showLoading();
    }

    @Override
    public void onResume() {
        super.onResume();
        application().getPendingExecution().fetch(true, new Data.FetchObserver<Boolean>() {
            @Override
            public void onFetch(Boolean aBoolean) {
                        dashboard().dialog_close();
               }

            @Override
            public void onError(Data.FetchError fetchError) {
                handleFetchError(fetchError);
            }
        });
    }

    @Override
    protected String customizeIssueActionText(int issueCode) {
        return "Close";
    }

    @Override
    protected boolean onIssueAction(int issueCode) {
        dashboard().dialog_close();
        return true;
    }

    @Override
    protected void onDialogClose() {}

}
