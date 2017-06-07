package game.unit.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
public abstract class Property<T> {

    /**
     * The Unit this Property is tied to.
     */
    private final Unit unitOwner;

    /**
     * The IncidentReporter used to track PropertyListeners listening to changes
     * in the property of this.
     */
    private final IncidentReporter changeReporter;

    /**
     * The original value of this property at the start of the game.
     */
    private final T defaultPropValue;

    /**
     * The PropertyEffects that have permanently affected the property. Just
     * used to keep track of what has interacted with this property.
     */
    private final List<PropertyEffect<T>> permanentPropEffects;
    /**
     * The PropertyEffects that are currently affecting the property.
     */
    private final List<PropertyEffect<T>> activePropEffects;

    /**
     * The value of the property after permanent effects have been applied.
     * Note*: needs to be updated.
     */
    private T currentBasePropValue;
    /**
     * The current value of the property, calculated after the effects of the
     * PropertyEffects affecting it. The value should be up to date before being
     * used. Note*: needs to be updated.
     */
    private T currentPropValue;

    /**
     * The last value of the property that the listeners were notified of. Used
     * to tell if the value was changed. Note*: needs to be updated.
     */
    private T lastCurrentPropValue;

    /**
     * This comparator orders the PropertyEffects in a higher priority gets more
     * of a "last say." Priority 10 effects get the last say always in how to
     * affect the property.
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
	this.lastCurrentPropValue = this.currentPropValue = this.currentBasePropValue = this.defaultPropValue = initValue;

	changeReporter = new IncidentReporter();

	permanentPropEffects = new ArrayList<>(5);
	activePropEffects = new ArrayList<>(2);
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
     * Updates the base value of the property T defaultPropValue and
     * List<PropertyEffects<T>> permanentPropEffects. and permanent effects.
     */
    private void updateBasePropertyValue() {
	T val = defaultPropValue;

	for (PropertyEffect<T> propEffect : permanentPropEffects) {
	    val = propEffect.affectProperty(val);
	}
	currentBasePropValue = val;

    }

    /**
     * Updates the current value using the T currentBasePropValue and
     * List<PropertyEffects<T>> activePropEffects.
     */
    private void updateCurrentPropertyValue() {
	T val = currentBasePropValue;

	for (PropertyEffect<T> propEffect : activePropEffects) {
	    val = propEffect.affectProperty(val);
	}
	currentPropValue = val;
    }

    /**
     * @return the list of effects that are currently affecting the value of
     *         this property.
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
    public void setPropertyValue(T value) {
	setPropertyValue(value, unitOwner);
    }

    public void setPropertyValue(T value, Object source) {
	addPropEffect(new PropertyEffect<T>(EffectType.PERMANENT, source, null, 0) {
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

	    updateBasePropertyValue();
	} else {
	    activePropEffects.add(effect);

	    // only place needed to sort the properties, because properties are
	    // being added.
	    Collections.sort(activePropEffects, normalPropEffectComparator);
	}

	updateCurrentPropertyValue();

	valueUpdated();
    }

    /**
     * Remove an effect that is currently affecting the value of this property.
     */
    public void removePropEffect(PropertyEffect<T> effect) {
	if (effect.getEffectType() == EffectType.PERMANENT) {
	    permanentPropEffects.remove(effect);

	    updateBasePropertyValue();
	} else {
	    activePropEffects.remove(effect);
	}

	updateCurrentPropertyValue();

	valueUpdated();
    }

    /**
     * Updates and makes sure only non-expired effects are acting on the
     * property.
     */
    public void updatePropEffectExistances() {
	Iterator<PropertyEffect<T>> it = activePropEffects.iterator();
	while (it.hasNext()) {
	    PropertyEffect<T> propEffect = it.next();
	    if (!propEffect.shouldExist()) {
		it.remove();
	    }
	}

	updateCurrentPropertyValue();

	valueUpdated();
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
     * Runs when it is suggested that the property value may have changed. If it
     * did change, then run the code for it to register the change and notify
     * the listeners.
     */
    protected void valueUpdated() {
	if (!currentPropValue.equals(lastCurrentPropValue)) {
	    Object[] specifications = getSpecificationsOfPropertyChange(lastCurrentPropValue, currentPropValue);
	    notifyPropertyChanged(lastCurrentPropValue, currentPropValue, specifications);
	    lastCurrentPropValue = currentPropValue;
	}
    }

    /**
     * Specify the change of the property with an Object[]
     * 
     * @param oldValue
     * @param newValue
     * @return the Object[] specifications of the change
     */
    protected abstract Object[] getSpecificationsOfPropertyChange(T oldValue, T newValue);

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
     *            additional specifications about the circumstances of the
     *            property change
     */
    protected void notifyPropertyChanged(T oldValue, T newValue, Object... specifications) {
	changeReporter.reportIncident(oldValue, newValue, unitOwner, this, specifications);
    }
}
