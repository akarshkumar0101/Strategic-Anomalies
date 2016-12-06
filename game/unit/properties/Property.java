package game.unit.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import game.interaction.incident.IncidentListener;
import game.interaction.incident.IncidentReporter;
import game.unit.Unit;

/**
 * Properties are used to contain and track changes in all properties of a Unit.
 * The actual property should be a final, disposable object that can and should
 * be replaced regularly.
 * 
 * @author Akarsh
 *
 * @param <T>
 *            the type of property base. Example: Integer, Coordinate, etc.
 */
public abstract class Property<T> {

	/**
	 * The Unit this Property is tied to.
	 */
	protected final Unit unit;

	/**
	 * The IncidentReporter used to track PropertyListeners listening to changes
	 * in the property of this.
	 */
	private final IncidentReporter changeReporter;

	/**
	 * The current value of the property.
	 */
	protected T property;

	private final List<PropertyEffect<T>> propEffects = new ArrayList<>(2);

	/**
	 * Initializes the Property with the given Unit and the given initial Value.
	 * 
	 * @param unit
	 *            Unit the Property is assigned to
	 * @param initValue
	 *            the initial value of the property
	 */
	public Property(Unit unit, T initValue) {
		this.unit = unit;
		this.property = initValue;
		changeReporter = new IncidentReporter();
	}

	/**
	 * 
	 * @return the Unit this Property is assigned to.
	 */
	public Unit getUnitOwner() {
		return unit;
	}

	public List<PropertyEffect<T>> getPropEffects() {
		return propEffects;
	}

	/**
	 * TODO: Javadoc coming soon.
	 */
	public void addPropEffect(PropertyEffect<T> effect) {
		T before = getAffectedProp();
		propEffects.add(effect);
		T after = getAffectedProp();
		if (!after.equals(before)) {
			propertyChanged(before, after);
		}
	}

	/**
	 * TODO: Javadoc coming soon.
	 */
	public void removePropEffect(PropertyEffect<T> effect) {
		T before = getAffectedProp();
		propEffects.remove(effect);
		T after = getAffectedProp();
		if (!after.equals(before)) {
			propertyChanged(before, after);
		}
	}

	private T getAffectedProp(T prop) {
		for (PropertyEffect<T> propEffect : propEffects) {
			prop = propEffect.affectProperty(prop);
		}
		return prop;
	}

	private T getAffectedProp() {
		return getAffectedProp(property);
	}

	public void updatePropEffectExistances() {
		Iterator<PropertyEffect<T>> it = propEffects.iterator();
		while (it.hasNext()) {
			PropertyEffect<T> propEffect = it.next();
			if (!propEffect.shouldExist()) {
				it.remove();
			}
		}
	}

	/**
	 * @return the current value of the property with effects.
	 */
	public T getProp() {
		return getAffectedProp();
	}

	/**
	 * Adds the given PropertyListener<T> to the list of PropertyListeners. It
	 * will be notified on Property change.
	 * 
	 * @param pl
	 *            the PropertyListener<T> to be notified
	 */
	public void addPropertyListener(PropertyListener<T> pl) {
		changeReporter.add(pl);
	}

	/**
	 * Removes the given PropertyListener<T> from the list of PropertyListeners.
	 * It will be no longer be notified on Property change.
	 * 
	 * @param pl
	 *            the PropertyListener<T> to no longer be notified
	 */
	public void removePropertyListener(PropertyListener<T> pl) {
		changeReporter.remove(pl);
	}

	/**
	 * Internal method that will be called by the subclass when the value of the
	 * property is changed.
	 * 
	 * @param oldValue
	 *            the old value of the property
	 * @param newValue
	 *            the new value of the property
	 * @param specifications
	 *            additional specifications about the circumstances of the
	 *            property change
	 */
	protected void propertyChanged(T oldValue, T newValue, Object... specifications) {
		for (IncidentListener il : changeReporter) {
			il.incidentReported(oldValue, newValue, this, unit, specifications);
		}
	}
}
