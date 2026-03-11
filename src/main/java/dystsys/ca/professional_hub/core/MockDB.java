package dystsys.ca.professional_hub.core;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MockDB {

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
    public static final List<String> WORK_SNIPPETS = Arrays.asList("Module A: Initialized safety protocols.",
	    "Module B: Calibrated workshop sensors to 0.05 precision.", "Module C: Completed material stress test report.",
	    "Module D: Final synchronization with local hub.");

    // signal_strength
    public static final List<Float> SIGNAL_STRENGTH = Arrays.asList(-45.5f, -75.0f, -85.2f, -10.0f, 0.25f);

    // task_id
    public static final Set<String> TASK_ID = Set.of("T-100", "T-200", "T-300", "T-400", "T-500");

    // site_id
    public static final Set<String> SITE_ID = Set.of("S-AAA", "S-ABC", "S-CCC", "S-ZET", "S-OAT");

    // worker_update
    public static final Map<String, List<String>> WORKER_UPDATE = Map.of("S-AAA",
	    List.of("John Doe - Active - Phase 1: Foundation layout verified.",
		    "Jane Smith - Pending - Phase 2: Structural steel arrival pending.",
		    "Bob Wilson - On Break - Phase 3: Safety clearance granted."),
	    "S-ABC",
	    List.of("Alice Brown - Active - Phase 1: Wiring conduits installed.",
		    "Charlie Day - Active - Phase 2: Power testing in progress.",
		    "Eve White - On Site - Phase 3: Inspection scheduled for 14:00."),
	    "S-CCC",
	    List.of("Frank Miller - Active - Phase 1: Site survey complete.",
		    "Grace Hall - In Transit - Phase 2: Equipment transport initialized."),
	    "S-ZET",
	    List.of("Hank Young - Active - Phase 1: Soil compaction confirmed.", "Ivy King - Active - Phase 2: Survey equipment deployed.",
		    "Jack Lee - Active - Phase 3: Perimeter fencing installation."),
	    "S-OAT",
	    List.of("Kelly Scott - Active - Phase 1: Electrical grid baseline established.",
		    "Liam Adams - Active - Phase 2: Transformer maintenance completed.",
		    "Mia Evans - On Site - Phase 3: Grid stability verification successful."));
    
    // ppe_protocol
    public static final Map<String, String> PPE_PROTOCOLS = Map.of(
	    "LOC-001", "Hard hat, safety glasses, steel-toed boots.",
	    "LOC-002", "Respirator, chemical-resistant gloves, apron.",
	    "LOC-003", "High-visibility vest, ear protection, hard hat.",
	    "LOC-004", "Full-body harness, tether, non-slip footwear.",
	    "LOC-005", "Arc flash suit, insulated gloves, face shield."
	);
    
    // zone_instruction
    public static final Map<String, String> ZONE_INSTRUCTIONS = Map.of(
	    "ZONE-RED", "CRITICAL: Oxygen levels low. Evacuate immediately.",
	    "ZONE-BLUE", "CAUTION: Wet floors. Use non-slip footwear.",
	    "ZONE-YELLOW", "NOTICE: High-traffic area. Watch for forklifts.",
	    "ZONE-GREEN", "SAFE: No active hazards detected.",
	    "ZONE-ORANGE", "WARNING: Elevated temperature. Limit exposure to 15 mins."
	);
}
