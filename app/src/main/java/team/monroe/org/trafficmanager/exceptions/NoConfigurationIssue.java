package team.monroe.org.trafficmanager.exceptions;

import android.content.res.Resources;

public class NoConfigurationIssue extends Issue {


    public NoConfigurationIssue() {
        super(null);
    }

    @Override
    public int getIssueCode() {
        return IssuesCodes.NO_CONFIGURATION;
    }

    @Override
    public String getIssueCaption(Resources resources) {
        return "Router connection not configured";
    }

    @Override
    public String getIssueDescription(Resources resources) {
        return "Please configure connection to your router";
    }


}
