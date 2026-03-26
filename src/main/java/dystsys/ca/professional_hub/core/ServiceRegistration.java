package dystsys.ca.professional_hub.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class ServiceRegistration {

	public static void registerServices(List<ServiceRegistrationInfo> services) {
		try {

			JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

			// loop through and register services
			for (ServiceRegistrationInfo s : services) {
				ServiceInfo info = ServiceInfo.create(s.type(), s.name(), 50051, "Professional Hub");
				jmdns.registerService(info);
				System.out.printf("Registered: %s | Port: %d", s.name(), s.port());
			}

			// shutdown hook
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				System.out.println("Shutting down jmDNS...");

				jmdns.unregisterAllServices(); // first unregister services
				try {
					jmdns.close(); // close connection
				} catch (IOException e) {
					e.printStackTrace();
				}
			}));
		} catch (IOException e) {
			System.err.println("jmDNS error during initialization: " + e.getMessage());
		}
	}
} // ServiceRegistration
