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

class WitchAbility extends ActiveTargetAbility implements AbilityPower, AbilityRange, AbilityAOE {
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
	if (!canUseAbility() || !inRange || !inPattern || coor.equals(targetcoor)) {
	    return false;
	} else {
	    return true;
	}
    }

    @Override
    public List<Square> getAOESqaures(Square target) {
	List<Square> aoeSquares = new ArrayList<>();

	Board board = getUnitOwner().getGame().getBoard();
	Direction dir = Coordinate.inGeneralDirection(getUnitOwner().getPosProp().getValue(), target.getCoor());
	if (dir != null) {
	    for (int i = 1; i <= abilityRangeProperty.getValue(); i++) {
		Coordinate targetCoor = Coordinate.shiftCoor(getUnitOwner().getPosProp().getValue(), dir, i);
		if (board.isInBoard(targetCoor)) {
		    aoeSquares.add(board.getSquare(targetCoor));
		}
	    }
	}
	return aoeSquares;
    }

    @Override
    protected void performAbility(Object... specs) {
	Square target = (Square) specs[0];
	if (!canUseAbilityOn(target)) {
	    return;
	}
	for (Square sqr : getAOESqaures(target)) {
	    Damage damage = new Damage(getAbilityPowerProperty().getValue(), DamageType.MAGIC, getUnitOwner(),
		    sqr.getUnitOnTop());
	    if (!sqr.isEmpty()) {
		sqr.getUnitOnTop().getHealthProp().takeDamage(damage);
	    }
	}
    }

}
