package dystsys.ca.professional_hub.server;

import java.util.List;

import com.generated.workshop.grpc.CheckInWorkerReq;
import com.generated.workshop.grpc.CheckInWorkerRes;
import com.generated.workshop.grpc.GetWorkerNotesReq;
import com.generated.workshop.grpc.GetWorkerNotesRes;
import com.generated.workshop.grpc.WorkshopGrpc.WorkshopImplBase;

import dystsys.ca.professional_hub.db.MockDB;
import io.grpc.stub.StreamObserver;

public class WorkshopServiceImpl extends WorkshopImplBase {
	
	// unary call
	@Override
	public void checkInWorker(CheckInWorkerReq request, StreamObserver<CheckInWorkerRes> responseObserver) {
		String workedId = request.getWorkerId();
		String workshopId = request.getWorkshopId();
		
		System.out.printf("Checking in worker: %s for workshop %s%n", workedId, workshopId);
		CheckInWorkerRes response = CheckInWorkerRes.newBuilder().setModuleName("").setWorkshopCredits(10).build();
	
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	} // checkInWorker
	
	// server stream call
	@Override
	public void getWorkerNotes(GetWorkerNotesReq request, StreamObserver<GetWorkerNotesRes> responseObserver) {
		String workshopId = request.getWorkshopId();
		
		System.out.printf("Sending material for workshop %s%n", workshopId);
				
		List<String> notes = MockDB.WORKSHOP_NOTES.getOrDefault(workshopId, List.of("Invalid workshop ID"));
		for (String note : notes) {
			GetWorkerNotesRes response = GetWorkerNotesRes.newBuilder().setNoteContent(note).build();
			responseObserver.onNext(response);
		}
		
		responseObserver.onCompleted();
	} // getWorkerNotes
	
	
} // WorkshopServiceImpl
