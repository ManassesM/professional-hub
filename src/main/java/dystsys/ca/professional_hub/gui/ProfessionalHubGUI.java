package dystsys.ca.professional_hub.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import com.generated.guardian.grpc.MonitorSafetyReq;
import com.generated.guardian.grpc.MonitorSafetyRes;
import com.generated.guardian.grpc.VerifyZoneSafetyReq;
import com.generated.productivity.grpc.ReportTaskProgressReq;
import com.generated.productivity.grpc.ReportTaskProgressRes;
import com.generated.productivity.grpc.StreamProductivityReq;
import com.generated.productivity.grpc.StreamProductivityRes;
import com.generated.productivity.grpc.VerifyHoursReq;
import com.generated.productivity.grpc.VerifyHoursRes;
import com.generated.workshop.grpc.CheckInWorkerReq;
import com.generated.workshop.grpc.CheckInWorkerRes;
import com.generated.workshop.grpc.GetWorkerNotesReq;
import com.generated.workshop.grpc.GetWorkerNotesRes;
import com.generated.workshop.grpc.RangeCheckReq;
import com.generated.workshop.grpc.RangeCheckRes;
import com.generated.workshop.grpc.SendLabWorkReq;
import com.generated.workshop.grpc.SendLabWorkRes;

import dystsys.ca.professional_hub.clients.ProfessionalHubClient;
import io.grpc.stub.StreamObserver;

public class ProfessionalHubGUI extends JFrame {
	private static final long serialVersionUID = 1L;

	// central client
	private ProfessionalHubClient hubClient;

	// GUI components
	private JTabbedPane tabbedPane;
	private JTextPane resultPane;

	// *****Workshop fields
	private JTextField workerIdField;
	private JTextField workshopIdField;
	private JTextField getNotesWorkshopIdField;
	private JTextField labSnippetField;
	private JTextField signalField;
	private JButton checkInButton;
	private JButton getNotesButton;
	private JButton prepareSendLabButton;
	private JButton sendLabSnippetButton;
	private JButton doneLabButton;
	private JButton prepareRangeButton;
	private JButton sendSignalButton;
	private JButton doneRangeButton;

	// *****Productivity fields
	private JTextField taskIdField;
	private JTextField percentField;
	private JTextField siteIdField;
	private JTextField minutesField;
	private JButton reportProgressButton;
	private JButton streamProductivityButton;
	private JButton prepareVerifyHoursButton;
	private JButton sendMinutesButton;
	private JButton doneHoursButton;

	// *****Guardian fields
	private JTextField locationIdField;
	private JTextField zoneIdField;
	private JButton monitorSafetyButton;
	private JButton prepareZoneButton;
	private JButton sendZoneButton;
	private JButton doneZoneButton;

	// observers needed for send and done buttons
	private StreamObserver<SendLabWorkReq> labRequestObserver;
	private StreamObserver<RangeCheckReq> rangeRequestObserver;
	private StreamObserver<VerifyHoursReq> hoursRequestObserver;
	private StreamObserver<VerifyZoneSafetyReq> zoneRequestObserver;

	public ProfessionalHubGUI() {
		hubClient = new ProfessionalHubClient();

		setTitle("Professional Hub");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1100, 720);
		setLocationRelativeTo(null);

		initComponents();

