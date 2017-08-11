package game.unit.property;

import game.board.Board;
import game.board.Coordinate;
import game.unit.Unit;

public class MovingProperty extends Property<Integer> {

    private final Property<Boolean> teleportingProp;

    public MovingProperty(Unit unitOwner, Integer initMoveRange, Boolean initTeleportingValue) {
	super(unitOwner, initMoveRange);

	teleportingProp = new Property<Boolean>(unitOwner, initTeleportingValue) {

	    @Override
	    protected Object[] getSpecificationsOfPropertyChange(Boolean oldValue, Boolean newValue) {
		return null;
	    }
	};
    }

    public Property<Boolean> getTeleportingProp() {
	return teleportingProp;
    }

    public boolean isInRangeOfWalking(Coordinate moveToCoor) {
	return Board.walkDist(moveToCoor, getUnitOwner().getPosProp().getValue()) <= getValue();
    }

    public boolean canCurrentlyMove() {
	if (getUnitOwner().getStunnedProp().getValue() || getUnitOwner().getWaitProp().isWaiting()) {
	    return false;
	}
	return getUnitOwner().getDefaultStat().canDefaultMove;
    }

    public boolean isCurrentlyStoic() {
	if (getUnitOwner().getStunnedProp().getValue()) {
	    return true;
	}
	return getUnitOwner().getDefaultStat().isDefaultStoic;
    }

    @Override
    protected Object[] getSpecificationsOfPropertyChange(Integer oldValue, Integer newValue) {
	return null;
    }

}
