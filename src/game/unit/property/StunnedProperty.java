package game.unit.property;

import game.unit.Unit;

public class StunnedProperty extends Property<Boolean> {

    public StunnedProperty(Unit unit, Boolean initValue) {
	super(unit, initValue);
    }

    @Override
    public void propertyChanged(Boolean oldValue, Boolean newValue) {
	super.notifyPropertyChanged(oldValue, newValue);
    }
}
