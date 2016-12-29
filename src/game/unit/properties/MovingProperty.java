package game.unit.properties;

import game.unit.Unit;

public class MovingProperty extends Property<Integer> {

    private final Property<Boolean> teleportingProp;

    public MovingProperty(Unit unitOwner, Integer initMoveRange, Boolean initTeleportingValue) {
	super(unitOwner, initMoveRange);

	teleportingProp = new Property<Boolean>(unitOwner, initTeleportingValue) {

	    @Override
	    protected void propertyChanged(Boolean oldValue, Boolean newValue) {
		notifyPropertyChanged(oldValue, newValue);
	    }
	};
    }

    public Property<Boolean> getTeleportingProp() {
	return teleportingProp;
    }

    public boolean canCurrentlyMove() {
	if (getUnitOwner().getStunnedProp().getCurrentPropertyValue() || getUnitOwner().getWaitProp().isWaiting()) {
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
