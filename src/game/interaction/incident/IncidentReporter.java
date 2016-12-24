package game.interaction.incident;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * IncidentReporters are used to maintain multiple IncidentListeners that are
 * listening for the same exact event to occur.
 * 
 * @author Akarsh
 *
 */
public class IncidentReporter {

    /**
     * The list of IncidentListeners listening to this IncidentReporter.
     */
    private final List<IncidentListener> listeners;

    /**
     * The list of listeners are only listening to one broadcast.
     */
    private final List<IncidentListener> listenersOnlyOnce;

    /**
     * Initializes the IncidentReporter.
     */
    public IncidentReporter() {
	listeners = new ArrayList<>(3);
	listenersOnlyOnce = new ArrayList<>(1);
    }

    /**
     * Notifies all of the IncidentListeners that a incident has been reported
     * with the given specifications.
     * 
     * @param specifications
     *            for the listeners
     */
    public void reportIncident(Object... specifications) {
	Iterator<IncidentListener> it = listeners.iterator();
	while (it.hasNext()) {
	    IncidentListener listener = it.next();

	    listener.incidentReported(specifications);

	    if (listenersOnlyOnce.contains(listener)) {
		it.remove();
		listenersOnlyOnce.remove(listener);
	    }
	}
    }

    /**
     * @return the list of listeners.
     */
    public List<IncidentListener> getListeners() {
	return listeners;
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
     * broadcast of this IncidentReporter. If the onlyOnce paramter is true then
     * the listener will be removed after the first broadcast; it will only be
     * notified once.
     * 
     * @param listener
     * @param onlyOnce
     */
    public void add(IncidentListener listener, boolean onlyOnce) {
	listeners.add(listener);
	if (onlyOnce) {
	    listenersOnlyOnce.add(listener);
	}
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
