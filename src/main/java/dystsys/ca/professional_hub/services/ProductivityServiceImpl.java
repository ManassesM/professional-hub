package dystsys.ca.professional_hub.services;

import java.util.List;

import com.generated.productivity.grpc.ProductivityGrpc.ProductivityImplBase;

import dystsys.ca.professional_hub.core.MockDB;

import com.generated.productivity.grpc.ReportTaskProgressReq;
import com.generated.productivity.grpc.ReportTaskProgressRes;
import com.generated.productivity.grpc.StreamProductivityReq;
import com.generated.productivity.grpc.StreamProductivityRes;
import com.generated.productivity.grpc.VerifyHoursReq;
import com.generated.productivity.grpc.VerifyHoursRes;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class ProductivityServiceImpl extends ProductivityImplBase {

    // unary **************************************************
    @Override
    public void reportTaskProgress(ReportTaskProgressReq request, StreamObserver<ReportTaskProgressRes> responseObserver) {
	String taskId = request.getTaskId();
	int percent_complete = request.getPercentComplete();

	// check for id
	if (!MockDB.TASK_ID.contains(taskId)) {
	    responseObserver
		    .onError(Status.NOT_FOUND.withDescription("Task ID " + taskId + " was not found on database.").asRuntimeException());
	    return;
	}

	// check percentage number
	if (percent_complete < 0 || percent_complete > 100) {
	    responseObserver.onError(Status.OUT_OF_RANGE.withDescription(percent_complete + " is invalid.").asRuntimeException());
	    return;
	}

	String acknowledge = "Task [" + taskId + "] " + percent_complete + "% completed.";
	ReportTaskProgressRes response = ReportTaskProgressRes.newBuilder().setAcknowledge(acknowledge).build();
	responseObserver.onNext(response);
	responseObserver.onCompleted();
    } // reportTaskProgress

    // server stream **************************************************
    @Override
    public void streamProductivity(StreamProductivityReq request, StreamObserver<StreamProductivityRes> responseObserver) {
	String siteId = request.getSiteId();

	// check for id
	if (!MockDB.SITE_ID.contains(siteId)) {
	    responseObserver
		    .onError(Status.NOT_FOUND.withDescription("Site ID " + siteId + " was not found on database.").asRuntimeException());
	    return;
	}
	
	System.out.printf("Sending location id...%nSite ID: [%s]", siteId);
	List<String> worker_update = MockDB.WORKER_UPDATE.get(siteId);
	for (String update : worker_update) {
	    StreamProductivityRes response = StreamProductivityRes.newBuilder().setWorkerUpdate(update).build();
	    responseObserver.onNext(response);
	}
	
	responseObserver.onCompleted();
    } // streamProductivity

    // client stream **************************************************
    @Override
    public StreamObserver<VerifyHoursReq> verifyHours(StreamObserver<VerifyHoursRes> responseObserver) {
	
	return new StreamObserver<VerifyHoursReq>() {
	    int total_minutes = 0;

	    @Override
	    public void onNext(VerifyHoursReq request) {
		try {
		    int minutes_worked = request.getMinutesWorked();
		    total_minutes += minutes_worked;
		    
		    System.out.printf("Time received!%nMinutes worked: [%d]%nTotal minutes: [%d]", minutes_worked, total_minutes);
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
		int total_hours = total_minutes / 60; // <-- it's meant to round down, hence the int type
		VerifyHoursRes response = VerifyHoursRes.newBuilder().setTotalHours(total_hours).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	    }
	};
    } // verifyHours
}
