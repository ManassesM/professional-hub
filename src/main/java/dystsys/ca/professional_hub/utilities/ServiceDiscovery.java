package dystsys.ca.professional_hub.utilities;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import dystsys.ca.professional_hub.records.DiscoveredService;

public class ServiceDiscovery {
	public static DiscoveredService findService(String type) {
		final DiscoveredService[] result = new DiscoveredService[1];
		CountDownLatch latch = new CountDownLatch(1);

		try {
			JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

			jmdns.addServiceListener(type, new ServiceListener() {

				@Override
				public void serviceAdded(ServiceEvent event) {
					jmdns.requestServiceInfo(event.getType(), event.getName());
				}

				@Override
				public void serviceResolved(ServiceEvent event) {
					String host = event.getInfo().getHostAddresses()[0];
					int port = event.getInfo().getPort();
					
					result[0] = new DiscoveredService(host, port);
					latch.countDown();
				}

				@Override
				public void serviceRemoved(ServiceEvent event) {
				}
			});

			latch.await();
			jmdns.close();
			return result[0];

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	} // DiscoveredService
} // ServiceDiscovery
