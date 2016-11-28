package game.interaction.incident;

import java.util.ArrayList;

public class IncidentReporter extends ArrayList<IncidentListener> {

	private static final long serialVersionUID = -3561634928118509922L;

	public IncidentReporter() {
		super(2);
	}

	public void reportIncident(Object... specifications) {
		for (IncidentListener il : this) {
			il.incidentReported(specifications);
		}
	}
}
