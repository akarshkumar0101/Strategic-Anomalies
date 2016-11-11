package game.unit;

import game.Player;
import game.Team;
import game.board.Board;
import game.board.Coordinate;
import game.board.Square;
import game.util.Direction;

public class Knight extends Unit {

	public static final int MOVERANGE = 3, ATTACKRANGE = 1, ATTACKDAMAGE=25;

	
	
	public Knight(Player playerOwner,Team teamOwner, Board board, Direction directionFacing, Coordinate coor) {
		super(playerOwner,teamOwner,board, directionFacing, coor);
	}
	
	

	@Override
	public int getMoveRange() {
		return MOVERANGE;
	}
	
	@Override
	public boolean canUseAbilityOn(Object... args) {
		Coordinate coor;
		try{
			coor = (Coordinate)args[0];
		}
		catch(Exception e){
			return false;
		}
		
		if(Coordinate.walkDist(this.coor, coor)>ATTACKRANGE){
			return false;
		}
		return true;
	}
	
	@Override
	public void performAbility(Object... args) {
		if(!canUseAbilityOn(args)){
			return;
		}
		Coordinate coor = (Coordinate)args[0];
		abilityInteract(board.getSquare(coor));
	}
	
	
	public int getCurrentAttackDamage(){
		return ATTACKDAMAGE;
	}
	
	@Override
	public void abilityInteract(Square sqr) {
		Unit unit = sqr.getUnitOnTop();
		
		if(unit== null) return;
		
		unit.takeDamage(getCurrentAttackDamage());
	}



	@Override
	public int getDefaultHealth() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public int getDefaultArmor() {
		// TODO Auto-generated method stub
		return 0;
	}

}
