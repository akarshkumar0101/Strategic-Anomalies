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

public class Scout extends Unit {

    public Scout(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	super(game, playerOwner, directionFacing, coor);
    }

    @Override
    public Ability getDefaultAbility() {
	UnitStat defaultStat = getDefaultStat();
	Ability ability = new BowmenAbility(this, defaultStat.defaultPower, defaultStat.defaultAttackRange,
		defaultStat.defaultWaitTime);
	return ability;
    }
}

class BowmenAbility extends ActiveTargetAbility implements AbilityPower, AbilityRange {

    private final Property<Integer> abilityPowerProperty;
    private final Property<Integer> abilityRangeProperty;

    public BowmenAbility(Unit unitOwner, int initialPower, int initialRange, int maxWaitTime) {
	super(unitOwner, maxWaitTime);

	abilityPowerProperty = new Property<>(unitOwner, initialPower);
	abilityRangeProperty = new Property<>(unitOwner, initialRange);
    }

    @Override
    public Property<Integer> getAbilityPowerProperty() {
	return abilityPowerProperty;
    }

    @Override
    public Property<Integer> getAbilityRangeProperty() {
	return abilityRangeProperty;
    }

    @Override
    public boolean canUseAbilityOn(Square target) {
	// TODO other stuff deciding scout?
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
	Damage damage = new Damage(getAbilityPowerProperty().getValue(), DamageType.PHYSICAL, getUnitOwner(),
		target.getUnitOnTop());
	target.getUnitOnTop().getHealthProp().takeDamage(damage);

    }

}
