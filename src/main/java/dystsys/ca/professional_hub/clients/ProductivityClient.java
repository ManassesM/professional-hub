package dystsys.ca.professional_hub.clients;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.generated.productivity.grpc.ProductivityGrpc;
import com.generated.productivity.grpc.ProductivityGrpc.ProductivityBlockingStub;
import com.generated.productivity.grpc.ProductivityGrpc.ProductivityStub;
import com.generated.productivity.grpc.ReportTaskProgressReq;
import com.generated.productivity.grpc.ReportTaskProgressRes;
import com.generated.productivity.grpc.StreamProductivityReq;
import com.generated.productivity.grpc.StreamProductivityRes;

import dystsys.ca.professional_hub.core.AppConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ProductivityClient {

    // helper variables
    private static String taskId = "T-100";
    private static int percentComplete = 99;
    private static String siteId = "S-AAA";

    // stubs
    private static ProductivityBlockingStub stub;
    private static ProductivityStub asyncStub;

    // **** main method **************************************************
    public static void main(String[] args) {

	ManagedChannel channel = ManagedChannelBuilder.forAddress(AppConfig.SERVER_HOST, AppConfig.SERVER_PORT).usePlaintext().build();

	stub = ProductivityGrpc.newBlockingStub(channel);
	asyncStub = ProductivityGrpc.newStub(channel);

	try {
	    // --> unary helper call
	    reportTaskProgressCall(taskId, percentComplete);

	    // --> server stream helper call
	    streamProductivityCall(siteId);

	    // waits for client to finish its tasks
	    channel.shutdown().awaitTermination(1, TimeUnit.MINUTES);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    } // main

    // --> unary helper **************************************************
    private static void reportTaskProgressCall(String taskId, int percentComplete) {
	System.out.print("----------reportTaskProgressCall----------\n");
	ReportTaskProgressReq request = ReportTaskProgressReq.newBuilder().setTaskId(taskId).setPercentComplete(percentComplete).build();

	try {

	    ReportTaskProgressRes response = stub.reportTaskProgress(request);
	    String acknowledge = response.getAcknowledge();
	    System.out.printf("Request sent!%n%s", acknowledge);

	} catch (RuntimeException e) {
	    System.err.printf("An error has occurred!%n***%s***%n", e.getMessage());
	}
    } // reportTaskProgressCall

    // --> server stream helper **************************************************
    private static void streamProductivityCall(String siteId) {
	System.out.print("----------streamProductivityCall----------\n");
	StreamProductivityReq request = StreamProductivityReq.newBuilder().setSiteId(siteId).build();

	Iterator<StreamProductivityRes> response = stub.streamProductivity(request);

	while (response.hasNext()) {
	    try {
		Thread.sleep(1000); // added to make it look "realistic"

		StreamProductivityRes res = response.next();
		String workerUpdate = res.getWorkerUpdate();
		System.out.printf("Update received!%n%s%n", workerUpdate);
	    } catch (RuntimeException | InterruptedException e) {
		System.err.printf("An error has occurred!%n***%s***%n", e.getMessage());
	    }
	}
    } // streamProductivityCall
} // ProductivityClient
