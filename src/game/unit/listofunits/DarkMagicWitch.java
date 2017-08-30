package game.unit.listofunits;

import game.Game;
import game.Player;
import game.board.Board;
import game.board.Coordinate;
import game.board.Direction;
import game.board.Square;
import game.interaction.Damage;
import game.interaction.DamageType;
import game.unit.Unit;
import game.unit.UnitStat;
import game.unit.property.Property;
import game.unit.property.ability.Ability;
import game.unit.property.ability.AbilityPower;
import game.unit.property.ability.AbilityRange;
import game.unit.property.ability.ActiveTargetAbility;

public class DarkMagicWitch extends Unit {

    public DarkMagicWitch(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	super(game, playerOwner, directionFacing, coor);
    }

    @Override
    public Ability getDefaultAbility() {
	UnitStat defaultStat = getDefaultStat();
	Ability ability = new WitchAbility(this, defaultStat.defaultPower, defaultStat.defaultAttackRange,
		defaultStat.defaultWaitTime);
	return ability;
    }
}

class WitchAbility extends ActiveTargetAbility implements AbilityPower, AbilityRange {
    // TODO set ability range property to permanently be 3
    private final Property<Integer> abilityPowerProperty;
    private final Property<Integer> abilityRangeProperty;

    public WitchAbility(Unit unitOwner, int initialPower, int initialAttackRange, int maxWaitTime) {
	super(unitOwner, maxWaitTime);
	abilityPowerProperty = new Property<>(unitOwner, initialPower);
	abilityRangeProperty = new Property<>(unitOwner, initialAttackRange);
    }

    @Override
    public Property<Integer> getAbilityPowerProperty() {
	return abilityPowerProperty;
    }

    @Override
    public Property<Integer> getAbilityRangeProperty() {
	return abilityRangeProperty;
    }

    // TODO look at whether target can be empty/ be friendly, etc
    @Override
    public boolean canUseAbilityOn(Square target) {
	Coordinate coor = getUnitOwner().getPosProp().getValue(), targetcoor = target.getCoor();
	boolean inRange = Board.walkDist(coor, targetcoor) <= getAbilityRangeProperty().getValue();
	boolean inPattern = coor.x() == targetcoor.x() || coor.y() == targetcoor.y();
	if (!canUseAbility() || !inRange || !inPattern) {
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
	Damage damage = new Damage(getAbilityPowerProperty().getValue(), DamageType.MAGIC, getUnitOwner(),
		target.getUnitOnTop());
	if (!target.isEmpty()) {
	    target.getUnitOnTop().getHealthProp().takeDamage(damage);
	}

    }

}
