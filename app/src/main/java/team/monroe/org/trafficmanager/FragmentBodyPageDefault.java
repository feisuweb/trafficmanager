package team.monroe.org.trafficmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.monroe.team.android.box.data.Data;

import team.monroe.org.trafficmanager.exceptions.Issue;
import team.monroe.org.trafficmanager.exceptions.IssuesCodes;
import team.monroe.org.trafficmanager.view.MyScrollView;

public abstract class FragmentBodyPageDefault extends FragmentDashboardBodyPage {

    private View mTopShadow;
    private MyScrollView mScrollView;

    private View mPanelContent;
    private View mPanelLoading;
    private View mPanelIssue;
    private State mState = null;
    private IssueRequest mIssueRequest;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_page_scrollable_panel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(getPanelLayoutId(), (ViewGroup) view.findViewById(R.id.panel_page_content), true);
        return view;
    }

    protected abstract int getPanelLayoutId();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTopShadow = view(R.id.image_scroll_shadow);
        mScrollView = view(R.id.scroll_view, MyScrollView.class);

        updateShadow(mScrollView.getScrollY());
        mScrollView.mScrollListener = new MyScrollView.OnScrollListener() {
            @Override
            public void onScrollChanged(int left, int top, int oldl, int oldt) {
                updateShadow(top);
            }
        };

        mPanelContent = view(R.id.panel_page_content);
        mPanelLoading = view(R.id.panel_loading);
        mPanelIssue = view(R.id.panel_issue);
        view(R.id.action_issue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean supported = onIssueAction(mIssueRequest.errorCode);
                if (!supported){
                    Toast.makeText(getActivity(),
                            "Upps, seems nothing can be done with this issue ["+mIssueRequest.errorCode+"]. Please contact support team.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        showContent();
    }

    final public void handleFetchError(Data.FetchError fetchError){
        if (fetchError instanceof Data.ExceptionFetchError){
            Throwable exception = ((Data.ExceptionFetchError) fetchError).cause;
            if (exception instanceof Issue){
                //TODO: Something here
                int issueCode = ((Issue) exception).getIssueCode();
                showIssue(new IssueRequest(
                        ((Issue) exception).getIssueCode(),
                        getIssueImageResource(issueCode),
                        ((Issue) exception).getIssueCaption(getResources()),
                        ((Issue) exception).getIssueDescription(getResources()),
                        getIssueActionText(issueCode)));
            } else {
                showIssue(new IssueRequest(
                        -1,
                        getIssueImageResource(-1),
                        "Upps something goes wrong",
                        fetchError.message(),
                        getIssueActionText(-1)));
            }
        }else {
            showIssue(new IssueRequest(
                    -1,
                    getIssueImageResource(-1),
                    "Upps something goes wrong",
                    fetchError.message(),
                    getIssueActionText(-1)));
        }
    }

    private String getIssueActionText(int issueCode) {
        String answer = customizeIssueActionText(issueCode);
        return answer != null? answer: "Try again ...";
    }

    private String customizeIssueActionText(int issueCode) {
        switch (issueCode){
            case IssuesCodes.NO_CONFIGURATION: return "Router Setup";
        }
        return null;
    }

    private int getIssueImageResource(int issueCode) {
        int answer = customizeIssueImageResource(issueCode);
        if (answer != 0) return answer;
        return R.drawable.android_bug_big;
    }

    protected int customizeIssueImageResource(int issueCode) {
        switch (issueCode){
            case IssuesCodes.NO_CONFIGURATION: return R.drawable.android_build_big;
        }
        return 0;
    }

    protected boolean onIssueAction(int issueCode){
        switch (issueCode){
            case IssuesCodes.NO_CONFIGURATION:
                dashboard().open_routerConfiguration();
                return true;
        }
        return false;
    }

    final public void showIssue(IssueRequest request) {
        if (mState == State.ISSUE && request.sameAs(mIssueRequest)) return;
        mIssueRequest = request;
        mState = State.ISSUE;
        visibility_allGone();
        view(R.id.text_issue_caption, TextView.class).setText(request.caption);
        view(R.id.text_issue_description, TextView.class).setText(request.description);
        view(R.id.issue_icon, ImageView.class).setImageResource(request.iconResource);

        mPanelIssue.setVisibility(View.VISIBLE);
    }

    /*
    final public IssueRequest issue_http(int code, String description, String alternativeActionText){
        return new IssueRequest(code, R.drawable.android_http_big, "Router communication issue", description, alternativeActionText);
    }

    final public IssueRequest issue_unknown(String description){
        return new IssueRequest(0, R.drawable.android_bug_big, "Upps something goes wrong", description, null);
    }
    */

    final public void showLoading() {
        if (mState == State.LOADING) return;
        visibility_allGone();
        mState = State.LOADING;
        mPanelLoading.setVisibility(View.VISIBLE);
    }

    final public void showContent() {
        if (mState == State.CONTENT) return;
        visibility_allGone();
        mState = State.CONTENT;
        mPanelContent.setVisibility(View.VISIBLE);
    }

    private void visibility_allGone() {
        mPanelContent.setVisibility(View.GONE);
        mPanelLoading.setVisibility(View.GONE);
        mPanelIssue.setVisibility(View.GONE);
    }

    private void updateShadow(int top) {
        if (top > 0){
            mTopShadow.setVisibility(View.VISIBLE);
        }else {
            mTopShadow.setVisibility(View.INVISIBLE);
        }
    }

    private enum State{
        CONTENT, LOADING, ISSUE
    }

    public final class IssueRequest {

        public final int errorCode;
        public final int iconResource;
        public final String caption;
        public final String description;
        public final String actionText;

        public IssueRequest(int errorCode, int iconResource, String caption, String description, String actionText) {
            this.errorCode = errorCode;
            this.iconResource = iconResource;
            this.caption = caption;
            this.description = description;
            this.actionText = actionText;
        }

        public boolean sameAs(IssueRequest mIssueRequest) {
            if (mIssueRequest == null) return false;
            return mIssueRequest.errorCode == errorCode;
        }
    }
}
