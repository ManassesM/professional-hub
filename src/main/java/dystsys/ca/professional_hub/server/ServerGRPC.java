package dystsys.ca.professional_hub.server;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dystsys.ca.professional_hub.core.AppConfig;
import dystsys.ca.professional_hub.core.ServiceRegistration;
import dystsys.ca.professional_hub.core.ServiceRegistrationInfo;
import dystsys.ca.professional_hub.services.GuardianServiceImpl;
import dystsys.ca.professional_hub.services.ProductivityServiceImpl;
import dystsys.ca.professional_hub.services.WorkshopServiceImpl;
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

			// jmDNS registration
			List<ServiceRegistrationInfo> services = List.of(
					new ServiceRegistrationInfo("GuardianService", "_guardian._tcp.local.", 50051),
					new ServiceRegistrationInfo("ProductivityService", "_productivity._tcp.local.", 50051),
					new ServiceRegistrationInfo("WorkshopService", "_workshop._tcp.local.", 50051));

			ServiceRegistration.registerServices(services);

			System.out.printf("Server listening on port: %d", port);

			// if server is stopped with tasks running
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				System.out.println("Server is shutting down...");
				server.shutdown(); // waits for tasks to finish before shutting it down

				try {

					// waits for 30 more seconds

					if (!server.awaitTermination(30, TimeUnit.SECONDS)) {
						server.shutdownNow();
						System.out.println("Forceful server shutdown."); // forces server to shutdown
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