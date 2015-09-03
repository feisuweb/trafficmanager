package team.monroe.org.trafficmanager.manage;

import android.content.res.Configuration;

import org.monroe.team.android.box.services.HttpManager;
import org.monroe.team.corebox.log.L;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.P;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import team.monroe.org.trafficmanager.entities.BandwidthLimitRule;
import team.monroe.org.trafficmanager.entities.ConnectionConfiguration;
import team.monroe.org.trafficmanager.entities.ProtocolClass;
import team.monroe.org.trafficmanager.exceptions.HttpIssue;
import team.monroe.org.trafficmanager.exceptions.InvalidStateIssue;
import team.monroe.org.trafficmanager.exceptions.IssuesCodes;
import team.monroe.org.trafficmanager.exceptions.RouterExecutionIssue;

public class RouterManager {

    private final HttpManager httpManager;
    private static final Pattern pattern_topLevelDhcpList = Pattern.compile(".*var *dhcpList *= *new *Array *\\(([^)]*).*",
            Pattern.MULTILINE|Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    private static final Pattern pattern_topLevelBandwidthLimitRules = Pattern.compile(".*var *QoSRuleListArray *= *new *Array *\\(([^)]*).*",
            Pattern.MULTILINE|Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    //var errCode = "27009";
    private static final Pattern pattern_topLevelError = Pattern.compile(".*var *errCode *= *\"([^\"]*).*",
            Pattern.MULTILINE|Pattern.CASE_INSENSITIVE|Pattern.DOTALL);

    private String mLocalizationErrorCodesPageContent = null;

    public RouterManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public List<DhcpReservedIpDetail> dhcpIpReservationList(ConnectionConfiguration configuration){
        List<DhcpReservedIpDetail> answer = new ArrayList<>();
        int page = 1;
        while (true){
            String pageText = doGetRequest(configuration.buildUrl("userRpm/FixMapCfgRpm.htm", new P<String, Object>("Page", page)),configuration.user, configuration.password);

            checkIfResultIsSuccess(configuration,pageText);

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

    public List<BandwidthLimitRule> bandwidthLimitRules(ConnectionConfiguration configuration) {
        List<BandwidthLimitRule> answer = new ArrayList<>();
        int page = 1;
        HashSet<String> ruleIdSet = new HashSet<>();
        while (true){
            String pageText = doGetRequest(
                    configuration.buildUrl("userRpm/QoSRuleListRpm.htm",
                    new P<String, Object>("Page", page)),
                    configuration.user, configuration.password);
            checkIfResultIsSuccess(configuration,pageText);
            Matcher matcher = pattern_topLevelBandwidthLimitRules.matcher(pageText);
            matcher.matches();
            String bandwidthLimitRulesString  = matcher.group(1);
            String[] itParsed = bandwidthLimitRulesString.split(",");
            /*
            101, "192.168.0.5 - 192.168.0.6/1 - 65535/TCP", 0, 1, 0, 1, 0, 1,
            65637, "192.168.0.10/1 - 60000", 0, 1, 0, 1, 0, 0,
                                                 out     in
            131173, "192.168.0.21/1 - 65535", 0, 64, 32, 65, 33, 0,
             */
            for (int i =0; i < itParsed.length - 8; i+=8){
                String id = itParsed[i].trim();
                if (ruleIdSet.contains(id)) return answer;
                ruleIdSet.add(id);
                String[] ipPortProtocolString = itParsed[i+1]
                        .replace('"',' ')
                        .replaceAll(" +","")
                        .trim().split("/");
                String[] ips = splitIPs(ipPortProtocolString[0]);
                int[] ports;
                ProtocolClass protocol;
                if (ipPortProtocolString.length > 1){
                    protocol = splitProtocol(ipPortProtocolString[1]);
                    if(protocol == null){
                        ports = splitPorts(ipPortProtocolString[1]);
                        if (ipPortProtocolString.length > 2){
                            protocol = splitProtocol(ipPortProtocolString[2]);
                        }else {
                            protocol = ProtocolClass.ALL;
                        }
                    }else {
                        ports = new int[]{0,0};
                    }
                }else {
                    protocol = ProtocolClass.ALL;
                    ports = new int[]{0,0};
                }

                int outMax = Integer.parseInt(itParsed[i+3].trim());
                int outMin = Integer.parseInt(itParsed[i+4].trim());
                int inMax = Integer.parseInt(itParsed[i+5].trim());
                int inMin = Integer.parseInt(itParsed[i+6].trim());
                boolean enabled = "1".equals(itParsed[i+7].trim());
                answer.add(new BandwidthLimitRule(id,ips[0],ips[1],ports[0],ports[1],enabled,inMax,inMin,outMax, outMin, protocol));
            }
            page++;
        }
    }

    private int[] splitPorts(String portString) {
        String[] ipParsed = portString.split("-");
        if (ipParsed.length == 2){
            return new int[]{Integer.parseInt(ipParsed[0]),Integer.parseInt(ipParsed[1])};
        }else {
            return new int[]{Integer.parseInt(ipParsed[0]),Integer.parseInt(ipParsed[0])};
        }
    }

    private ProtocolClass splitProtocol(String protocolString) {
        if (protocolString.toUpperCase().equals(ProtocolClass.TCP.name())){
            return ProtocolClass.TCP;
        }else if (protocolString.toUpperCase().equals(ProtocolClass.UDP.name())){
            return ProtocolClass.UDP;
        }else {
            return null;
        }
    }

    private String[] splitIPs(String ipString) {
        String[] ipParsed = ipString.split("-");
        if (ipParsed.length == 2){
            return new String[]{ipParsed[0],ipParsed[1]};
        }else {
            return new String[]{ipParsed[0],ipParsed[0]};
        }
    }

    public void updateBandwidthLimitRule(ConnectionConfiguration configuration, String id, String startIp, String endIp, int startPort, int endPort, int inLimit, int outLimit) {
        String pageText = doGetRequest(
                configuration.buildUrl("userRpm/QoSRuleListRpm.htm",
                        new P<String, Object>("start_ip_addr", startIp),
                        new P<String, Object>("end_ip_addr", endIp),
                        new P<String, Object>("start_port", startPort),
                        new P<String, Object>("end_port", endPort),
                        new P<String, Object>("protocol", 0),
                        new P<String, Object>("min_up_band_width",outLimit),
                        new P<String, Object>("max_up_band_width", outLimit),
                        new P<String, Object>("min_down_band_width", inLimit),
                        new P<String, Object>("max_down_band_width", inLimit),
                        new P<String, Object>("curEditId", id),
                        new P<String, Object>("enable", "true"),
                        new P<String, Object>("Page", 1)
                ),
                configuration.user, configuration.password);
        checkIfResultIsSuccess(configuration, pageText);
    }

    private void checkIfResultIsSuccess(ConnectionConfiguration configuration, String pageText) {
        Matcher matcher = pattern_topLevelError.matcher(pageText);
        if (matcher.matches()){
            String errorCode  = matcher.group(1);
            String humanDescription = convertToHumanDescription(configuration, errorCode);
            throw new RouterExecutionIssue(humanDescription);
        }
    }

    private synchronized String convertToHumanDescription(ConnectionConfiguration configuration, String errorCode) {

        String fallbackErrorDescription = "Error code = "+errorCode;
        if (mLocalizationErrorCodesPageContent == null){
            try {
            mLocalizationErrorCodesPageContent = doGetRequest(
                    configuration.buildUrl("localiztion/str_err.js"),
                    configuration.user, configuration.password);
            }catch (Throwable e){
                L.DEBUG.w("Couldn`t fetch error codes description", e);
                mLocalizationErrorCodesPageContent = "";
            }
        }

        if (!mLocalizationErrorCodesPageContent.isEmpty()){
            Matcher matcher = Pattern.compile(".*var *([^ ]*) *= *"+errorCode+".*", Pattern.DOTALL|Pattern.MULTILINE)
                    .matcher(mLocalizationErrorCodesPageContent);
            if (matcher.matches()){
                String errorCodeShort = matcher.group(1).trim();

                matcher = Pattern.compile(".*str_err."+errorCodeShort+".[ \\t]*=[ \\t]*\"([^\"]*).*", Pattern.DOTALL|Pattern.MULTILINE)
                        .matcher(mLocalizationErrorCodesPageContent);
                if (matcher.matches()){
                    return matcher.group(1);
                }else {
                    return "Error short = "+errorCodeShort;
                }
            }
        }
        return fallbackErrorDescription;
    }

    public void disableBandwidthLimitRule(ConnectionConfiguration configuration, String id) {
        String pageText = doGetRequest(
                configuration.buildUrl("userRpm/QoSRuleListRpm.htm",
                        new P<String, Object>("enableId", id),
                        new P<String, Object>("enable", "false"),
                        new P<String, Object>("Page", 1)
                ),
                configuration.user, configuration.password);
        checkIfResultIsSuccess(configuration, pageText);
    }

    public void deleteBandwidthLimitRule(ConnectionConfiguration configuration, String id) {
        String pageText = doGetRequest(
                configuration.buildUrl("userRpm/QoSRuleListRpm.htm",
                        new P<String, Object>("Del", id),
                        new P<String, Object>("Page", 1)
                ),
                configuration.user, configuration.password);
        checkIfResultIsSuccess(configuration, pageText);
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
