package dystsys.ca.professional_hub.server;

import com.generated.workshop.grpc.CheckInWorkerReq;
import com.generated.workshop.grpc.CheckInWorkerRes;
import com.generated.workshop.grpc.WorkshopGrpc.WorkshopImplBase;

import io.grpc.stub.StreamObserver;

public class WorkshopServiceImpl extends WorkshopImplBase {

	@Override
	public void checkInWorker(CheckInWorkerReq request, StreamObserver<CheckInWorkerRes> responseObserver) {
		String workedId = request.getWorkerId();
		String workshopId = request.getWorkshopId();
		
		System.out.printf("Checking in worker: %s for workshop %s", workedId, workshopId);
		CheckInWorkerRes response = CheckInWorkerRes.newBuilder().setModuleName("").setWorkshopCredits(10).build();
	
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	} // checkInWorker
} // WorkshopServiceImpl
