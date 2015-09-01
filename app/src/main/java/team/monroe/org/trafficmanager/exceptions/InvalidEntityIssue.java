package team.monroe.org.trafficmanager.exceptions;

import android.content.res.Resources;

public class InvalidEntityIssue extends Issue {

    public InvalidEntityIssue(Throwable cause) {
        super(cause);
    }

    @Override
    public int getIssueCode() {
        return IssuesCodes.INVALID_ENTITY;
    }

    @Override
    public String getIssueCaption(Resources resources) {
        return "Invalid input data";
    }

    @Override
    public String getIssueDescription(Resources resources) {
        return getCause().getMessage();
    }
}
