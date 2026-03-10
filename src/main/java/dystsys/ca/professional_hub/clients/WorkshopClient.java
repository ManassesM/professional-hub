package dystsys.ca.professional_hub.clients;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.generated.workshop.grpc.CheckInWorkerReq;
import com.generated.workshop.grpc.CheckInWorkerRes;
import com.generated.workshop.grpc.GetWorkerNotesReq;
import com.generated.workshop.grpc.GetWorkerNotesRes;
import com.generated.workshop.grpc.ProximityStatus;
import com.generated.workshop.grpc.RangeCheckReq;
import com.generated.workshop.grpc.RangeCheckRes;
import com.generated.workshop.grpc.SendLabWorkReq;
import com.generated.workshop.grpc.SendLabWorkRes;
import com.generated.workshop.grpc.WorkshopGrpc;
import com.generated.workshop.grpc.WorkshopGrpc.WorkshopBlockingStub;
import com.generated.workshop.grpc.WorkshopGrpc.WorkshopStub;

import dystsys.ca.professional_hub.db.MockDB;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class WorkshopClient {

    // helper variables
    private static String workerId = "W-1001";
    private static String workshopId = "WS-001";

    // stubs
    private static WorkshopBlockingStub stub;
    private static WorkshopStub asyncStub;

    // *** main method will call helper methods which calls the server
    public static void main(String[] args) {

	ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

	stub = WorkshopGrpc.newBlockingStub(channel);
	asyncStub = WorkshopGrpc.newStub(channel);

	try {
	    // --> unary helper call
	    checkInWorkerCall(workerId, workshopId);

	    // --> server stream helper call
	    getWorkerNotesCall(workshopId);

	    // --> client stream helper call
	    sendLabWorkCall(MockDB.WORK_SNIPPETS);

	    // --> bi-directional stream helper call
	    rangeCheckCall(MockDB.SIGNAL_STRENGTH);

	    // waits for client to finish its tasks
	    channel.shutdown().awaitTermination(1, TimeUnit.MINUTES);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    } // main

    // --> unary helper **************************************************
    private static void checkInWorkerCall(String workerId, String workshopId) {
	System.out.print("----------checkInWorkerCall----------\n");
	CheckInWorkerReq request = CheckInWorkerReq.newBuilder().setWorkerId(workerId).setWorkshopId(workshopId).build(); // builds and
															  // sends request
	try {

	    CheckInWorkerRes response = stub.checkInWorker(request); // gets response
	    System.out.printf("Worker %s checked in!%nModule name: %s%nCredits: %d%n", workerId, response.getModuleName(),
		    response.getWorkshopCredits());

	} catch (RuntimeException e) {
	    System.err.printf("An error has occurred!%n***%s***%n", e.getMessage());
	}

    } // checkInWorkerCall

    // --> server stream helper **************************************************
    private static void getWorkerNotesCall(String workshopId) {
	System.out.print("\n----------getWorkerNotesCall----------\n");
	GetWorkerNotesReq request = GetWorkerNotesReq.newBuilder().setWorkshopId(workshopId).build();

	Iterator<GetWorkerNotesRes> response = stub.getWorkerNotes(request);

	System.out.printf("Receiving materials for workshop [ %s ]:%n%n", workshopId);
	while (response.hasNext()) {
	    try {
		Thread.sleep(1000); // added to make it look "realistic"

		GetWorkerNotesRes res = response.next();
		String note_content = res.getNoteContent();
		System.out.printf("CONTENT [ %s ]%n", note_content);

	    } catch (RuntimeException | InterruptedException e) {
		System.err.printf("An error has occurred!%n***%s***%n", e.getMessage());
	    }
	}
    } // getWorkerNotesCall

    // --> client stream helper **************************************************
    private static void sendLabWorkCall(List<String> workSnippets) {
	System.out.print("\n----------sendLabWorkCall----------\n");
	CountDownLatch latch = new CountDownLatch(1);

	StreamObserver<SendLabWorkRes> responseObserver = new StreamObserver<SendLabWorkRes>() {

	    @Override
	    public void onNext(SendLabWorkRes response) {
		System.out.printf("Server finished!%nSubmission summary:%nTotal tasks received => [ %d ]%n",
			response.getSubmssionSummary());
	    }

	    @Override
	    public void onError(Throwable t) {
		System.err.printf("An error has occurred!%n***%s***%n", t.getMessage());
		latch.countDown();
	    }

	    @Override
	    public void onCompleted() {
		System.out.printf("***Server acknowledgement***");
		latch.countDown();
	    }
	};

	StreamObserver<SendLabWorkReq> requestObserver = asyncStub.sendLabWork(responseObserver);

	// client sends helper **************************************************
	for (String workSnippet : workSnippets) {
	    SendLabWorkReq request = SendLabWorkReq.newBuilder().setWorkSnippet(workSnippet).build();
	    requestObserver.onNext(request);
	}
	requestObserver.onCompleted(); // done sending data

	try {
	    // waits for client to finish sending data
	    latch.await(1, TimeUnit.MINUTES);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    } // sendLabWorkCall

    // --> bi-directional stream helper **************************************************
    private static void rangeCheckCall(List<Float> signalStrength) {
	System.out.print("\n----------rangeCheckCall----------\n");
	CountDownLatch latch = new CountDownLatch(1);

	StreamObserver<RangeCheckRes> responseObserver = new StreamObserver<RangeCheckRes>() {

	    @Override
	    public void onNext(RangeCheckRes response) {
		try {
		    ProximityStatus proximity_status = response.getProximityStatus();
		    String status_message = response.getStatusMessage();
		    System.out.printf("Signal received!%nProximity Status: %s%nStatus: %s", proximity_status, status_message);

		    Thread.sleep(2000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }

	    @Override
	    public void onError(Throwable t) {
		System.err.printf("An error has occurred!%n***%s***%n", t.getMessage());
		latch.countDown();
	    }

	    @Override
	    public void onCompleted() {
		// TODO Auto-generated method stub
		latch.countDown();
	    }
	};

	StreamObserver<RangeCheckReq> requestObserver = asyncStub.rangeCheck(responseObserver);

	for (Float signal : signalStrength) {
	    RangeCheckReq request = RangeCheckReq.newBuilder().setSignalStrength(signal).build();
	    requestObserver.onNext(request);
	}
	requestObserver.onCompleted();

	try {
	    // waits for client to finish sending data
	    latch.await(1, TimeUnit.MINUTES);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    } // rangeCheckCall
} // WorkshopClient
