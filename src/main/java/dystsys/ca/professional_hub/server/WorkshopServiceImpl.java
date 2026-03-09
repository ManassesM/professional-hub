package dystsys.ca.professional_hub.server;

import java.util.List;

import com.generated.workshop.grpc.CheckInWorkerReq;
import com.generated.workshop.grpc.CheckInWorkerRes;
import com.generated.workshop.grpc.GetWorkerNotesReq;
import com.generated.workshop.grpc.GetWorkerNotesRes;
import com.generated.workshop.grpc.WorkshopGrpc.WorkshopImplBase;

import dystsys.ca.professional_hub.db.MockDB;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class WorkshopServiceImpl extends WorkshopImplBase {
	
	// unary call
	@Override
	public void checkInWorker(CheckInWorkerReq request, StreamObserver<CheckInWorkerRes> responseObserver) {
		String workerId = request.getWorkerId();
		String workshopId = request.getWorkshopId();
		
		// check for worker
		if(!MockDB.REGISTERED_WORKERS.contains(workerId)) {
			responseObserver.onError(Status.NOT_FOUND.withDescription("Worker ID " + workerId + " was not found on database.").asRuntimeException());
			return;
		}
		
		// check for workshop id
		if(!MockDB.WORKSHOP_NOTES.containsKey(workshopId)) {
			responseObserver.onError(Status.NOT_FOUND.withDescription("Workshop ID " + workshopId + " does not exist.").asRuntimeException());
			return;
		}
		
		System.out.printf("Checking in worker: %s for workshop %s%n", workerId, workshopId);
		CheckInWorkerRes response = CheckInWorkerRes.newBuilder().setModuleName("").setWorkshopCredits(10).build();
	
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	} // checkInWorker
	
	// server stream call
	@Override
	public void getWorkerNotes(GetWorkerNotesReq request, StreamObserver<GetWorkerNotesRes> responseObserver) {
		String workshopId = request.getWorkshopId();
		
		System.out.printf("Sending material for workshop %s%n", workshopId);
		
		// check for workshop id 
		if(!MockDB.WORKSHOP_NOTES.containsKey(workshopId)) {
			responseObserver.onError(Status.NOT_FOUND.withDescription("Workshop ID " + workshopId + " does not exist.").asRuntimeException());
			return;
		}
		
		// workshop_id exists on db, loop through notes 
		List<String> notes = MockDB.WORKSHOP_NOTES.get(workshopId);
		for (String note : notes) {
			GetWorkerNotesRes response = GetWorkerNotesRes.newBuilder().setNoteContent(note).build();
			responseObserver.onNext(response);
		}
		
		responseObserver.onCompleted();
	} // getWorkerNotes
	
	
} // WorkshopServiceImpl
