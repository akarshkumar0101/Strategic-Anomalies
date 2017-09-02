package game.unit.property;

import game.Turn;
import game.unit.Unit;

public class WaitProperty extends Property<Integer> {
    public final Property<Integer> maxValueProperty;

    public WaitProperty(Unit unitOwner, int initValue, int defaultMaxValue) {
	super(unitOwner, initValue);
	maxValueProperty = new Property<>(unitOwner, defaultMaxValue);

	unitOwner.getGame().turnEndReporter.add(specifications -> {
	    onTurnEnd((Turn) specifications[0]);
	});
    }

    public void triggerWaitForAttack() {
	int maxValue = maxValueProperty.getValue();
	int valinc = maxValue / 2;
	if (valinc % 2 != 0) {
	    valinc++;
	}
	setValue(getValue() + valinc, getUnitOwner().getAbility());
    }

    public void triggerWaitForMove() {
	int maxValue = maxValueProperty.getValue();
	int valinc = maxValue / 2;
	setValue(getValue() + valinc, getUnitOwner().getAbility());
    }

    private void onTurnEnd(Turn endingTurn) {
	if (getValue() > 0) {
	    setValue(getValue() - 1, endingTurn);
	}
    }

    public boolean isWaiting() {
	return getValue() > 0;
    }

}
