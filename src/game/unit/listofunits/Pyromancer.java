package game.unit.listofunits;

import java.util.ArrayList;
import java.util.List;

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
import game.unit.property.ability.AbilityAOE;
import game.unit.property.ability.AbilityPower;
import game.unit.property.ability.AbilityRange;
import game.unit.property.ability.ActiveTargetAbility;

public class Pyromancer extends Unit {
    public Pyromancer(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	super(game, playerOwner, directionFacing, coor);
    }

    @Override
    public Ability getDefaultAbility() {
	UnitStat defaultStat = getDefaultStat();
	Ability abilityProp = new MageAbilty(this, defaultStat.defaultPower, defaultStat.defaultAttackRange,
		defaultStat.defaultWaitTime);
	return abilityProp;
    }
}

class MageAbilty extends ActiveTargetAbility implements AbilityPower, AbilityRange, AbilityAOE {

    private final Property<Integer> abilityPowerProperty;
    private final Property<Integer> abilityRangeProperty;

    public MageAbilty(Unit unitOwner, int initialPower, int initialRange, int maxWaitTime) {
	super(unitOwner, maxWaitTime);
	abilityPowerProperty = new Property<>(unitOwner, initialPower);
	abilityRangeProperty = new Property<>(unitOwner, initialRange);
    }

    @Override
    public boolean canUseAbilityOn(Square target) {
	if (!canUseAbility()
		|| target.getUnitOnTop() == null || Board.walkDist(getUnitOwner().getPosProp().getValue(),
			target.getCoor()) > getAbilityRangeProperty().getValue()
		|| Unit.areAllies(getUnitOwner(), target.getUnitOnTop())) {
	    return false;
	} else {
	    return true;
	}
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
    public List<Square> getAOESqaures(Square target) {
	List<Square> list = new ArrayList<>();
	list.add(target);

	Board board = getUnitOwner().getGame().getBoard();
	Square left = board.getSquare(Coordinate.shiftCoor(target.getCoor(), Direction.LEFT));
	Square up = board.getSquare(Coordinate.shiftCoor(target.getCoor(), Direction.UP));
	Square right = board.getSquare(Coordinate.shiftCoor(target.getCoor(), Direction.RIGHT));
	Square down = board.getSquare(Coordinate.shiftCoor(target.getCoor(), Direction.DOWN));

	if (left != null) {
	    list.add(left);
	}
	if (up != null) {
	    list.add(up);
	}
	if (right != null) {
	    list.add(right);
	}
	if (down != null) {
	    list.add(down);
	}

	return list;
    }

    @Override
    protected void performAbility(Object... specs) {
	Square target = (Square) specs[0];
	if (!canUseAbilityOn(target)) {
	    return;
	}
	List<Square> targets = getAOESqaures(target);
	for (Square ss : targets) {
	    Damage damage = new Damage(getAbilityPowerProperty().getValue(), DamageType.MAGIC, getUnitOwner(),
		    ss.getUnitOnTop());
	    if (!ss.isEmpty()) {
		ss.getUnitOnTop().getHealthProp().takeDamage(damage);
	    }
	}
    }

}
