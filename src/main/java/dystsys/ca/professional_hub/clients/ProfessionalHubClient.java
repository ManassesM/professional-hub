package dystsys.ca.professional_hub.clients;

import com.generated.guardian.grpc.GuardianGrpc;
import com.generated.guardian.grpc.GuardianGrpc.GuardianBlockingStub;
import com.generated.guardian.grpc.GuardianGrpc.GuardianStub;
import com.generated.productivity.grpc.ProductivityGrpc;
import com.generated.productivity.grpc.ProductivityGrpc.ProductivityBlockingStub;
import com.generated.productivity.grpc.ProductivityGrpc.ProductivityStub;
import com.generated.workshop.grpc.WorkshopGrpc;
import com.generated.workshop.grpc.WorkshopGrpc.WorkshopBlockingStub;
import com.generated.workshop.grpc.WorkshopGrpc.WorkshopStub;

import dystsys.ca.professional_hub.utilities.AppConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ProfessionalHubClient {

	private final String HOST = AppConfig.SERVER_HOST;
	private final int PORT = AppConfig.SERVER_PORT;
	
	private final ManagedChannel channel;

	// Workshop stubs
	private final WorkshopBlockingStub workshopBlockingStub;
	private final WorkshopStub workshopAsyncStub;

	// Productivity stubs
	private final ProductivityBlockingStub productivityBlockingStub;
	private final ProductivityStub productivityAsyncStub;

	// Guardian stub
	private final GuardianBlockingStub guardianBlockingStub;
	private final GuardianStub guardianAsyncStub;

	public ProfessionalHubClient() {
		// shared channel to the server
		channel = ManagedChannelBuilder.forAddress(HOST, PORT).usePlaintext().build();

		// stubs
		workshopBlockingStub = WorkshopGrpc.newBlockingStub(channel);
		workshopAsyncStub = WorkshopGrpc.newStub(channel);

		productivityBlockingStub = ProductivityGrpc.newBlockingStub(channel);
		productivityAsyncStub = ProductivityGrpc.newStub(channel);

		guardianBlockingStub = GuardianGrpc.newBlockingStub(channel);
		guardianAsyncStub = GuardianGrpc.newStub(channel);

		System.out.println("ProfessionalHubClient connected to " + HOST + ":" + PORT);
	} // constructor

	// ***************getters for GUI
	// Workshop
	public WorkshopBlockingStub getWorkshopBlockingStub() {
		return workshopBlockingStub;
	}

	public WorkshopStub getWorkshopAsyncStub() {
		return workshopAsyncStub;
	}

	// Productivity
	public ProductivityBlockingStub getProductivityBlockingStub() {
		return productivityBlockingStub;
	}

	public ProductivityStub getProductivityAsyncStub() {
		return productivityAsyncStub;
	}

	// Guardian
	public GuardianBlockingStub getGuardianBlockingStub() {
		return guardianBlockingStub;
	}

	public GuardianStub getGuardianAsyncStub() {
		return guardianAsyncStub;
	}
	
	public void shutdown() {
		channel.shutdown();
		System.out.println("ProfessionalHubClient channel shut down");
	}
} // ProfessionalHubClient
