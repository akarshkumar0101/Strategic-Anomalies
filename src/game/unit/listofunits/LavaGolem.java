package game.unit.listofunits;

import game.Game;
import game.Player;
import game.board.Coordinate;
import game.board.Direction;
import game.unit.Unit;
import game.unit.property.ability.Ability;

public class LavaGolem extends Unit {

    public LavaGolem(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	super(game, playerOwner, directionFacing, coor);
    }

    @Override
    public Ability getDefaultAbility() {

	return null;
    }
}
