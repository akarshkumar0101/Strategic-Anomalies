package game.unit;

import game.Game;
import game.Player;
import game.board.Coordinate;
import game.board.Square;
import game.util.Direction;

/**
 * The Knight Unit implementation.
 * 
 * @author Akarsh
 *
 */
public class Knight extends Unit {

	public static final int MOVERANGE = 3, ATTACKRANGE = 1, ATTACKDAMAGE = 25;

	public Knight(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
		super(game, playerOwner, directionFacing, coor);
	}

	@Override
	public int getDefaultHealth() {
		return 0;
	}

	@Override
	public int getDefaultArmor() {
		return 0;
	}

	@Override
	public double getDefaultSideBlock() {
		return 0;
	}

	@Override
	public double getDefaultFrontBlock() {
		return 0;
	}

	@Override
	public int getMoveRange() {
		return MOVERANGE;
	}

	@Override
	public boolean canUseAbilityOn(Object... args) {
		Coordinate coor;
		try {
			coor = (Coordinate) args[0];
		} catch (Exception e) {
			return false;
		}

		if (Coordinate.walkDist(this.posProp.getCurrentPropertyValue(), coor) > ATTACKRANGE)
			return false;
		else
			return true;
	}

	@Override
	public void performAbility(Object... args) {
		if (!canUseAbilityOn(args)) {
			return;
		}
		Coordinate coor = (Coordinate) args[0];
		abilityInteract(game.getBoard().getSquare(coor));
	}

	@Override
	public void abilityInteract(Square sqr) {
		Unit unit = sqr.getUnitOnTop();

		if (unit == null)
			return;

		// unit.takeDamage();
	}

}
