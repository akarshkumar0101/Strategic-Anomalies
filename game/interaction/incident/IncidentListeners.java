package game.interaction.incident;

import java.util.ArrayList;

public class IncidentListeners extends ArrayList<IncidentListener> {

	private static final long serialVersionUID = -3561634928118509922L;

	public IncidentListeners() {
		super(2);
	}

	public void triggerIncident(Object... specifications) {
		for (IncidentListener il : this) {
			il.incidentReported(specifications);
		}
	}
}
