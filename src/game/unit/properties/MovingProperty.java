package game.unit.properties;

import game.unit.Unit;

public class MovingProperty extends Property<Integer> {

    public MovingProperty(Unit unitOwner, Integer initValue) {
	super(unitOwner, initValue);
    }

    public boolean canCurrentlyMove() {
	if (getUnitOwner().getStunnedProp().getCurrentPropertyValue()) {
	    return false;
	}
	return getUnitOwner().canDefaultMove();
    }

    public boolean isCurrentlyStoic() {
	if (getUnitOwner().getStunnedProp().getCurrentPropertyValue()) {
	    return true;
	}
	return getUnitOwner().isDefaultStoic();
    }

    @Override
    protected void propertyChanged(Integer oldValue, Integer newValue) {
	super.notifyPropertyChanged(oldValue, newValue);

    }

}
