package team.monroe.org.trafficmanager.exceptions;

import android.content.res.Resources;

public class RouterExecutionIssue extends Issue{

    public RouterExecutionIssue(String description) {
        super(new IllegalStateException(description));
    }

    @Override
    public int getIssueCode() {
        return IssuesCodes.ROUTER_EXECUTION_ISSUE;
    }

    @Override
    public String getIssueCaption(Resources resources) {
        return "Router execution failed";
    }

    @Override
    public String getIssueDescription(Resources resources) {
        return getCause().getMessage();
    }

}
