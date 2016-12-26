package game.unit.ability;

import java.util.ArrayList;
import java.util.List;

import game.board.Board;
import game.unit.Unit;

public abstract class AOEPassiveAbilityProperty extends PassiveAbilityProperty {

    private final List<Unit> currentlyAffectedUnits;

    public AOEPassiveAbilityProperty(Unit unitOwner, int initialPower, int initialAttackRange) {
	super(unitOwner, initialPower, initialAttackRange);

	currentlyAffectedUnits = new ArrayList<>(5);
    }

    public void onGameStart() {
	List<Unit> units = getUnitOwner().getGame().getAllUnits();

	for (Unit unit : units) {
	    if (!unit.equals(getUnitOwner())) {
		addUnitOnRadar(unit);
	    }
	}
    }

    public void onUnitSpawn(Unit unit) {
	addUnitOnRadar(unit);
    }

    public void addUnitOnRadar(Unit unit) {
	unit.getPosProp().addPropertyListener((oldValue, newValue, unitOwner, property, specifications) -> {
	    checkSituation(unit);
	});
	getAbilityRangeProperty().addPropertyListener((oldValue, newValue, unitOwner, property, specifications) -> {
	    List<Unit> units = getUnitOwner().getGame().getAllUnits();

	    for (Unit u : units) {
		if (!unit.equals(getUnitOwner())) {
		    checkSituation(u);
		}
	    }
	});
	checkSituation(unit);
    }

    private void checkSituation(Unit unit) {
	if (!currentlyAffectedUnits.contains(unit) && canAffect(unit) && isInRange(unit)) {
	    addAffectedUnit(unit);
	} else if (currentlyAffectedUnits.contains(unit) && !isInRange(unit)) {
	    removeAffectedUnit(unit);
	}
    }

    private boolean isInRange(Unit unit) {
	return Board.walkDist(getUnitOwner().getPosProp().getCurrentPropertyValue(),
		unit.getPosProp().getCurrentPropertyValue()) <= getAbilityRangeProperty().getCurrentPropertyValue();
    }

    private void addAffectedUnit(Unit unit) {
	affectUnit(unit);
	currentlyAffectedUnits.add(unit);
    }

    private void removeAffectedUnit(Unit unit) {
	unaffectUnit(unit);
	currentlyAffectedUnits.remove(unit);
    }

    protected abstract boolean canAffect(Unit unit);

    protected abstract void affectUnit(Unit unit);

    protected abstract void unaffectUnit(Unit unit);
}
