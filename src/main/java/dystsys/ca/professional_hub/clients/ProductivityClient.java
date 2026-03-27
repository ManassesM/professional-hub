package dystsys.ca.professional_hub.clients;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.generated.productivity.grpc.ProductivityGrpc;
import com.generated.productivity.grpc.ProductivityGrpc.ProductivityBlockingStub;
import com.generated.productivity.grpc.ProductivityGrpc.ProductivityStub;
import com.generated.productivity.grpc.ReportTaskProgressReq;
import com.generated.productivity.grpc.ReportTaskProgressRes;
import com.generated.productivity.grpc.StreamProductivityReq;
import com.generated.productivity.grpc.StreamProductivityRes;
import com.generated.productivity.grpc.VerifyHoursReq;
import com.generated.productivity.grpc.VerifyHoursRes;

import dystsys.ca.professional_hub.records.DiscoveredService;
import dystsys.ca.professional_hub.utilities.ServiceDiscovery;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class ProductivityClient {

	// helper variables
	private static String taskId = "T-100";
	private static int percentComplete = 99;
	private static String siteId = "S-AAA";
	private static List<Integer> minutesWorked = Arrays.asList(30, 25, 20, 60, 45, 22, 31, 50);

	// stubs
	private static ProductivityBlockingStub stub;
	private static ProductivityStub asyncStub;

	// **** main method **************************************************
	public static void main(String[] args) {

		DiscoveredService ds = ServiceDiscovery.findService("_productivity._tcp.local.");
		ManagedChannel channel = ManagedChannelBuilder.forAddress(ds.host(), ds.port()).usePlaintext().build();

		stub = ProductivityGrpc.newBlockingStub(channel);
		asyncStub = ProductivityGrpc.newStub(channel);

		try {
			// --> unary helper call
			reportTaskProgressCall(taskId, percentComplete);

			// --> server stream helper call
			streamProductivityCall(siteId);

			// --> client stream helper call
			verifyHoursCall(minutesWorked);

			// waits for client to finish its tasks
			channel.shutdown().awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	} // main

	// --> unary helper **************************************************
	private static void reportTaskProgressCall(String taskId, int percentComplete) {
		System.out.print("----------reportTaskProgressCall----------\n");
		ReportTaskProgressReq request = ReportTaskProgressReq.newBuilder().setTaskId(taskId)
				.setPercentComplete(percentComplete).build();

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

	// --> client stream helper **************************************************
	private static void verifyHoursCall(List<Integer> minutesWorked) {
		System.out.print("\n----------verifyHoursCall----------\n");
		CountDownLatch latch = new CountDownLatch(1);

		StreamObserver<VerifyHoursRes> responseObserver = new StreamObserver<VerifyHoursRes>() {

			@Override
			public void onNext(VerifyHoursRes response) {
				int totalHours = response.getTotalHours();
				System.out.printf("Time received succesffully!%nTotal hours:%n%d%n", totalHours);
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

		StreamObserver<VerifyHoursReq> requestObserver = asyncStub.verifyHours(responseObserver);

		for (Integer minutes : minutesWorked) {
			VerifyHoursReq request = VerifyHoursReq.newBuilder().setMinutesWorked(minutes).build();
			requestObserver.onNext(request);
		}

		requestObserver.onCompleted();

		try {
			// waits for client to finish sending data
			latch.await(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	} // verifyHoursCall
} // ProductivityClient
