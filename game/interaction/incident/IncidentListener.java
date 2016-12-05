package game.interaction.incident;

/**
 * Listener for certain events that take place in game. Will be called most
 * likely by a incidentReporter that will trigger the incidentReported method.
 * The casts done inside of this method should take into consideration what will
 * be calling the method and with what arguments.
 * 
 * @author Akarsh
 *
 */
@FunctionalInterface
public interface IncidentListener {

	/**
	 * Runs the implemented code when the incident is reported. Be careful when
	 * casting the objects by knowing what will call it.
	 * 
	 * @param specifications
	 *            the caller of the method will send these arguments to it
	 */
	public abstract void incidentReported(Object... specifications);

}