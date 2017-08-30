package game.unit.listofunits;

import java.util.List;

import game.Game;
import game.Player;
import game.board.Board;
import game.board.Coordinate;
import game.board.Direction;
import game.board.Square;
import game.interaction.Healing;
import game.unit.Unit;
import game.unit.UnitStat;
import game.unit.property.Property;
import game.unit.property.ability.Ability;
import game.unit.property.ability.AbilityAOE;
import game.unit.property.ability.AbilityPower;
import game.unit.property.ability.AbilityRange;
import game.unit.property.ability.ActiveTargetAbility;

public class Monk extends Unit {

    public Monk(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	super(game, playerOwner, directionFacing, coor);
    }

    @Override
    public Ability getDefaultAbility() {
	UnitStat defaultStat = getDefaultStat();
	Ability ability = new MonkAbility(this, defaultStat.defaultPower, defaultStat.defaultAttackRange,
		defaultStat.defaultWaitTime);
	return ability;
    }
}

class MonkAbility extends ActiveTargetAbility implements AbilityPower, AbilityRange, AbilityAOE {

    private final Property<Integer> abilityPowerProp;
    private final Property<Integer> abilityRangeProp;

    public MonkAbility(Unit unitOwner, int initialPower, int initialRange, int maxWaitTime) {
	super(unitOwner, maxWaitTime);

	abilityPowerProp = new Property<>(unitOwner, initialPower);
	abilityRangeProp = new Property<>(unitOwner, initialRange);
    }

    @Override
    public Property<Integer> getAbilityPowerProperty() {
	return abilityPowerProp;
    }

    @Override
    public Property<Integer> getAbilityRangeProperty() {
	return abilityRangeProp;
    }

    @Override
    public List<Square> getAOESqaures(Square target) {
	return getUnitOwner().getGame().getBoard().squaresInRange(target, 3);
    }

    @Override
    public boolean canUseAbilityOn(Square target) {
	if (!canUseAbility() || Board.walkDist(getUnitOwner().getPosProp().getValue(),
		target.getCoor()) > getAbilityRangeProperty().getValue()) {
	    return false;
	} else {
	    return true;
	}
    }

    @Override
    protected void performAbility(Object... specs) {
	Square target = (Square) specs[0];
	if (!canUseAbilityOn(target)) {
	    return;
	}
	for (Square sqr : getAOESqaures(target)) {
	    if (!sqr.isEmpty()) {
		Unit unit = sqr.getUnitOnTop();
		Healing healing = new Healing(abilityPowerProp.getValue(), this, unit);
		unit.getHealthProp().takeHealing(healing);
	    }
	}
    }
}
