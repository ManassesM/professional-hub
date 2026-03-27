package dystsys.ca.professional_hub.services;

import com.generated.guardian.grpc.GuardianGrpc.GuardianImplBase;
import com.generated.guardian.grpc.MonitorSafetyReq;
import com.generated.guardian.grpc.MonitorSafetyRes;
import com.generated.guardian.grpc.VerifyZoneSafetyReq;
import com.generated.guardian.grpc.VerifyZoneSafetyRes;

import dystsys.ca.professional_hub.database.MockDB;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class GuardianServiceImpl extends GuardianImplBase {

    // unary **************************************************
    @Override
    public void monitorSafety(MonitorSafetyReq request, StreamObserver<MonitorSafetyRes> responseObserver) {
	String locationId = request.getLocationId();

	if (!MockDB.PPE_PROTOCOLS.containsKey(locationId)) {
	    responseObserver.onError(
		    Status.NOT_FOUND.withDescription("Location ID " + locationId + " was not found on database.").asRuntimeException());
	    return;
	}

	String ppeProtocol = MockDB.PPE_PROTOCOLS.get(locationId);
	MonitorSafetyRes response = MonitorSafetyRes.newBuilder().setPpeProtocol(ppeProtocol).build();

	responseObserver.onNext(response);
	responseObserver.onCompleted();
    } // monitorSafety

    // bi-directional stream **************************************************
    @Override
    public StreamObserver<VerifyZoneSafetyReq> verifyZoneSafety(StreamObserver<VerifyZoneSafetyRes> responseObserver) {
	return new StreamObserver<VerifyZoneSafetyReq>() {

	    @Override
	    public void onNext(VerifyZoneSafetyReq request) {
		String currentZoneId = request.getCurrentZoneId();

		String instruction = MockDB.ZONE_INSTRUCTIONS.getOrDefault(currentZoneId, "UNKNOWN ZONE: Proceed with extreme caution.");
		VerifyZoneSafetyRes response = VerifyZoneSafetyRes.newBuilder().setZoneInstruction(instruction).build();
		responseObserver.onNext(response);
	    }

	    @Override
	    public void onError(Throwable t) {
		t.printStackTrace();
	    }

	    @Override
	    public void onCompleted() {
		responseObserver.onCompleted();
	    }
	};
    } // verifyZoneSafety
} // GuardianServiceImpl
