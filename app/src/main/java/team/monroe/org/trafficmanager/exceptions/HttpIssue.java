package team.monroe.org.trafficmanager.exceptions;

import android.content.res.Resources;

public class HttpIssue extends Issue{

    private final int issueCode;

    public HttpIssue(Throwable cause, int issueCode) {
        super(cause);
        this.issueCode = issueCode;
    }

    @Override
    public int getIssueCode() {
        return issueCode;
    }

    @Override
    public String getIssueCaption(Resources resources) {
        return "Http issue "+issueCode;
    }

    @Override
    public String getIssueDescription(Resources resources) {
        return "There is no description for issue "+issueCode;
    }
}
