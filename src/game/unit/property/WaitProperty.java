package game.unit.property;

import game.Turn;
import game.unit.Unit;

public class WaitProperty extends Property<Integer> {

    public final int defaultMaxValue;

    public WaitProperty(Unit unitOwner, int initValue, int defaultMaxValue) {
	super(unitOwner, initValue);
	this.defaultMaxValue = defaultMaxValue;
    }

    public void triggerWaitAfterAttack() {
	setPropertyValue(defaultMaxValue, getUnitOwner().getAbilityProp());
    }

    public void incrementTurn(Turn currentTurn) {
	if (getCurrentPropertyValue() > 0) {
	    setPropertyValue(getCurrentPropertyValue() - 1, currentTurn);
	}
    }

    public boolean isWaiting() {
	return getCurrentPropertyValue() > 0;
    }

    @Override
    protected Object[] getSpecificationsOfPropertyChange(Integer oldValue, Integer newValue) {
	return null;
    }

}
