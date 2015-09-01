package team.monroe.org.trafficmanager.exceptions;

final public class IssuesCodes {

    public static final int NO_CONFIGURATION = 0;

    public static final int HTTP_BAD_URL = 100;
    public static final int HTTP_NOT_AUTHORIZED = 101;
    public static final int HTTP_NO_ROUTE = 102;
    public static final int HTTP_BAD_BODY = 103;
    public static final int HTTP_GENERAL = 104;

    public static final int INVALID_ENTITY = 201;

    public static boolean isHttpIssue(int issueCode){
        return issueCode >= 100 && issueCode < 201;
    }
}
