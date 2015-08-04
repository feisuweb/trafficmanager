package team.monroe.org.trafficmanager.exceptions;

import android.content.res.Resources;

public abstract class Issue extends RuntimeException{

    public Issue(Throwable cause) {
        super(cause);
    }

    public abstract int getIssueCode();
    public abstract String getIssueCaption(Resources resources);
    public abstract String getIssueDescription(Resources resources);

}
