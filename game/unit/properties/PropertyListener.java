package game.unit.properties;

import java.util.Arrays;

import game.interaction.incident.IncidentListener;
import game.unit.Unit;

public interface PropertyListener<T> extends IncidentListener {

	/**
	 * @param unit
	 * @param property
	 * @param oldValue
	 * @param newValue
	 * @param specifications
	 *            optional additional arguments to specify the change
	 */
	public abstract void propertyChanged(T oldValue, T newValue, Property<T> property, Unit unit,
			Object... specifications);

	@SuppressWarnings("unchecked")
	@Override
	public default void incidentReported(Object... specifications) {
		if (specifications.length > 4)
			propertyChanged((T) specifications[0], (T) specifications[1], (Property<T>) specifications[2],
					(Unit) specifications[3], Arrays.copyOfRange(specifications, 4, specifications.length));
		else
			propertyChanged((T) specifications[0], (T) specifications[1], (Property<T>) specifications[2],
					(Unit) specifications[3]);
	}

}
