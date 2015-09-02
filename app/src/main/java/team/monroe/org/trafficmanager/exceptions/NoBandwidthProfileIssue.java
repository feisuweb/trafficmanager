package team.monroe.org.trafficmanager.exceptions;

import android.content.res.Resources;

public class NoBandwidthProfileIssue extends Issue {

    public NoBandwidthProfileIssue() {
        super(new IllegalStateException("No bandwidth profiles"));
    }

    @Override
    public int getIssueCode() {
        return IssuesCodes.NO_CONFIGURATION_PROFILES;
    }

    @Override
    public String getIssueCaption(Resources resources) {
        return "No bandwidth profiles";
    }

    @Override
    public String getIssueDescription(Resources resources) {
        return "Please create at least one bandwidth profile";
    }
}
