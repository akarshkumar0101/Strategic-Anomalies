package game.unit;

import game.Game;
import game.Player;
import game.board.Coordinate;
import game.board.Direction;
import game.unit.ability.AbilityProperty;

public class Lightningmancer extends Unit {

    public static final int DEFAULT_HEALTH;
    public static final int DEFAULT_ARMOR;
    public static final int DEFAULT_POWER;
    public static final int DEFAULT_MOVE_RANGE;
    public static final int DEFAULT_ATTACK_RANGE;
    public static final int MAX_WAIT_TIME;
    public static final double DEFAULT_SIDE_BLOCK;
    public static final double DEFAULT_FRONT_BLOCK;

    static {
	PieceStats stats = UnitStats.unitStats.get(Lightningmancer.class);
	DEFAULT_HEALTH = stats.defaultHealth;
	DEFAULT_ARMOR = stats.defaultArmor;
	DEFAULT_POWER = stats.defaultPower;
	DEFAULT_MOVE_RANGE = stats.defaultMoveRange;
	DEFAULT_ATTACK_RANGE = stats.defaultAttackRange;
	MAX_WAIT_TIME = stats.maxWaitTime;
	DEFAULT_SIDE_BLOCK = stats.defaultSideBlock;
	DEFAULT_FRONT_BLOCK = stats.defaultFrontBlock;
    }

    public Lightningmancer(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
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
	AbilityProperty abilityProp = new MageAbiltyProperty(this, DEFAULT_POWER, DEFAULT_ATTACK_RANGE);
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
