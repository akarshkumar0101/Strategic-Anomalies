package game.unit.properties;

import java.util.Arrays;

import game.interaction.incident.IncidentListener;
import game.unit.Unit;

/**
 * A subclass of the IncidentListener that listens specifically for changes in
 * the value of a property.
 * 
 * @author Akarsh
 *
 * @param <T>
 */
@FunctionalInterface
public interface PropertyListener<T> extends IncidentListener {

    /**
     * Runs the implemented code when the property changes its value.
     * 
     * @param oldValue
     * @param newValue
     * @param property
     * @param unit
     * @param specifications
     */
    public abstract void propertyChanged(T oldValue, T newValue, Unit unitOwner, Property<T> property,
	    Object... specifications);

    /**
     * This method should be ignored, as it is only a gateway from an
     * IncidentListener to a PropertyListener. This method lets the Property
     * keep track of its PropertyListeners using a regular IncidentReporter.
     */
    @SuppressWarnings("unchecked")
    @Override
    public default void incidentReported(Object... specifications) {
	if (specifications.length > 4) {
	    propertyChanged((T) specifications[0], (T) specifications[1], (Unit) specifications[2],
		    (Property<T>) specifications[3], Arrays.copyOfRange(specifications, 4, specifications.length));
	} else {
	    propertyChanged((T) specifications[0], (T) specifications[1], (Unit) specifications[2],
		    (Property<T>) specifications[3]);
	}
    }
    // oldValue, newValue, unitOwner, this, specifications
}
