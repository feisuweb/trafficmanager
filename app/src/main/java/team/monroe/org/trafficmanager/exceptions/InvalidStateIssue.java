package team.monroe.org.trafficmanager.exceptions;

import android.content.res.Resources;

public class InvalidStateIssue  extends Issue{

    public InvalidStateIssue(String msg) {
        super(new IllegalStateException(msg));
    }

    @Override
    public int getIssueCode() {
        return IssuesCodes.INVALID_STATE;
    }

    @Override
    public String getIssueCaption(Resources resources) {
        return "Huston, we have a problem !";
    }

    @Override
    public String getIssueDescription(Resources resources) {
        return getCause().getMessage();
    }
}
