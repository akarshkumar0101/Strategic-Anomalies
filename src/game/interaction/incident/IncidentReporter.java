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
     * Listeners listening to this for broadcasts from this reporter. The boolean
     * value suggests if the listener is only listening for one broadcast. If it is
     * true, the listener will be removed from the list of listeners after it has
     * received one broadcast.
     */
    private final List<IncidentListener> listeners;

    /**
     * Initializes the IncidentReporter.
     */
    public IncidentReporter() {
	listeners = new ArrayList<>();
    }

    /**
     * Notifies all of the IncidentListeners that a incident has been reported with
     * the given specifications.
     *
     * @param specifications
     *            for the listeners
     */
    public void reportIncident(Object... args) {
	Iterator<IncidentListener> it = listeners.iterator();
	while (it.hasNext()) {
	    IncidentListener listener = it.next();

	    listener.incidentReported(args);

	    if (listener.shouldExistCondition().performCondition(this, listener)) {
		it.remove();
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
	listeners.add(listener);
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
    public void add(IncidentListener listener_, boolean onlyOnce) {
	ConditionalIncidentListener listener = new ConditionalIncidentListener(
		onlyOnce ? Condition.falseCondition : Condition.trueCondition) {

	    @Override
	    public void incidentReported(Object... specifications) {
		listener_.incidentReported(specifications);
	    }
	};
	add(listener);
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