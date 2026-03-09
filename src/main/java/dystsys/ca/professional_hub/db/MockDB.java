package dystsys.ca.professional_hub.db;

import java.util.List;
import java.util.Map;

public class MockDB {
    public static final Map<String, List<String>> WORKSHOP_NOTES = Map.of(
            "WS-001", List.of(
                    "Ensure all personal protective equipment is inspected prior to commencement of work.",
                    "Report any defective tools or machinery to the site supervisor immediately.",
                    "Maintain a clear workspace to prevent trip hazards and facilitate efficient operations.",
                    "Conduct a pre-shift inventory check of all small hand tools.",
                    "Log all work progress in the digital management system before exiting the floor."),
            
            "WS-002", List.of(
                    "Verify calibration status of all precision measurement instruments before use.",
                    "Ensure secondary containment protocols are active when handling lubricants.",
                    "Clean all debris from workstations at the conclusion of every session.",
                    "Check lighting levels in the work area to ensure visibility standards are met.",
                    "Double-check ventilation systems are operating at full capacity during chemical application."),
            
            "WS-003", List.of(
                    "Adhere strictly to the established lockout/tagout procedures during equipment maintenance.",
                    "Review the updated safety protocols for the current facility module.",
                    "Verify that emergency exits remain unobstructed at all times.",
                    "Update the hazardous materials log if any chemicals have been restocked.",
                    "Confirm that the emergency shut-off valve is accessible and clearly marked.")
    );
}
