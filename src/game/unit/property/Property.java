package game.unit.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import game.interaction.effect.EffectType;
import game.interaction.incident.IncidentReporter;
import game.unit.Unit;

/**
 * Properties are used to contain and track changes in any properties of a Unit.
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
public class Property<T> {

    /**
     * The Unit this Property is tied to.
     */
    private final Unit unitOwner;

    /**
     * The IncidentReporter used to track PropertyListeners listening to changes in
     * the property of this.
     */
    private final IncidentReporter changeReporter;

    /**
     * The original value of this property at the start of the game.
     */
    private final T defaultValue;

    /**
     * The PropertyEffects that have permanently affected the property. Just used to
     * keep track of what has interacted with this property.
     */
    private final List<PropertyEffect<T>> permanentPropEffects;
    /**
     * The PropertyEffects that are currently affecting the property.
     */
    private final List<PropertyEffect<T>> activePropEffects;

    /**
     * The value of the property after permanent effects have been applied. Note*:
     * needs to be updated.
     */
    private T currentBaseValue;
    /**
     * The current value of the property, calculated after the effects of the
     * PropertyEffects affecting it. The value should be up to date before being
     * used. Note*: needs to be updated.
     */
    private T currentValue;

    /**
     * The last value of the property that the listeners were notified of. Used to
     * tell if the value was changed. Note*: needs to be updated.
     */
    private T lastCurrentValue;

    /**
     * This comparator orders the PropertyEffects in a higher priority gets more of
     * a "last say." Priority 10 effects get the last say always in how to affect
     * the property.
     */
    private static final Comparator<PropertyEffect<?>> normalPropEffectComparator = (effect1,
	    effect2) -> (int) ((effect1.getPriority() - effect2.getPriority()) * 1000);

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
	this.lastCurrentValue = this.currentValue = this.currentBaseValue = defaultValue = initValue;

	changeReporter = new IncidentReporter();

	permanentPropEffects = new ArrayList<>();
	activePropEffects = new ArrayList<>();
    }

    /**
     *
     * @return the Unit this Property is assigned to
     */
    public Unit getUnitOwner() {
	return unitOwner;
    }

    /**
     * @return the original value of this property at the start of the game
     */
    public T getDefaultValue() {
	return defaultValue;
    }

    /**
     * @return the current value of the property after all effects have affected it
     */
    public T getValue() {
	return currentValue;
    }

    /**
     * Updates the base value of the property T defaultPropValue and
     * List<PropertyEffects<T>> permanentPropEffects. and permanent effects.
     */
    private void updateBaseValue() {
	T val = defaultValue;

	for (PropertyEffect<T> propEffect : permanentPropEffects) {
	    val = propEffect.affectProperty(val);
	}
	currentBaseValue = val;

    }

    /**
     * Updates the current value using the T currentBasePropValue and
     * List<PropertyEffects<T>> activePropEffects.
     */
    private void updateCurrentValue() {
	T val = currentBaseValue;

	for (PropertyEffect<T> propEffect : activePropEffects) {
	    val = propEffect.affectProperty(val);
	}
	currentValue = val;
    }

    /**
     * Should be used if the active/passive PropertyEffects use other game
     * dependencies to calculate values(uses health of another unit, which could
     * change over time). This value updates the values and can be called by a
     * IncidentListener or anything else.
     */
    public void updateValue() {
	updateBaseValue();
	updateCurrentValue();

	valueUpdated();
    }

    /**
     * @return the list of effects that are currently affecting the value of this
     *         property.
     */
    public List<PropertyEffect<T>> getActivePropEffects() {
	return activePropEffects;
    }

    /**
     * Sets the base value of the property as the value by adding a permanent
     * PropertyEffect that changes the base value to that.
     *
     * @param value
     *            the new value to set it to
     */
    public void setValue(T value) {
	setValue(value, unitOwner);
    }

    public void setValue(T value, Object source) {
	addPropEffect(new PropertyEffect<T>(EffectType.PERMANENT, source, 0) {
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
	if (effect.getEffectType() == EffectType.PERMANENT) {
	    permanentPropEffects.add(effect);

	    updateBaseValue();
	} else {
	    activePropEffects.add(effect);

	    // only place needed to sort the properties, because properties are
	    // being added.
	    Collections.sort(activePropEffects, Property.normalPropEffectComparator);
	}

	updateCurrentValue();

	valueUpdated();
    }

    /**
     * Remove an effect that is currently affecting the value of this property.
     */
    public void removePropEffect(PropertyEffect<T> effect) {
	if (effect.getEffectType() == EffectType.PERMANENT) {
	    permanentPropEffects.remove(effect);

	    updateBaseValue();
	} else {
	    activePropEffects.remove(effect);
	}

	updateCurrentValue();

	valueUpdated();
    }

    /**
     * Adds the given PropertyListener<T> to the list of PropertyListeners. It will
     * be notified on Property change.
     *
     * @param pl
     *            the PropertyListener<T> to be notified
     */
    public void addPropertyListener(PropertyListener<T> pl) {
	changeReporter.add(pl);
    }

    /**
     * Removes the given PropertyListener<T> from the list of PropertyListeners. It
     * will be no longer be notified on Property change.
     *
     * @param pl
     *            the PropertyListener<T> to no longer be notified
     */
    public void removePropertyListener(PropertyListener<T> pl) {
	changeReporter.remove(pl);
    }

    /**
     * Runs when it is suggested that the property value may have changed. If it did
     * change, then run the code for it to register the change and notify the
     * listeners.
     */
    protected void valueUpdated() {
	if (!currentValue.equals(lastCurrentValue)) {
	    Object[] specifications = getSpecificationsOfPropertyChange(lastCurrentValue, currentValue);
	    notifyPropertyChanged(lastCurrentValue, currentValue, specifications);
	    lastCurrentValue = currentValue;
	}
    }

    /**
     * Specify the change of the property with an Object[], should be overwritten
     * for proper use.
     *
     * @param oldValue
     * @param newValue
     * @return the Object[] specifications of the change
     */
    protected Object[] getSpecificationsOfPropertyChange(T oldValue, T newValue) {
	return null;
    }

    /**
     * Internal method that will be called by the subclass when the value of the
     * property is changed. <br>
     * <br>
     * The property listeners will be notified with the following arguments:
     * incidentReported(T oldValue, T newValue, Unit unit, Property<T>
     * changedProperty, Object[] specifications);
     *
     * @param oldValue
     *            the old value of the property
     * @param newValue
     *            the new value of the property
     * @param specifications
     *            additional specifications about the circumstances of the property
     *            change
     */
    private void notifyPropertyChanged(T oldValue, T newValue, Object... specifications) {
	changeReporter.reportIncident(oldValue, newValue, unitOwner, this, specifications);
    }
}
