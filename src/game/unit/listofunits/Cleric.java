package game.unit.listofunits;

import java.util.ArrayList;
import java.util.List;

import game.Game;
import game.Player;
import game.board.Coordinate;
import game.board.Direction;
import game.board.Square;
import game.interaction.Damage;
import game.interaction.DamageType;
import game.unit.Unit;
import game.unit.UnitClass;
import game.unit.UnitStat;
import game.unit.UnitStats;
import game.unit.property.ability.AbilityProperty;
import game.unit.property.ability.ActiveTargetAbilityProperty;

public class Cleric extends Unit {

    public static final int DEFAULT_HEALTH;
    public static final int DEFAULT_ARMOR;
    public static final int DEFAULT_POWER;
    public static final int DEFAULT_MOVE_RANGE;
    public static final int DEFAULT_ATTACK_RANGE;
    public static final int MAX_WAIT_TIME;
    public static final double DEFAULT_SIDE_BLOCK;
    public static final double DEFAULT_FRONT_BLOCK;

    static {
	UnitStat stat = UnitStats.getStat(Cleric.class);
	DEFAULT_HEALTH = stat.defaultHealth;
	DEFAULT_ARMOR = stat.defaultArmor;
	DEFAULT_POWER = stat.defaultPower;
	DEFAULT_MOVE_RANGE = stat.defaultMoveRange;
	DEFAULT_ATTACK_RANGE = stat.defaultAttackRange;
	MAX_WAIT_TIME = stat.maxWaitTime;
	DEFAULT_SIDE_BLOCK = stat.defaultSideBlock;
	DEFAULT_FRONT_BLOCK = stat.defaultFrontBlock;
    }

    public Cleric(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
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
	AbilityProperty abilityProp = new HealerAbilityProperty(this, DEFAULT_POWER, DEFAULT_ATTACK_RANGE,
		MAX_WAIT_TIME);
	return abilityProp;
    }

    @Override
    public int getMaxWaitTime() {
	return MAX_WAIT_TIME;
    }

    @Override
    public UnitClass getUnitClass() {
	return UnitClass.MAGE;
    }
}

class HealerAbilityProperty extends ActiveTargetAbilityProperty {

    public HealerAbilityProperty(Unit unitOwner, int initialPower, int initialAttackRange, int maxWaitTime) {
	super(unitOwner, initialPower, initialAttackRange, maxWaitTime);
    }

    @Override
    public boolean canUseAbilityOn(Square target) {
	return true;
    }

    @Override
    public List<Square> getAOESqaures(Square target) {
	List<Square> list = new ArrayList<>(1);

	return list;
    }

    @Override
    public void performAbility(Square target) {
	if (!canUseAbilityOn(target)) {
	    return;
	}
	List<Square> targets = getAOESqaures(target);
	for (Square ss : targets) {
	    Damage damage = new Damage(getCurrentPropertyValue(), DamageType.MAGIC, getUnitOwner(), ss.getUnitOnTop());
	    ss.getUnitOnTop().getHealthProp().takeDamage(damage);
	}
    }

}
