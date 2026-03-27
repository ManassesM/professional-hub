package dystsys.ca.professional_hub.server;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.jmdns.JmDNS;

import dystsys.ca.professional_hub.records.ServiceRegistrationInfo;
import dystsys.ca.professional_hub.services.GuardianServiceImpl;
import dystsys.ca.professional_hub.services.ProductivityServiceImpl;
import dystsys.ca.professional_hub.services.WorkshopServiceImpl;
import dystsys.ca.professional_hub.utilities.AppConfig;
import dystsys.ca.professional_hub.utilities.ServiceRegistration;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class ServerGRPC {

	public static void main(String[] args) {

		WorkshopServiceImpl workshopService = new WorkshopServiceImpl();
		ProductivityServiceImpl productivityService = new ProductivityServiceImpl();
		GuardianServiceImpl guardianServiceImpl = new GuardianServiceImpl();

		try {
			int port = AppConfig.SERVER_PORT;

			Server server = ServerBuilder.forPort(port).addService(workshopService).addService(productivityService)
					.addService(guardianServiceImpl).build();
			server.start();

			// ******************************jmDNS registration
			List<ServiceRegistrationInfo> services = List.of(
					new ServiceRegistrationInfo("GuardianService", "_guardian._tcp.local.", 50051),
					new ServiceRegistrationInfo("ProductivityService", "_productivity._tcp.local.", 50051),
					new ServiceRegistrationInfo("WorkshopService", "_workshop._tcp.local.", 50051));

			System.out.printf("Server listening on port: %d%n", port);
			final JmDNS jmdns = ServiceRegistration.registerServices(services); // final so we can use it inside Lambda

			// ******************************shutdown hook
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				System.out.println("jmDNS is shutting down...");
				if (jmdns != null) {
					jmdns.unregisterAllServices();
					try {
						jmdns.close();
						System.out.println("jmDNS services unregistered.");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				System.out.println("gRPC server is shutting down...");
				server.shutdown(); // waits for tasks to finish before shutting it down
				try {
					if (!server.awaitTermination(30, TimeUnit.SECONDS)) { // waits for 30 more seconds
						server.shutdownNow(); // forces server to shutdown
						System.out.println("Forceful server shutdown.");
					}
				} catch (InterruptedException e) {
					server.shutdownNow();
				}
				System.out.println("Server stopped.");
			}));
			server.awaitTermination(); // waits for tasks to finish
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	} // main
} // ServerGRPC