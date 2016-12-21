package game.unit.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import game.interaction.effect.EffectType;
import game.interaction.incident.IncidentListener;
import game.interaction.incident.IncidentReporter;
import game.unit.Unit;

/**
 * Properties are used to contain and track changes in all properties of a Unit.
 * The actual property should be a final, disposable object that can and should
 * be replaced regularly. <br>
 * <br>
 * Property values change ONLY according to PropertyEffects. Permanent effects,
 * such as setting the armor permanently to 5, should be declared permanent.
 * Active effects will be piled on according to priority.
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
	protected final Unit unitOwner;

	/**
	 * The IncidentReporter used to track PropertyListeners listening to changes
	 * in the property of this.
	 */
	private final IncidentReporter changeReporter;

	/**
	 * The original value of this property at the start of the game.
	 */
	protected final T defaultPropValue;

	/**
	 * The current value of the property, calculated after the effects of the
	 * PropertyEffects affecting it. The value should be up to date before being
	 * used.
	 */
	private T currentPropValue;

	/**
	 * The PropertyEffects that are currently effecting the property.
	 */
	private final List<PropertyEffect<T>> propEffects = new ArrayList<>(2);

	/**
	 * This comparator orders the PropertyEffects in a higher priority gets more
	 * of a "last say." Priority 10 effects get the last say always in how to
	 * affect the property.
	 */
	private static final Comparator<PropertyEffect<?>> normalPropEffectComparator = new Comparator<PropertyEffect<?>>() {
		@Override
		public int compare(PropertyEffect<?> effect1, PropertyEffect<?> effect2) {
			return (int) ((effect1.getPriority() - effect2.getPriority()) * 1000);
		}
	};

	/**
	 * Initializes the Property with the given Unit and the given initial Value.
	 * 
	 * @param unit
	 *            Unit the Property is assigned to
	 * @param initValue
	 *            the initial value of the property
	 */
	public Property(Unit unitOwner, T initValue) {
		this.unitOwner = unitOwner;
		this.currentPropValue = this.defaultPropValue = initValue;
		changeReporter = new IncidentReporter();
	}

	/**
	 * 
	 * @return the Unit this Property is assigned to
	 */
	public Unit getUnitOwner() {
		return unitOwner;
	}

	/**
	 * @return the current value of the property after all effects have affected
	 *         it
	 */
	public T getCurrentPropertyValue() {
		return currentPropValue;
	}

	/**
	 * @return the original value of this property at the start of the game
	 */
	public T getDefaultPropertyValue() {
		return defaultPropValue;
	}

	/**
	 * Updates the current value of the property based on the PropertyEffects
	 * affecting the property.
	 */
	private void updateCurrentPropertyValue() {
		T val = defaultPropValue;

		for (PropertyEffect<T> propEffect : propEffects) {
			val = propEffect.affectProperty(val);
		}
		currentPropValue = val;
	}

	/**
	 * @return the list of effects that are currently affecting the value of
	 *         this property.
	 */
	public List<PropertyEffect<T>> getPropEffects() {
		return propEffects;
	}

	/**
	 * Sets the base value of the property as the value by adding a permanent
	 * PropertyEffect that changes the base value to that.
	 * 
	 * @param value
	 *            the new value to set it to
	 */
	public void setPropertyValue(T value) {
		addPropEffect(new PropertyEffect<T>(EffectType.PERMANENT, unitOwner, null, 0) {
			@Override
			public T affectProperty(T init) {
				return value;
			}
		});
	}

	/**
	 * Add an effect to the property that affects the value of the property over
	 * time/another limit.
	 */
	public void addPropEffect(PropertyEffect<T> effect) {
		T before = currentPropValue;

		propEffects.add(effect);

		// only place needed to sort the properties, because properties are
		// being added.
		Collections.sort(propEffects, normalPropEffectComparator);
		updateCurrentPropertyValue();
		T after = currentPropValue;

		if (!after.equals(before)) {
			propertyChanged(before, after);
		}
	}

	/**
	 * Remove an effect that is currently affecting the value of this property.
	 */
	public void removePropEffect(PropertyEffect<T> effect) {
		T before = currentPropValue;

		propEffects.remove(effect);

		updateCurrentPropertyValue();
		T after = currentPropValue;

		if (!after.equals(before)) {
			propertyChanged(before, after);
		}
	}

	/**
	 * Updates and makes sure only non-expired effects are acting on the
	 * property.
	 */
	public void updatePropEffectExistances() {
		T before = currentPropValue;

		Iterator<PropertyEffect<T>> it = propEffects.iterator();
		while (it.hasNext()) {
			PropertyEffect<T> propEffect = it.next();
			if (propEffect.hasExistenceCondition() && !propEffect.shouldExist()) {
				it.remove();
			}
		}

		updateCurrentPropertyValue();
		T after = currentPropValue;

		if (!after.equals(before)) {
			propertyChanged(before, after);
		}

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
			il.incidentReported(oldValue, newValue, this, unitOwner, specifications);
		}
	}
}
