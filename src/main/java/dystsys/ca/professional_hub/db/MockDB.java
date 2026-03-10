package dystsys.ca.professional_hub.db;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MockDB {
	
	// ***** mock for CheckInWorker *****
	// workershop_id
	public static final Map<String, List<String>> WORKSHOP_NOTES = Map.of("WS-001",
			List.of("Ensure all personal protective equipment is inspected prior to commencement of work.",
					"Report any defective tools or machinery to the site supervisor immediately.",
					"Maintain a clear workspace to prevent trip hazards and facilitate efficient operations.",
					"Conduct a pre-shift inventory check of all small hand tools.",
					"Log all work progress in the digital management system before exiting the floor."),

			"WS-002",
			List.of("Verify calibration status of all precision measurement instruments before use.",
					"Ensure secondary containment protocols are active when handling lubricants.",
					"Clean all debris from workstations at the conclusion of every session.",
					"Check lighting levels in the work area to ensure visibility standards are met.",
					"Double-check ventilation systems are operating at full capacity during chemical application."),

			"WS-003",
			List.of("Adhere strictly to the established lockout/tagout procedures during equipment maintenance.",
					"Review the updated safety protocols for the current facility module.",
					"Verify that emergency exits remain unobstructed at all times.",
					"Update the hazardous materials log if any chemicals have been restocked.",
					"Confirm that the emergency shut-off valve is accessible and clearly marked."));

	// worker_id
	public static final Set<String> REGISTERED_WORKERS = Set.of("W-1001", "W-1002", "W-1003", "W-1004");
	
	// work_snippets
	public static final List<String> WORK_SNIPPETS = Arrays.asList(
		    "Module A: Initialized safety protocols.",
		    "Module B: Calibrated workshop sensors to 0.05 precision.",
		    "Module C: Completed material stress test report.",
		    "Module D: Final synchronization with local hub."
		);
	
	// signal_strength
	public static final List<Float> SIGNAL_STRENGTH = Arrays.asList(-45.5f, -75.0f, -85.2f, -10.0f, 0.25f);
}
