package dystsys.ca.professional_hub.client;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.generated.workshop.grpc.CheckInWorkerReq;
import com.generated.workshop.grpc.CheckInWorkerRes;
import com.generated.workshop.grpc.GetWorkerNotesReq;
import com.generated.workshop.grpc.GetWorkerNotesRes;
import com.generated.workshop.grpc.WorkshopGrpc;
import com.generated.workshop.grpc.WorkshopGrpc.WorkshopBlockingStub;
import com.generated.workshop.grpc.WorkshopGrpc.WorkshopStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class WorkshopClient {

	// helper variables
	private static String workerId = "W-1001";
	private static String workshopId = "WS-001";
	
	// stubs
	private static WorkshopBlockingStub stub;
	private static WorkshopStub asyncStub;
	
	// *** main method will call helper methods which calls the server
	public static void main(String[] args) throws InterruptedException {
		
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

		stub = WorkshopGrpc.newBlockingStub(channel);
		asyncStub = WorkshopGrpc.newStub(channel);
		
		// unary helper call
		checkInWorkerCall(workerId, workshopId);
		
		// server stream helper call
		getWorkerNotesCall(workshopId);
		
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	} // main
	
	// unary call
	private static void checkInWorkerCall(String workerId, String workshopId) {
		CheckInWorkerReq request = CheckInWorkerReq.newBuilder().setWorkerId(workerId).setWorkshopId(workshopId).build(); // builds and sends request
		CheckInWorkerRes response = stub.checkInWorker(request); // gets response
		
		System.out.printf("Worker %s checked in!%nModule name: %s%nCredits: %d%n", workerId, response.getModuleName(), response.getWorkshopCredits());
	} // checkInWorkerCall
	
	// server stream call
	private static void getWorkerNotesCall(String workshopId) throws InterruptedException {
		GetWorkerNotesReq request = GetWorkerNotesReq.newBuilder().setWorkshopId(workshopId).build();
		
		Iterator<GetWorkerNotesRes> response = stub.getWorkerNotes(request);
		
		System.out.printf("Receiving materials for workshop [ %s ]:%n", workshopId);
		while(response.hasNext()) {
			Thread.sleep(1000); // added to make it look "realistic"
			GetWorkerNotesRes res = response.next();			
			String note_content = res.getNoteContent();
			System.out.printf("CONTENT [ %s ]%n", note_content);
		}
	} // getWorkerNotesCall
	
} // WorkshopClient
