package team.monroe.org.trafficmanager.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.ArrayList;
import java.util.List;

import team.monroe.org.trafficmanager.entities.ConnectionConfiguration;
import team.monroe.org.trafficmanager.entities.IpReservation;
import team.monroe.org.trafficmanager.exceptions.NoConfigurationIssue;
import team.monroe.org.trafficmanager.manage.ObjectManager;
import team.monroe.org.trafficmanager.manage.RouterManager;

public class IpReservationGetAll extends UserCaseSupport<Void, List<IpReservation>> {

    public IpReservationGetAll(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected List<IpReservation> executeImpl(Void request) {
        ConnectionConfiguration configuration = using(ObjectManager.class).getConnectionConfiguration();
        if (configuration == null) {
            throw new NoConfigurationIssue();
        }
        List<RouterManager.DhcpReservedIpDetail> dhcpReservedIpDetailList = using(RouterManager.class).dhcpIpReservationList(configuration);
        List<IpReservation> answer = new ArrayList<>();
        for (RouterManager.DhcpReservedIpDetail dhcpReservedIpDetail : dhcpReservedIpDetailList) {
            if (dhcpReservedIpDetail.enabled){
                IpReservation ipClient = new IpReservation(dhcpReservedIpDetail.ip, dhcpReservedIpDetail.mac);
                answer.add(ipClient);
            }
        }
        return answer;
    }
}
