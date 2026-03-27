package dystsys.ca.professional_hub.utilities;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import dystsys.ca.professional_hub.records.ServiceRegistrationInfo;

public class ServiceRegistration {

	public static JmDNS registerServices(List<ServiceRegistrationInfo> services) {
		try {

			JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

			// loop through and register services
			for (ServiceRegistrationInfo s : services) {
				ServiceInfo info = ServiceInfo.create(s.type(), s.name(), s.port(), "Professional Hub");
				jmdns.registerService(info);
				System.out.printf("Registered: %s | Port: %d%n", s.name(), s.port());
			}

			return jmdns;
		} catch (IOException e) {
			System.err.println("jmDNS error during initialization: " + e.getMessage());
			return null;
		}
	}
} // ServiceRegistration
