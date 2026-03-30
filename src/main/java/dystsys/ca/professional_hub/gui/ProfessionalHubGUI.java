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

import com.generated.workshop.grpc.CheckInWorkerReq;
import com.generated.workshop.grpc.CheckInWorkerRes;
import com.generated.workshop.grpc.GetWorkerNotesReq;
import com.generated.workshop.grpc.GetWorkerNotesRes;
import com.generated.workshop.grpc.SendLabWorkReq;

import dystsys.ca.professional_hub.clients.ProfessionalHubClient;
import io.grpc.stub.StreamObserver;

public class ProfessionalHubGUI extends JFrame {
	private static final long serialVersionUID = 1L;

	// central client
	private ProfessionalHubClient hubClient;

	// GUI components
	private JTabbedPane tabbedPane;
	private JTextPane resultPane;

	// Workshop fields
	private JTextField workerIdField;
	private JTextField workshopIdField;
	private JTextField getNotesWorkshopIdField;
	private JTextField labSnippetField;
	
	private JButton checkInButton;
	private JButton getNotesButton;
	private JButton prepareSendLabButton;
	private JButton sendLabSnippetButton;
	private JButton doneLabButton;

	private StreamObserver<SendLabWorkReq> labRequestObserver; // this is needed for send and done buttons
	
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

		JLabel lblUnaryWorkshopCheat = new JLabel("W-1001 & WS-001"); // cheat code for testing
		lblUnaryWorkshopCheat.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblUnaryWorkshopCheat.setForeground(new Color(255, 0, 0));
		unary.add(lblUnaryWorkshopCheat);

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
		
		JLabel lblServerStreamWorkshopCheat = new JLabel("WS-001"); // cheat code for testing
		lblServerStreamWorkshopCheat.setForeground(Color.RED);
		lblServerStreamWorkshopCheat.setFont(new Font("Tahoma", Font.BOLD, 14));
		server_streaming.add(lblServerStreamWorkshopCheat);

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
		// TODO:

		// *****BI-DI-STREAM section
		// TODO:

		// ***************tabs***************
		tabbedPane.addTab("Workshop", workshopPanel);
		// TODO:
		tabbedPane.addTab("Productivity", new JPanel());
		tabbedPane.addTab("Guardian", new JPanel());

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

		// ***************action listeners***************
		checkInButton.addActionListener(this::checkInWorkerButtonActionPerformed);
		getNotesButton.addActionListener(this::getWorkerNotesButtonActionPerformed);
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
} // ProfessionalHubGUI
