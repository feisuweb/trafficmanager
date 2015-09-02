package team.monroe.org.trafficmanager;

import android.os.Bundle;

import org.monroe.team.android.box.app.FragmentSupport;
import org.monroe.team.android.box.data.Data;

import team.monroe.org.trafficmanager.exceptions.IssuesCodes;
import team.monroe.org.trafficmanager.view.ContentPanelController;
import team.monroe.org.trafficmanager.view.MyScrollView;

public abstract class FragmentDashboardContentSupport extends FragmentDashboardSupport{

    private ContentPanelController mContentPanelController;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContentPanelController = new ContentPanelController(getActivity()) {
            @Override
            protected String customIssueActionText(int issueCode) {
                return customizeIssueActionText(issueCode);
            }

            @Override
            protected int customIssueImageResource(int issueCode) {
                return customizeIssueImageResource(issueCode);
            }

            @Override
            protected boolean issueAction(int issueCode) {
                return onIssueAction(issueCode);
            }
        };
        mContentPanelController.onCreated(getFragmentView());
        mContentPanelController.showContent();
    }

    final public void handleFetchError(Data.FetchError fetchError){
        mContentPanelController.handleFetchError(fetchError);
    }

    public void handleException(Throwable exception) {
        mContentPanelController.handleException(exception);
    }


    protected String customizeIssueActionText(int issueCode) {
        switch (issueCode){
            case IssuesCodes.HTTP_BAD_URL:
            case IssuesCodes.HTTP_NOT_AUTHORIZED:
            case IssuesCodes.NO_CONFIGURATION: return "Change router settings";
            case IssuesCodes.NO_CONFIGURATION_PROFILES: return "Create Profile";
        }
        return null;
    }

    protected int customizeIssueImageResource(int issueCode) {
        switch (issueCode){
            case IssuesCodes.NO_CONFIGURATION_PROFILES:
            case IssuesCodes.NO_CONFIGURATION: return R.drawable.android_build_big;
        }
        if (IssuesCodes.isHttpIssue(issueCode)) return R.drawable.android_http_big;
        return 0;
    }

    protected boolean onIssueAction(int issueCode){
        switch (issueCode){
            case IssuesCodes.NO_CONFIGURATION:
                initiateRouterConfiguration();
                return true;
            case IssuesCodes.NO_CONFIGURATION_PROFILES:
                dashboard().dialog_editBandwidthProfile(null);
                return true;
            case IssuesCodes.HTTP_NOT_AUTHORIZED:
                initiateRouterConfiguration();
                return true;
            case IssuesCodes.HTTP_BAD_URL:
                initiateRouterConfiguration();
                return true;
        }
        return false;
    }

    private void initiateRouterConfiguration() {
        dashboard().open_routerConfiguration();
    }

    final public void showIssue(ContentPanelController.IssueRequest request) {
        mContentPanelController.showIssue(request);
    }

    final public void showLoading() {
        mContentPanelController.showLoading();
    }

    final public void showContent() {
        mContentPanelController.showContent();
    }
}
