package game.unit.property;

import game.Turn;
import game.unit.Unit;

public class WaitProperty extends Property<Integer> {

    public final int defaultMaxValue;

    public WaitProperty(Unit unitOwner, int initValue, int defaultMaxValue) {
	super(unitOwner, initValue);
	this.defaultMaxValue = defaultMaxValue;

	unitOwner.getGame().turnEndReporter.add(specifications -> {
	    onTurnEnd((Turn) specifications[0]);
	});
    }

    public void triggerWaitAfterAttack() {
	setValue(defaultMaxValue, getUnitOwner().getAbility());
    }

    private void onTurnEnd(Turn endingTurn) {
	if (getValue() > 0) {
	    setValue(getValue() - 1, endingTurn);
	}
    }

    public boolean isWaiting() {
	return getValue() > 0;
    }

    @Override
    protected Object[] getSpecificationsOfPropertyChange(Integer oldValue, Integer newValue) {
	return null;
    }

}
