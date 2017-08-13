package game.unit.listofunits;

import game.Game;
import game.Player;
import game.board.Coordinate;
import game.board.Direction;
import game.unit.Unit;
import game.unit.UnitStat;
import game.unit.property.ability.Ability;

public class Sage extends Unit {

    public Sage(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	super(game, playerOwner, directionFacing, coor);
    }

    @Override
    public Ability getDefaultAbility() {
	UnitStat defaultStat = getDefaultStat();
	Ability ability = new HealerAbility(this, defaultStat.defaultPower, defaultStat.defaultWaitTime);
	return ability;
    }
}