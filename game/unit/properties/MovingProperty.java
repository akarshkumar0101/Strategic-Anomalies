package game.unit.properties;

import game.unit.Unit;

public class MovingProperty extends Property<Integer> {

    public MovingProperty(Unit unitOwner, Integer initValue) {
	super(unitOwner, initValue);
    }

    public boolean canCurrentlyMove() {
	if (unitOwner.getStunnedProp().getCurrentPropertyValue()) {
	    return false;
	}
	return unitOwner.canDefaultMove();
    }

    public boolean isCurrentlyStoic() {
	if (unitOwner.getStunnedProp().getCurrentPropertyValue()) {
	    return true;
	}
	return unitOwner.isDefaultStoic();
    }

}
