package game.unit.property.ability;

import java.util.ArrayList;
import java.util.List;

import game.board.Board;
import game.unit.Unit;

public abstract class AOEPassiveAbility extends PassiveAbility implements AbilityRange {

    private final List<Unit> currentlyAffectedUnits;

    public AOEPassiveAbility(Unit unitOwner) {
	super(unitOwner);

	currentlyAffectedUnits = new ArrayList<>();

	unitOwner.getGame().gameStartReporter.add(specifications -> onGameStart(specifications));
    }

    private void onGameStart(Object... specifications) {
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
	if (!currentlyAffectedUnits.contains(unit) && canAffect(unit) && isInAOERange(unit)) {
	    addAffectedUnit(unit);
	} else if (currentlyAffectedUnits.contains(unit) && !isInAOERange(unit)) {
	    removeAffectedUnit(unit);
	}
    }

    protected boolean isInAOERange(Unit unit) {
	return Board.walkDist(getUnitOwner().getPosProp().getValue(),
		unit.getPosProp().getValue()) <= getAbilityRangeProperty().getValue();
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
