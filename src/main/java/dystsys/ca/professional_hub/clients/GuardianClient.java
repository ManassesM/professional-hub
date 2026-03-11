package dystsys.ca.professional_hub.clients;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.generated.guardian.grpc.GuardianGrpc;
import com.generated.guardian.grpc.GuardianGrpc.GuardianBlockingStub;
import com.generated.guardian.grpc.GuardianGrpc.GuardianStub;
import com.generated.guardian.grpc.MonitorSafetyReq;
import com.generated.guardian.grpc.MonitorSafetyRes;
import com.generated.guardian.grpc.VerifyZoneSafetyReq;
import com.generated.guardian.grpc.VerifyZoneSafetyRes;

import dystsys.ca.professional_hub.core.AppConfig;
import dystsys.ca.professional_hub.core.MockDB;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class GuardianClient {

    // helper variables
    private static String locationId = "LOC-001";

    // stubs
    private static GuardianBlockingStub stub;
    private static GuardianStub asyncStub;
    private static List<String> zoneIds = new ArrayList<>(MockDB.ZONE_INSTRUCTIONS.keySet());

    // *** main method
    public static void main(String[] args) {

	ManagedChannel channel = ManagedChannelBuilder.forAddress(AppConfig.SERVER_HOST, AppConfig.SERVER_PORT).usePlaintext().build();

	stub = GuardianGrpc.newBlockingStub(channel);
	asyncStub = GuardianGrpc.newStub(channel);

	try {
	    // --> unary helper call
	    monitorSafetyCall(locationId);

	    // --> bi-directional stream helper call
	    verifyZoneSafetyCall(zoneIds);

	    channel.shutdown().awaitTermination(1, TimeUnit.MINUTES);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    } // main

    // --> unary helper **************************************************
    private static void monitorSafetyCall(String locationId) {
	System.out.print("----------checkInWorkerCall----------\n");
	MonitorSafetyReq request = MonitorSafetyReq.newBuilder().setLocationId(locationId).build();

	try {

	    MonitorSafetyRes response = stub.monitorSafety(request);
	    System.out.printf("[Protocol Received]%nLocation: %s%n%s%n", locationId, response.getPpeProtocol());

	} catch (RuntimeException e) {
	    System.err.printf("An error has occurred!%n***%s***%n", e.getMessage());
	}

    } // monitorSafetyCall

    // --> bi-directional stream helper
    // **************************************************
    private static void verifyZoneSafetyCall(List<String> zoneIds) {
	System.out.print("\n----------verifyZoneSafetyCall----------\n");
	CountDownLatch latch = new CountDownLatch(1);

	StreamObserver<VerifyZoneSafetyRes> responseObserver = new StreamObserver<VerifyZoneSafetyRes>() {

	    @Override
	    public void onNext(VerifyZoneSafetyRes response) {
		try {
		    System.out.printf("Safety update: [%s]%n", response.getZoneInstruction());
		    Thread.sleep(1500);
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
		latch.countDown();
	    }
	};

	StreamObserver<VerifyZoneSafetyReq> requestObserver = asyncStub.verifyZoneSafety(responseObserver);
	for (String zoneId : zoneIds) {
	    System.out.printf("Checking zone %s...%n", zoneId);
	    VerifyZoneSafetyReq request = VerifyZoneSafetyReq.newBuilder().setCurrentZoneId(zoneId).build();
	    requestObserver.onNext(request);
	}
	requestObserver.onCompleted();

	try {
	    // waits for client to finish sending data
	    latch.await(1, TimeUnit.MINUTES);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    } // verifyZoneSafetyCall
} // GuardianClient
