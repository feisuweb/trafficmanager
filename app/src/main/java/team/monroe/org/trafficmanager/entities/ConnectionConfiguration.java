package team.monroe.org.trafficmanager.entities;

import org.monroe.team.corebox.utils.P;

import java.io.Serializable;

public class ConnectionConfiguration implements Serializable {

    public final String user;
    public final String password;
    public final String host;
    public final String port;

    public ConnectionConfiguration(String user, String password, String host, String port) {
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    public String buildUrl(String uri, P<String,Object>... args) {
        StringBuilder builder = new StringBuilder("http://");
        builder.append(host).append(":").append(port);
        builder.append(uri == null ? "" : "/" + uri);
        if (args != null && args.length > 0){
            builder.append("?");
            for (P<String, Object> arg : args) {
                builder.append(arg.first).append("=").append(arg.second).append("&");
            }
            builder.deleteCharAt(builder.length()-1);
        }
        return builder.toString();
    }
}
