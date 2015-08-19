package team.monroe.org.trafficmanager.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.monroe.team.android.box.data.Data;
import org.monroe.team.corebox.log.L;

import team.monroe.org.trafficmanager.R;
import team.monroe.org.trafficmanager.exceptions.Issue;

public abstract class ContentPanelController {

    private final Context mContext;
    private View mPanelContent;
    private View mPanelLoading;
    private View mPanelIssue;
    private State mState = null;
    private IssueRequest mIssueRequest;
    private Button mActionIssue;
    private TextView mTextIssueCaption;
    private TextView mTextIssueDescription;
    private ImageView mImageIssue;

    public ContentPanelController(Context context) {
        this.mContext = context;
    }

    final public void onCreated(View rootContentView) {
        mPanelContent = rootContentView.findViewById(R.id.panel_page_content);
        mPanelLoading = rootContentView.findViewById(R.id.panel_loading);
        mPanelIssue = rootContentView.findViewById(R.id.panel_issue);

        rootContentView.findViewById(R.id.action_issue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean supported = issueAction(mIssueRequest.errorCode);
                if (!supported){
                    Toast.makeText(mContext,
                            "Upps, seems nothing can be done with this issue [" + mIssueRequest.errorCode + "]. Please contact support team.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mActionIssue = (Button) rootContentView.findViewById(R.id.action_issue);
        mTextIssueCaption = (TextView) rootContentView.findViewById(R.id.text_issue_caption);
        mTextIssueDescription = (TextView) rootContentView.findViewById(R.id.text_issue_description);
        mImageIssue = (ImageView) rootContentView.findViewById(R.id.issue_icon);
    }

    final public void handleFetchError(Data.FetchError fetchError){
        if (fetchError instanceof Data.ExceptionFetchError){
            Throwable exception = ((Data.ExceptionFetchError) fetchError).cause;
            handleException(exception);
        }else {
            showIssue(new IssueRequest(
                    -1,
                    getIssueImageResource(-1),
                    "Upps something goes wrong",
                    fetchError.message(),
                    getIssueActionText(-1)));
        }
    }

    public void handleException(Throwable exception) {
        L.w("UI", "Exception handled", exception);
        if (exception instanceof Issue){
            //TODO: Something here
            int issueCode = ((Issue) exception).getIssueCode();
            showIssue(new IssueRequest(
                    ((Issue) exception).getIssueCode(),
                    getIssueImageResource(issueCode),
                    ((Issue) exception).getIssueCaption(mContext.getResources()),
                    ((Issue) exception).getIssueDescription(mContext.getResources()),
                    getIssueActionText(issueCode)));
        } else {
            showIssue(new IssueRequest(
                    -1,
                    getIssueImageResource(-1),
                    "Upps something goes wrong",
                    exception.getClass().getName()+ "["+exception.getMessage()+"]",
                    getIssueActionText(-1)));
        }
    }

    private String getIssueActionText(int issueCode) {
        String answer = customIssueActionText(issueCode);
        return answer != null? answer: "Try again ...";
    }


    private int getIssueImageResource(int issueCode) {
        int answer = customIssueImageResource(issueCode);
        if (answer != 0) return answer;
        return R.drawable.android_bug_big;
    }


    protected abstract String customIssueActionText(int issueCode);
    protected abstract int customIssueImageResource(int issueCode);
    protected abstract boolean issueAction(int issueCode);


    final public void showIssue(IssueRequest request) {
        if (mState == State.ISSUE && request.sameAs(mIssueRequest)) return;
        mIssueRequest = request;
        mState = State.ISSUE;
        visibility_allGone();
        mActionIssue.setText(request.actionText);
        mTextIssueCaption.setText(request.caption);
        mTextIssueDescription.setText(request.description);
        mImageIssue.setImageResource(request.iconResource);
        mPanelIssue.setVisibility(View.VISIBLE);
    }

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
