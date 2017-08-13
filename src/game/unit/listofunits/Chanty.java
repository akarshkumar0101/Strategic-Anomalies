package game.unit.listofunits;

import game.Game;
import game.Player;
import game.board.Coordinate;
import game.board.Direction;
import game.board.Square;
import game.unit.Unit;
import game.unit.UnitStat;
import game.unit.property.Property;
import game.unit.property.ability.Ability;
import game.unit.property.ability.AbilityRange;
import game.unit.property.ability.ActiveTargetAbility;

public class Chanty extends Unit {

    public Chanty(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	super(game, playerOwner, directionFacing, coor);
    }

    @Override
    public Ability getDefaultAbility() {
	UnitStat defaultStat = getDefaultStat();
	Ability ability = new ChantyAbility(this, defaultStat.defaultAttackRange, defaultStat.defaultWaitTime);
	return ability;
    }
}

class ChantyAbility extends ActiveTargetAbility implements AbilityRange {

    private final Property<Integer> abilityRangeProperty;

    public ChantyAbility(Unit unitOwner, int initialRange, int maxWaitTime) {
	super(unitOwner, maxWaitTime);
	abilityRangeProperty = new Property<>(unitOwner, initialRange);
    }

    @Override
    public Property<Integer> getAbilityRangeProperty() {
	return abilityRangeProperty;
    }

    @Override
    public boolean canUseAbilityOn(Square target) {
	return false;
    }

    @Override
    protected void performAbility(Object... specs) {

    }

}
