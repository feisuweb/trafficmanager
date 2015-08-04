package team.monroe.org.trafficmanager.manage;

import org.monroe.team.android.box.services.HttpManager;
import org.monroe.team.corebox.utils.Closure;

import java.io.IOException;

import team.monroe.org.trafficmanager.entities.ConnectionConfiguration;
import team.monroe.org.trafficmanager.exceptions.HttpIssue;
import team.monroe.org.trafficmanager.exceptions.IssuesCodes;

public class RouterManager {

    private final HttpManager httpManager;

    public RouterManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public void checkOnline(ConnectionConfiguration configuration){
        doGetRequest(configuration.buildUrl(null),configuration.user, configuration.password);
    }

    private String doGetRequest(String urlCommand, String user, String pass) {
        String body;
        try {
            HttpManager.Response<String> response = httpManager.get(urlCommand,
                    HttpManager.details().basicAuth(user, pass),
                    HttpManager.response_text());
            if (response.statusCode == 401){
                throw new HttpIssue(null, IssuesCodes.HTTP_NOT_AUTHORIZED);
            }
            if (response.statusCode > 299){
                //success
                throw new HttpIssue(new IllegalStateException("Response status ["+response.statusCode+"] "+response.statusMessage), IssuesCodes.HTTP_GENERAL);
            }
            body = response.body;
        } catch (HttpManager.InvalidBodyFormatException e) {
          throw new HttpIssue(e, IssuesCodes.HTTP_BAD_BODY);
        } catch (HttpManager.BadUrlException e){
          throw new HttpIssue(e, IssuesCodes.HTTP_BAD_URL);
        }catch (HttpManager.NoRouteToHostException e){
          throw new HttpIssue(e, IssuesCodes.HTTP_NO_ROUTE);
        } catch (IOException e) {
          throw new HttpIssue(e, IssuesCodes.HTTP_GENERAL);
        }
        return body;
    }


}
