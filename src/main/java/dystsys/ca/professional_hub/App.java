package dystsys.ca.professional_hub;

import dystsys.ca.professional_hub.clients.GuardianClient;
import dystsys.ca.professional_hub.clients.ProductivityClient;
import dystsys.ca.professional_hub.clients.WorkshopClient;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Launching clients...");

		WorkshopClient.main(args);
		ProductivityClient.main(args);
		GuardianClient.main(args);
	}
}
