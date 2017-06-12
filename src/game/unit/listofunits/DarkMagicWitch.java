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

public class DarkMagicWitch extends Unit {

    public DarkMagicWitch(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	super(game, playerOwner, directionFacing, coor);
    }

    @Override
    public AbilityProperty getDefaultAbilityProperty() {
	UnitStat defaultStat = getDefaultStat();
	AbilityProperty abilityProp = new WitchAbilityProperty(this, defaultStat.defaultPower,
		defaultStat.defaultAttackRange, defaultStat.defaultWaitTime);
	return abilityProp;
    }
}

class WitchAbilityProperty extends ActiveTargetAbilityProperty {
    // TODO set ability range property to permanently be 3

    public WitchAbilityProperty(Unit unitOwner, int initialPower, int initialAttackRange, int maxWaitTime) {
	super(unitOwner, initialPower, initialAttackRange, maxWaitTime);
    }

    // TODO look at whether target can be empty/ be friendly, etc
    @Override
    public boolean canUseAbilityOn(Square target) {
	Coordinate thiscoor = getUnitOwner().getPosProp().getCurrentPropertyValue();
	Coordinate targetcoor = target.getCoor();

	boolean inRange = Coordinate.inDirectDirection(thiscoor, targetcoor) != null
		&& Board.walkDist(thiscoor, targetcoor) <= getAbilityRangeProperty().getCurrentPropertyValue();

	if (!canCurrentlyUseAbility() || !inRange) {
	    return false;
	} else {
	    return true;
	}
    }

    @Override
    public List<Square> getAOESqaures(Square target) {
	List<Square> list = new ArrayList<>(1);

	Coordinate thiscoor = getUnitOwner().getPosProp().getCurrentPropertyValue();
	Coordinate targetcoor = target.getCoor();

	if (Board.walkDist(thiscoor, targetcoor) <= getAbilityRangeProperty().getCurrentPropertyValue()) {

	    Direction dir = Coordinate.inDirectDirection(thiscoor, targetcoor);
	    Coordinate current = thiscoor;
	    for (int i = 0; i < 3; i++) {
		current = Coordinate.shiftCoor(current, dir);
		Square sqr = getUnitOwner().getGame().getBoard().getSquare(current);
		if (sqr != null) {
		    list.add(sqr);
		}
	    }

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
