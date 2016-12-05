package game.interaction.incident;

import java.util.ArrayList;

/**
 * IncidentReporters are used to maintain multiple IncidentListeners that are
 * listening for the same exact event to occur.
 * 
 * @author Akarsh
 *
 */
public class IncidentReporter extends ArrayList<IncidentListener> {

	private static final long serialVersionUID = -3561634928118509922L;

	/**
	 * Initializes the IncidentReporter.
	 */
	public IncidentReporter() {
		super(2);
	}

	/**
	 * Notifies all of the IncidentListeners that a incident has been reported
	 * with the given specifications.
	 * 
	 * @param specifications
	 *            for the listeners
	 */
	public void reportIncident(Object... specifications) {
		for (IncidentListener il : this) {
			il.incidentReported(specifications);
		}
	}
}
