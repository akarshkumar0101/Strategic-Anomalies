package game.interaction.incident;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * IncidentReporters are used to maintain multiple IncidentListeners that are
 * listening for the same exact event to occur.
 * 
 * @author Akarsh
 *
 */
public class IncidentReporter {

    /**
     * Listeners listening to this for broadcasts from this reporter. The boolean
     * value suggests if the listener is only listening for one broadcast. If it is
     * true, the listener will be removed from the list of listeners after it has
     * received one broadcast.
     */
    private final Map<IncidentListener, Boolean> listeners;

    /**
     * Initializes the IncidentReporter.
     */
    public IncidentReporter() {
	listeners = new HashMap<>();
    }

    /**
     * Notifies all of the IncidentListeners that a incident has been reported with
     * the given specifications.
     * 
     * @param specifications
     *            for the listeners
     */
    public void reportIncident(Object... args) {
	Iterator<IncidentListener> it = listeners.keySet().iterator();
	while (it.hasNext()) {
	    IncidentListener listener = it.next();

	    listener.incidentReported(args);

	    if (listeners.get(listener)) {
		it.remove();
	    }
	}
    }

    /**
     * @return the list of listeners.
     */
    public Set<IncidentListener> getListeners() {
	return listeners.keySet();
    }

    /**
     * Adds the given listener to the list of listeners that will be notified on
     * broadcast of this IncidentReporter.
     * 
     * @param listener
     */
    public void add(IncidentListener listener) {
	add(listener, false);
    }

    /**
     * Adds the given listener to the list of listeners that will be notified on
     * broadcast of this IncidentReporter. If the onlyOnce paramter is true then the
     * listener will be removed after the first broadcast; it will only be notified
     * once.
     * 
     * @param listener
     * @param onlyOnce
     */
    public void add(IncidentListener listener, boolean onlyOnce) {
	listeners.put(listener, onlyOnce);
    }

    /**
     * Removes the given listener from the list of listeners to be notified on
     * broadcast.
     * 
     * @param listener
     */
    public void remove(IncidentListener listener) {
	listeners.remove(listener);
    }

}