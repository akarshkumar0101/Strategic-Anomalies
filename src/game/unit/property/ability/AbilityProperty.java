package game.unit.property.ability;

import game.unit.Unit;
import game.unit.property.Property;

//the value of the property IS the power.
public abstract class AbilityProperty extends Property<Integer> {

    private final Property<Integer> abilityRangeProp;

    public AbilityProperty(Unit unitOwner, int initialPower, int initialAttackRange) {
	super(unitOwner, initialPower);
	abilityRangeProp = new Property<Integer>(unitOwner, initialAttackRange) {

	    @Override
	    protected Object[] getSpecificationsOfPropertyChange(Integer oldValue, Integer newValue) {
		return null;
	    }
	};
    }

    public Property<Integer> getAbilityRangeProperty() {
	return abilityRangeProp;
    }

    public boolean isActiveAbility() {
	return this instanceof ActiveAbilityProperty;
    }

    public boolean canCurrentlyUseAbility() {
	return false;
    }

    // TODO some of these methods are public, CHANGE THEM TO PROTECTED
    @Override
    protected Object[] getSpecificationsOfPropertyChange(Integer oldValue, Integer newValue) {
	return null;
    }
}