		// shutdown hook
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				hubClient.shutdown();
			}
		});
	} // constructor

	private void initComponents() {
		tabbedPane = new JTabbedPane();
		resultPane = new JTextPane();

		// ***************workshop tab***************
		JPanel workshopPanel = new JPanel();
		workshopPanel.setLayout(new GridLayout(0, 1, 0, 0)); // vertical section stacking

		// *****UNARY section
		JPanel unary = new JPanel();
		unary.setBorder(
				new TitledBorder(null, "UNARY CheckInWorker", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		// ----------cheat code field----------
		JLabel lblUnaryWorkshopCheat = new JLabel("W-1001 & WS-001");
		lblUnaryWorkshopCheat.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblUnaryWorkshopCheat.setForeground(new Color(255, 0, 0));
		unary.add(lblUnaryWorkshopCheat);
		// ----------------------------------------

		// workerId label and text field
		unary.add(new JLabel("Worker ID"));
		workerIdField = new JTextField(15);
		unary.add(workerIdField);

		// workshop label and text field
		unary.add(new JLabel("Workshop ID"));
		workshopIdField = new JTextField(15);
		unary.add(workshopIdField);

		// checkInWorker btn
		checkInButton = new JButton("Check In Worker");
		unary.add(checkInButton);

		workshopPanel.add(unary);

		// *****SERVER-STREAM section
		JPanel server_streaming = new JPanel();
		server_streaming.setBorder(new TitledBorder(null, "SERVER-STREAM GetWorkerNotes", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		// ----------cheat code field----------
		JLabel lblServerStreamWorkshopCheat = new JLabel("WS-001");
		lblServerStreamWorkshopCheat.setForeground(Color.RED);
		lblServerStreamWorkshopCheat.setFont(new Font("Tahoma", Font.BOLD, 14));
		server_streaming.add(lblServerStreamWorkshopCheat);
		// ----------------------------------------

		// workshopId label and text field
		server_streaming.add(new JLabel("Workshop ID"));
		getNotesWorkshopIdField = new JTextField();
		getNotesWorkshopIdField.setColumns(15);
		server_streaming.add(getNotesWorkshopIdField);

		// getWorkerNotes btn
		getNotesButton = new JButton("Get Worker Notes");
		server_streaming.add(getNotesButton);

		workshopPanel.add(server_streaming);

		// *****CLIENT-STREAM section
		JPanel client_streaming = new JPanel();
		client_streaming.setBorder(new TitledBorder(null, "CLIENT-STREAM SendLabWork", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		// ---------- cheat code field ----------
		JLabel lblCliCheat = new JLabel("Type in any set of String");
		lblCliCheat.setForeground(Color.RED);
		lblCliCheat.setFont(new Font("Tahoma", Font.BOLD, 14));
		client_streaming.add(lblCliCheat);
		// ----------------------------------------

		client_streaming.add(new JLabel("Work Snippet"));
		labSnippetField = new JTextField();
		labSnippetField.setColumns(30);
		client_streaming.add(labSnippetField);

		prepareSendLabButton = new JButton("Prepare Client Stream");
		sendLabSnippetButton = new JButton("Send Snippet");
		doneLabButton = new JButton("Finish Stream");

		client_streaming.add(prepareSendLabButton);
		client_streaming.add(sendLabSnippetButton);
		client_streaming.add(doneLabButton);

		workshopPanel.add(client_streaming);

		// *****BI-DI-STREAM section
		JPanel bi_directional = new JPanel();
		bi_directional.setBorder(
				new TitledBorder(null, "BI-DI-STREAM RangeCheck", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		// ---------- cheat code field ----------
		JLabel lblBiCheat = new JLabel("Try: -45.5, -85.2, 0.25");
		lblBiCheat.setForeground(Color.RED);
		lblBiCheat.setFont(new Font("Tahoma", Font.BOLD, 14));
		bi_directional.add(lblBiCheat);
		// ----------------------------------------

		bi_directional.add(new JLabel("Signal Strength"));
		signalField = new JTextField();
		signalField.setColumns(15);
		bi_directional.add(signalField);
		prepareRangeButton = new JButton("Prepare Bi-Di Stream");
		sendSignalButton = new JButton("Send Signal");
		doneRangeButton = new JButton("Finish Stream");
		bi_directional.add(prepareRangeButton);
		bi_directional.add(sendSignalButton);
		bi_directional.add(doneRangeButton);

		workshopPanel.add(bi_directional);

		// ***************productivity tab***************
		JPanel productivityPanel = new JPanel();
		productivityPanel.setLayout(new GridLayout(0, 1, 0, 10));

		// ****UNARY section
		JPanel prodUnary = new JPanel();
		prodUnary.setBorder(
				new TitledBorder(null, "UNARY ReportTaskProgress", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		// ---------- cheat code field ----------
		JLabel lblProdUnaryCheat = new JLabel("T-100 & 99");
		lblProdUnaryCheat.setForeground(Color.RED);
		lblProdUnaryCheat.setFont(new Font("Tahoma", Font.BOLD, 14));
		prodUnary.add(lblProdUnaryCheat);
		// ----------------------------------------

		prodUnary.add(new JLabel("Task ID"));
		taskIdField = new JTextField(15);
		prodUnary.add(taskIdField);
		prodUnary.add(new JLabel("Percent Complete"));
		percentField = new JTextField(8);
		prodUnary.add(percentField);
		reportProgressButton = new JButton("Report Task Progress");
		prodUnary.add(reportProgressButton);

		productivityPanel.add(prodUnary);

		// ****SERVER-STREAM section
		JPanel prodServerStream = new JPanel();
		prodServerStream.setBorder(new TitledBorder(null, "SERVER-STREAM StreamProductivity", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		// ---------- cheat code field ----------
		JLabel lblProdServerCheat = new JLabel("S-AAA");
		lblProdServerCheat.setForeground(Color.RED);
		lblProdServerCheat.setFont(new Font("Tahoma", Font.BOLD, 14));
		prodServerStream.add(lblProdServerCheat);
		// ----------------------------------------

		prodServerStream.add(new JLabel("Site ID"));
		siteIdField = new JTextField(15);
		prodServerStream.add(siteIdField);
		streamProductivityButton = new JButton("Stream Productivity");
		prodServerStream.add(streamProductivityButton);

		productivityPanel.add(prodServerStream);

		// ****CLIENT-STREAM section
		JPanel prodClientStream = new JPanel();
		prodClientStream.setBorder(new TitledBorder(null, "CLIENT-STREAM VerifyHours", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		// ---------- cheat code field ----------
		JLabel lblProdClientCheat = new JLabel("30, 25, 20, ...");
		lblProdClientCheat.setForeground(Color.RED);
		lblProdClientCheat.setFont(new Font("Tahoma", Font.BOLD, 14));
		prodClientStream.add(lblProdClientCheat);
		// ----------------------------------------

		prodClientStream.add(new JLabel("Minutes Worked"));
		minutesField = new JTextField(12);
		prodClientStream.add(minutesField);
		prepareVerifyHoursButton = new JButton("Prepare Hours Stream");
		prodClientStream.add(prepareVerifyHoursButton);
		sendMinutesButton = new JButton("Send Minutes");
		prodClientStream.add(sendMinutesButton);
		doneHoursButton = new JButton("Get Total");
		prodClientStream.add(doneHoursButton);

		productivityPanel.add(prodClientStream);

		// ***************guardian tab***************
		JPanel guardianPanel = new JPanel();
		guardianPanel.setLayout(new GridLayout(0, 1, 0, 10));

		// ****UNARY section
		JPanel guardUnary = new JPanel();
        guardUnary.setBorder(new TitledBorder(null, "UNARY MonitorSafety", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		// ---------- cheat code field ----------
        JLabel lblGuardUnaryCheat = new JLabel("LOC-001");
        lblGuardUnaryCheat.setForeground(Color.RED);
        lblGuardUnaryCheat.setFont(new Font("Tahoma", Font.BOLD, 14));
        guardUnary.add(lblGuardUnaryCheat);
		// ----------------------------------------
        
        guardUnary.add(new JLabel("Location ID"));
        locationIdField = new JTextField(15);
        guardUnary.add(locationIdField);
        monitorSafetyButton = new JButton("Monitor Safety");
        guardUnary.add(monitorSafetyButton);
        
        guardianPanel.add(guardUnary);
        
		// ****BI-DI-STREAM section
		// TODO:

		// ***************tabs***************
		tabbedPane.addTab("Workshop", workshopPanel);
		tabbedPane.addTab("Productivity", productivityPanel);
		tabbedPane.addTab("Guardian", guardianPanel);

		// ***************result pane***************
		resultPane.setEditable(false);
		resultPane.setFont(new Font("Tahoma", Font.BOLD, 14));
		JScrollPane scrollPane = new JScrollPane(resultPane);

		// ***************main layout***************
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, scrollPane);
		splitPane.setResizeWeight(0.75);
		splitPane.setDividerLocation(450);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(splitPane, BorderLayout.CENTER);

		// ***************workshop listeners***************
		// --- unary
		checkInButton.addActionListener(this::checkInWorkerButtonActionPerformed);
		// --- server-stream
		getNotesButton.addActionListener(this::getWorkerNotesButtonActionPerformed);
		// --- client-stream
		prepareSendLabButton.addActionListener(this::prepareSendLabButtonActionPerformed);
		sendLabSnippetButton.addActionListener(this::sendLabSnippetButtonActionPerformed);
		doneLabButton.addActionListener(this::doneLabButtonActionPerformed);
		// bi-directional stream
		prepareRangeButton.addActionListener(this::prepareRangeButtonActionPerformed);
		sendSignalButton.addActionListener(this::sendSignalButtonActionPerformed);
		doneRangeButton.addActionListener(this::doneRangeButtonActionPerformed);

		// ***************productivity listeners***************
		// --- unary
		reportProgressButton.addActionListener(this::reportProgressButtonActionPerformed);
		// -- server-stream
		streamProductivityButton.addActionListener(this::streamProductivityButtonActionPerformed);
		// -- client-stream
		prepareVerifyHoursButton.addActionListener(this::prepareVerifyHoursButtonActionPerformed);
		sendMinutesButton.addActionListener(this::sendMinutesButtonActionPerformed);
		doneHoursButton.addActionListener(this::doneHoursButtonActionPerformed);

		// ***************guardian listeners***************
		// --- unary
		monitorSafetyButton.addActionListener(this::monitorSafetyButtonActionPerformed);
		
		// bi-directional stream
		// TODO:
	}

	public ProfessionalHubClient getHubClient() {
		return hubClient;
	}

	// main launcher
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new ProfessionalHubGUI().setVisible(true);
		});
	} // main

	// ==============================WORKSHOP==============================
	// ***************UNARY checkInWorker***************
	private void checkInWorkerButtonActionPerformed(ActionEvent event) {
		String workerId = workerIdField.getText().trim();
		String workshopId = workshopIdField.getText().trim();

		// check empty field
		if (workerId.isEmpty() || workshopId.isEmpty()) {
			resultPane.setText("Please fill in both fields!\n" + resultPane.getText());
			return;
		}

		// gRPC request
		CheckInWorkerReq req = CheckInWorkerReq.newBuilder().setWorkerId(workerId).setWorkshopId(workshopId).build();

		try {
			CheckInWorkerRes res = getHubClient().getWorkshopBlockingStub().checkInWorker(req);

			String message = String.format("Worker %s checked in successfully!%nModule: %s%nCredits: %d%n%n", workerId,
					res.getModuleName(), res.getWorkshopCredits());

			resultPane.setText(message + resultPane.getText());
		} catch (Exception e) {
			resultPane.setText("Error: " + e.getMessage() + "\n" + resultPane.getText());
		}
	} // checkInWorkerButtonActionPerformed

	// ***************SERVER-STREAM GetWorkerNotes***************
	private void getWorkerNotesButtonActionPerformed(ActionEvent evt) {
		String workshopId = getNotesWorkshopIdField.getText().trim();

		if (workshopId.isEmpty()) {
			resultPane.setText("Please enter a Workshop ID!\n\n" + resultPane.getText());
			return;
		}

		GetWorkerNotesReq req = GetWorkerNotesReq.newBuilder().setWorkshopId(workshopId).build();

		resultPane.setText("Receiving notes for workshop " + workshopId + "...\n\n");

		// swingworker for live response
		new SwingWorker<Void, String>() {

			@Override
			protected Void doInBackground() throws Exception {
				Iterator<GetWorkerNotesRes> notes = getHubClient().getWorkshopBlockingStub().getWorkerNotes(req);

				while (notes.hasNext()) {
					String note = notes.next().getNoteContent();
					publish(note);
					Thread.sleep(300);
				}
				return null;
			}

			@Override
			protected void process(List<String> chunks) {
				for (String note : chunks) {
					String currText = resultPane.getText();
					resultPane.setText(currText + "- " + note + "\n\n");
				}
			}

		}.execute(); // swingWorker
	} // getWorkerNotesButtonActionPerformed

	// ***************CLIENT-STREAM sendLabWork***************
	private void prepareSendLabButtonActionPerformed(ActionEvent evt) {
		resultPane.setText("Client stream prepared. Ready to send lab work snippets...\n\n");

		StreamObserver<SendLabWorkRes> responseObserver = new StreamObserver<>() {
			@Override
			public void onNext(SendLabWorkRes response) {
				resultPane.setText(resultPane.getText() + "Server finished!\nSubmission summary: "
						+ response.getSubmssionSummary() + " tasks received\n\n");
			}

			@Override
			public void onError(Throwable t) {
				resultPane.setText(resultPane.getText() + "Error: " + t.getMessage() + "\n\n");
			}

			@Override
			public void onCompleted() {
				resultPane.setText(resultPane.getText() + "*** Server acknowledgement ***\n\n");
			}
		};

		labRequestObserver = hubClient.getWorkshopAsyncStub().sendLabWork(responseObserver);
	} // prepareSendLabButtonActionPerformed

	private void sendLabSnippetButtonActionPerformed(ActionEvent evt) {
		if (labRequestObserver == null) {
			resultPane.setText("Please click 'Prepare Client Stream' first!\n" + resultPane.getText());
			return;
		}

		String snippet = labSnippetField.getText().trim();
		if (snippet.isEmpty()) {
			resultPane.setText("Please enter a work snippet!\n" + resultPane.getText());
			return;
		}

		SendLabWorkReq req = SendLabWorkReq.newBuilder().setWorkSnippet(snippet).build();
		labRequestObserver.onNext(req);

		resultPane.setText(resultPane.getText() + "Sent snippet: " + snippet + "\n");
		labSnippetField.setText(""); // clear for next snippet
	} // sendLabSnippetButtonActionPerformed

	private void doneLabButtonActionPerformed(ActionEvent evt) {
		if (labRequestObserver == null) {
			resultPane.setText("No active stream to finish!\n" + resultPane.getText());
			return;
		}

		labRequestObserver.onCompleted();
		labRequestObserver = null; // reset for next use
		resultPane.setText(resultPane.getText() + "Stream completed – waiting for server summary...\n\n");
	} // doneLabButtonActionPerformed

	// ***************BI-DI-STREAM rangeCheck***************
	private void prepareRangeButtonActionPerformed(ActionEvent event) {
		resultPane.setText("Bi-directional stream prepared. Send signals one by one...\n\n");

		StreamObserver<RangeCheckRes> responseObserver = new StreamObserver<>() {
			@Override
			public void onNext(RangeCheckRes response) {
				resultPane.setText(resultPane.getText() + "Signal received! Proximity: " + response.getProximityStatus()
						+ " >>> " + response.getStatusMessage() + "\n\n");
			}

			@Override
			public void onError(Throwable t) {
				resultPane.setText(resultPane.getText() + "Error: " + t.getMessage() + "\n\n");
			}

			@Override
			public void onCompleted() {
				resultPane.setText(resultPane.getText() + "Bi-directional stream completed\n\n");
			}
		};

		rangeRequestObserver = hubClient.getWorkshopAsyncStub().rangeCheck(responseObserver);
	} // prepareRangeButtonActionPerformed

	private void sendSignalButtonActionPerformed(ActionEvent event) {
		if (rangeRequestObserver == null) {
			resultPane.setText("Click 'Prepare Bi-Di Stream' first!\n" + resultPane.getText());
			return;
		}

		String signalText = signalField.getText().trim();
		if (signalText.isEmpty()) {
			resultPane.setText("Enter a signal strength!\n" + resultPane.getText());
			return;
		}

		try {
			float signal = Float.parseFloat(signalText);
			RangeCheckReq req = RangeCheckReq.newBuilder().setSignalStrength(signal).build();
			rangeRequestObserver.onNext(req);
			resultPane.setText(resultPane.getText() + "Sent signal: " + signal + "\n");
			signalField.setText("");
		} catch (NumberFormatException ex) {
			resultPane.setText("Invalid number! Use e.g. -45.5\n" + resultPane.getText());
		}
	} // sendSignalButtonActionPerformed

	private void doneRangeButtonActionPerformed(ActionEvent event) {
		if (rangeRequestObserver == null) {
			resultPane.setText("No active bi-di stream!\n" + resultPane.getText());
			return;
		}
		rangeRequestObserver.onCompleted();
		rangeRequestObserver = null;
		resultPane.setText(resultPane.getText() + "Bi-di stream finished – waiting for final responses...\n\n");
	} // doneRangeButtonActionPerformed

	// ==============================PRODUCTIVITY==============================
	// ***************UNARY ReportTaskProgress***************
	private void reportProgressButtonActionPerformed(ActionEvent event) {
		String taskId = taskIdField.getText().trim();
		String percentText = percentField.getText().trim();

		if (taskId.isEmpty() || percentText.isEmpty()) {
			resultPane.setText("Please fill in Task ID and Percent!\n" + resultPane.getText());
			return;
		}

		int percent = Integer.parseInt(percentText);
		ReportTaskProgressReq req = ReportTaskProgressReq.newBuilder().setTaskId(taskId).setPercentComplete(percent)
				.build();

		try {
			ReportTaskProgressRes res = getHubClient().getProductivityBlockingStub().reportTaskProgress(req);
			resultPane.setText(res.getAcknowledge() + "\n\n" + resultPane.getText());
		} catch (Exception e) {
			resultPane.setText("Error: " + e.getMessage() + "\n\n" + resultPane.getText());
		}
	} // reportProgressButtonActionPerformed

	// ***************SERVER-STREAM StreamProductivity***************
	private void streamProductivityButtonActionPerformed(ActionEvent event) {
		String siteId = siteIdField.getText().trim();
		if (siteId.isEmpty()) {
			resultPane.setText("Please enter a Site ID!\n" + resultPane.getText());
			return;
		}

		StreamProductivityReq req = StreamProductivityReq.newBuilder().setSiteId(siteId).build();
		resultPane.setText("Receiving productivity updates for site " + siteId + "...\n\n");

		new SwingWorker<Void, String>() {
			@Override
			protected Void doInBackground() throws Exception {
				Iterator<StreamProductivityRes> iterator = getHubClient().getProductivityBlockingStub()
						.streamProductivity(req);

				while (iterator.hasNext()) {
					String update = iterator.next().getWorkerUpdate();
					publish(update);
					Thread.sleep(1200);
				}
				return null;
			}

			@Override
			protected void process(List<String> chunks) {
				for (String update : chunks) {
					resultPane.setText(resultPane.getText() + ">>> " + update + "\n\n");
				}
			}
		}.execute();
	} // streamProductivityButtonActionPerformed

	// ***************CLIENT-STREAM VerifyHours***************
	private void prepareVerifyHoursButtonActionPerformed(ActionEvent evt) {
		resultPane.setText("Client stream prepared. Ready to send minutes worked...\n\n");

		StreamObserver<VerifyHoursRes> responseObserver = new StreamObserver<>() {
			@Override
			public void onNext(VerifyHoursRes response) {
				resultPane.setText(resultPane.getText() + "Total hours: " + response.getTotalHours() + "\n\n");
			}

			@Override
			public void onError(Throwable t) {
				resultPane.setText(resultPane.getText() + "Error: " + t.getMessage() + "\n\n");
			}

			@Override
			public void onCompleted() {
				resultPane.setText(resultPane.getText() + "Hours verification completed\n\n");
			}
		};

		hoursRequestObserver = hubClient.getProductivityAsyncStub().verifyHours(responseObserver);
	} // prepareVerifyHoursButtonActionPerformed

	private void sendMinutesButtonActionPerformed(ActionEvent event) {
		if (hoursRequestObserver == null) {
			resultPane.setText("Click 'Prepare Hours Stream' first!\n" + resultPane.getText());
			return;
		}

		String minutesText = minutesField.getText().trim();
		if (minutesText.isEmpty()) {
			resultPane.setText("Enter minutes worked!\n" + resultPane.getText());
			return;
		}

		int minutes = Integer.parseInt(minutesText);
		VerifyHoursReq req = VerifyHoursReq.newBuilder().setMinutesWorked(minutes).build();
		hoursRequestObserver.onNext(req);

		resultPane.setText(resultPane.getText() + "Sent " + minutes + " minutes\n");
		minutesField.setText("");
	} // sendMinutesButtonActionPerformed

	private void doneHoursButtonActionPerformed(ActionEvent event) {
		if (hoursRequestObserver == null) {
			resultPane.setText("No active hours stream!\n" + resultPane.getText());
			return;
		}
		hoursRequestObserver.onCompleted();
		hoursRequestObserver = null;
		resultPane.setText(resultPane.getText() + "Hours stream finished – waiting for total...\n\n");
	} // doneHoursButtonActionPerformed

	// ==============================GUARDIAN==============================
	// ***************UNARY MonitorSafety***************
	private void monitorSafetyButtonActionPerformed(ActionEvent evt) {
        String locationId = locationIdField.getText().trim();
        if (locationId.isEmpty()) {
            resultPane.setText("Please enter a Location ID!\n" + resultPane.getText());
            return;
        }

        MonitorSafetyReq req = MonitorSafetyReq.newBuilder().setLocationId(locationId).build();

        try {
            MonitorSafetyRes res = getHubClient().getGuardianBlockingStub().monitorSafety(req);
            resultPane.setText("PPE Protocol for " + locationId + ":\n" + res.getPpeProtocol() + "\n\n" + resultPane.getText());
        } catch (Exception e) {
            resultPane.setText("Error: " + e.getMessage() + "\n\n" + resultPane.getText());
        }
    } // monitorSafetyButtonActionPerformed

	// ***************B-DI-STREAM VerifyZoneSafety***************
	// TODO:
} // ProfessionalHubGUI
