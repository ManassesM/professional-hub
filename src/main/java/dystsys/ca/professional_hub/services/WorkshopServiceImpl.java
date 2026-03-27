package dystsys.ca.professional_hub.services;

import java.util.List;

import com.generated.workshop.grpc.CheckInWorkerReq;
import com.generated.workshop.grpc.CheckInWorkerRes;
import com.generated.workshop.grpc.GetWorkerNotesReq;
import com.generated.workshop.grpc.GetWorkerNotesRes;
import com.generated.workshop.grpc.ProximityStatus;
import com.generated.workshop.grpc.RangeCheckReq;
import com.generated.workshop.grpc.RangeCheckRes;
import com.generated.workshop.grpc.SendLabWorkReq;
import com.generated.workshop.grpc.SendLabWorkRes;
import com.generated.workshop.grpc.WorkshopGrpc.WorkshopImplBase;

import dystsys.ca.professional_hub.database.MockDB;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class WorkshopServiceImpl extends WorkshopImplBase {

	// unary **************************************************
	@Override
	public void checkInWorker(CheckInWorkerReq request, StreamObserver<CheckInWorkerRes> responseObserver) {
		String workerId = request.getWorkerId();
		String workshopId = request.getWorkshopId();

		// check for worker
		if (!MockDB.REGISTERED_WORKERS.contains(workerId)) {
			responseObserver.onError(Status.NOT_FOUND
					.withDescription("Worker ID " + workerId + " was not found on database.").asRuntimeException());
			return;
		}

		// check for workshop id
		if (!MockDB.WORKSHOP_NOTES.containsKey(workshopId)) {
			responseObserver.onError(Status.NOT_FOUND.withDescription("Workshop ID " + workshopId + " does not exist.")
					.asRuntimeException());
			return;
		}

		System.out.printf("Checking in worker: %s for workshop %s%n", workerId, workshopId);
		CheckInWorkerRes response = CheckInWorkerRes.newBuilder().setModuleName("Advanced Safety Protocols")
				.setWorkshopCredits(10).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	} // checkInWorker

	// server stream **************************************************
	@Override
	public void getWorkerNotes(GetWorkerNotesReq request, StreamObserver<GetWorkerNotesRes> responseObserver) {
		String workshopId = request.getWorkshopId();

		// check for workshop id
		if (!MockDB.WORKSHOP_NOTES.containsKey(workshopId)) {
			responseObserver.onError(Status.NOT_FOUND.withDescription("Workshop ID " + workshopId + " does not exist.")
					.asRuntimeException());
			return;
		}

		// workshop_id exists on db, loop through notes
		System.out.printf("Sending material for workshop %s%n", workshopId);
		List<String> notes = MockDB.WORKSHOP_NOTES.get(workshopId);
		for (String note : notes) {
			GetWorkerNotesRes response = GetWorkerNotesRes.newBuilder().setNoteContent(note).build();
			responseObserver.onNext(response);
		}

		responseObserver.onCompleted();
	} // getWorkerNotes

	// client stream **************************************************
	@Override
	public StreamObserver<SendLabWorkReq> sendLabWork(StreamObserver<SendLabWorkRes> responseObserver) {

		return new StreamObserver<SendLabWorkReq>() {
			int submssion_summary = 0; // counter for summary

			@Override
			public void onNext(SendLabWorkReq request) {
				try {
					submssion_summary++;
					System.out.printf("Received task:%n%s%n", request.getWorkSnippet());

					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} // onNext

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}

			@Override
			public void onCompleted() {
				SendLabWorkRes response = SendLabWorkRes.newBuilder().setSubmssionSummary(submssion_summary).build();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
			}
		};
	} // sendLabWork

	// bi-directional stream **************************************************
	@Override
	public StreamObserver<RangeCheckReq> rangeCheck(StreamObserver<RangeCheckRes> responseObserver) {
		return new StreamObserver<RangeCheckReq>() {

			@Override
			public void onNext(RangeCheckReq request) {
				float signal_strength = request.getSignalStrength();
				System.out.printf("Received signal: %f%n", signal_strength);

				if (signal_strength <= -80) {
					// signal is too weak
					RangeCheckRes response = RangeCheckRes.newBuilder().setProximityStatus(ProximityStatus.DISCONNECTED)
							.setStatusMessage("Out of range... Disconnected!").build();
					responseObserver.onNext(response);
				} else if (signal_strength > -80 && signal_strength < 0) {
					// signal is strong enough
					RangeCheckRes response = RangeCheckRes.newBuilder().setProximityStatus(ProximityStatus.IN_RANGE)
							.setStatusMessage("In range... Connected!").build();
					responseObserver.onNext(response);
				} else {
					// signal is corrupted or invalid
					RangeCheckRes response = RangeCheckRes.newBuilder().setProximityStatus(ProximityStatus.INVALID)
							.setStatusMessage("Signal received is invalid.").build();
					responseObserver.onNext(response);
				}
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
	} // rangeCheck
} // WorkshopServiceImpl
