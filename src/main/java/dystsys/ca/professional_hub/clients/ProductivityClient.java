package dystsys.ca.professional_hub.clients;

import java.util.concurrent.TimeUnit;

import com.generated.productivity.grpc.ProductivityGrpc;
import com.generated.productivity.grpc.ProductivityGrpc.ProductivityBlockingStub;
import com.generated.productivity.grpc.ProductivityGrpc.ProductivityStub;
import com.generated.productivity.grpc.ReportTaskProgressReq;
import com.generated.productivity.grpc.ReportTaskProgressRes;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ProductivityClient {

    // helper variables
    private static String taskId = "T-100";
    private static int percentComplete = 99;

    // stubs
    private static ProductivityBlockingStub stub;
    private static ProductivityStub asyncStub;

    // **** main method **************************************************
    public static void main(String[] args) {

	ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

	stub = ProductivityGrpc.newBlockingStub(channel);
	asyncStub = ProductivityGrpc.newStub(channel);

	try {
	    // --> unary helper call
	    reportTaskProgressCall(taskId, percentComplete);

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
} // ProductivityClient
