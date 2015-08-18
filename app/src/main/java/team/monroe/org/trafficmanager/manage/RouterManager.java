package team.monroe.org.trafficmanager.manage;

import org.monroe.team.android.box.services.HttpManager;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.P;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import team.monroe.org.trafficmanager.entities.ConnectionConfiguration;
import team.monroe.org.trafficmanager.exceptions.HttpIssue;
import team.monroe.org.trafficmanager.exceptions.IssuesCodes;

public class RouterManager {

    private final HttpManager httpManager;
    private static final Pattern pattern_topLevelDhcpList = Pattern.compile(".*var *dhcpList *= *new *Array *\\(([^)]*).*",
            Pattern.MULTILINE|Pattern.CASE_INSENSITIVE|Pattern.DOTALL);

    public RouterManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public List<DhcpReservedIpDetail> dhcpIpReservationList(ConnectionConfiguration configuration){
        List<DhcpReservedIpDetail> answer = new ArrayList<>();
        int page = 1;
        while (true){
            String pageText = doGetRequest(configuration.buildUrl("userRpm/FixMapCfgRpm.htm", new P<String, Object>("Page", page)),configuration.user, configuration.password);
            Matcher matcher = pattern_topLevelDhcpList.matcher(pageText);
            matcher.matches();
            String dhcpListDataString  = matcher.group(1);
            String[] splitDhcpList = dhcpListDataString.split(",");
            for (int i =0; i < splitDhcpList.length - 3; i+=3){
                String mac = splitDhcpList[i].replace('"',' ').trim();
                String ip = splitDhcpList[i+1].replace('"',' ').trim();
                boolean enabled = splitDhcpList[i+2].trim().equals("1");
                DhcpReservedIpDetail ipDetails = new DhcpReservedIpDetail(mac, ip, enabled);
                if (answer.indexOf(ipDetails) != -1) return answer;
                answer.add(ipDetails);
            }
            page++;
        }
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

    public static class DhcpReservedIpDetail{

        public final String mac;
        public final String ip;
        public final boolean enabled;

        public DhcpReservedIpDetail(String mac, String ip, boolean enabled) {
            this.mac = mac;
            this.ip = ip;
            this.enabled = enabled;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DhcpReservedIpDetail)) return false;

            DhcpReservedIpDetail that = (DhcpReservedIpDetail) o;

            if (enabled != that.enabled) return false;
            if (!ip.equals(that.ip)) return false;
            if (!mac.equals(that.mac)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = mac.hashCode();
            result = 31 * result + ip.hashCode();
            result = 31 * result + (enabled ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "DhcpReservedIpDetail{" +
                    "mac='" + mac + '\'' +
                    ", ip='" + ip + '\'' +
                    ", enabled=" + enabled +
                    '}';
        }
    }

}
