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
import game.unit.property.ability.AbilityProperty;
import game.unit.property.ability.ActiveTargetAbilityProperty;

public class Pyromancer extends Unit {
    public Pyromancer(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	super(game, playerOwner, directionFacing, coor);
    }

    @Override
    public AbilityProperty getDefaultAbilityProperty() {
	UnitStat defaultStat = getDefaultStat();
	AbilityProperty abilityProp = new MageAbiltyProperty(this, defaultStat.defaultPower,
		defaultStat.defaultAttackRange, defaultStat.defaultWaitTime);
	return abilityProp;
    }
}

class MageAbiltyProperty extends ActiveTargetAbilityProperty {

    public MageAbiltyProperty(Unit unitOwner, int initialPower, int initialAttackRange, int maxWaitTime) {
	super(unitOwner, initialPower, initialAttackRange, maxWaitTime);
    }

    @Override
    public boolean canUseAbilityOn(Square target) {
	if (!canCurrentlyUseAbility() || target.getUnitOnTop() == null
		|| Board.walkDist(getUnitOwner().getPosProp().getCurrentPropertyValue(),
			target.getCoor()) > getAbilityRangeProperty().getCurrentPropertyValue()
		|| Unit.areAllies(getUnitOwner(), target.getUnitOnTop())) {
	    return false;
	} else {
	    return true;
	}
    }

    @Override
    public List<Square> getAOESqaures(Square target) {
	List<Square> list = new ArrayList<>(1);
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
