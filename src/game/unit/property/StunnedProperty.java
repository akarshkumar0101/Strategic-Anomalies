package game.unit.property;

import game.unit.Unit;

public class StunnedProperty extends Property<Boolean> {

    public StunnedProperty(Unit unit, Boolean initValue) {
	super(unit, initValue);
    }

    @Override
    protected Object[] getSpecificationsOfPropertyChange(Boolean oldValue, Boolean newValue) {
	return null;
    }
}
