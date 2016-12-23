package game.unit;

import game.Game;
import game.Player;
import game.board.Board;
import game.board.Coordinate;
import game.board.Square;
import game.interaction.Damage;
import game.interaction.DamageType;
import game.unit.ability.AbilityType;
import game.unit.properties.AbilityProperty;
import game.util.Direction;

public class Knight extends Unit {

    public static final int DEFAULT_HEALTH = 50;
    public static final int DEFAULT_ARMOR = 8;
    public static final double DEFAULT_SIDE_BLOCK = 0.35;
    public static final double DEFAULT_FRONT_BLOCK = 0.8;
    public static final int DEFAULT_MOVE_RANGE = 3;
    public static final int DEFAULT_ATTACK_RANGE = 1;
    public static final int DEFAULT_POWER = 25;

    public Knight(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	super(game, playerOwner, directionFacing, coor);
    }

    @Override
    public int getDefaultHealth() {
	return DEFAULT_HEALTH;
    }

    @Override
    public int getDefaultArmor() {
	return DEFAULT_ARMOR;
    }

    @Override
    public double getDefaultSideBlock() {
	return DEFAULT_SIDE_BLOCK;
    }

    @Override
    public double getDefaultFrontBlock() {
	return DEFAULT_FRONT_BLOCK;
    }

    @Override
    public int getDefaultMoveRange() {
	return DEFAULT_MOVE_RANGE;
    }

    @Override
    public int getDefaultAttackRange() {
	return DEFAULT_ATTACK_RANGE;
    }

    @Override
    public int getDefaultPower() {
	return DEFAULT_POWER;
    }

    @Override
    public AbilityProperty getDefaultAbilityProperty() {
	AbilityProperty abilityProp = new KnightAbilityProperty(this, DEFAULT_POWER, DEFAULT_ATTACK_RANGE);
	return abilityProp;
    }
}

class KnightAbilityProperty extends AbilityProperty {

    public KnightAbilityProperty(Unit unitOwner, int initialPower, int initialAttackRange) {
	super(unitOwner, initialPower, initialAttackRange);
    }

    @Override
    public AbilityType getAbilityType() {
	return AbilityType.ACTIVE_TARGET;
    }

    @Override
    public boolean canUseAbilityOn(Square target) {
	if (getUnitOwner().getStunnedProp().getCurrentPropertyValue() || target.getUnitOnTop() == null
		|| Board.walkDist(getUnitOwner().getPosProp().getCurrentPropertyValue(),
			target.getCoor()) > getAbilityRangeProperty().getCurrentPropertyValue()
		|| Unit.areAllies(getUnitOwner(), target.getUnitOnTop())) {
	    return false;
	} else {
	    return true;
	}
    }

    @Override
    public void performAbility(Square target) {
	if (!canUseAbilityOn(target)) {
	    return;
	}
	target.getUnitOnTop().healthProp.takeDamage(
		new Damage(getCurrentPropertyValue(), DamageType.PHYSICAL, getUnitOwner(), target.getUnitOnTop()));
    }
}
