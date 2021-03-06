package game.unit.listofunits;

import game.Game;
import game.Player;
import game.board.Coordinate;
import game.board.Direction;
import game.unit.Unit;
import game.unit.UnitStat;
import game.unit.property.ability.Ability;

public class Lightningmancer extends Unit {

    public Lightningmancer(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
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
