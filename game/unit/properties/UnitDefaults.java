package game.unit.properties;

import game.board.Square;

public interface UnitDefaults {

	public abstract int getDefaultHealth();

	public abstract int getDefaultArmor();

	public abstract double getDefaultSideBlock();

	public abstract double getDefaultFrontBlock();

	// TODO make sure all units should consider overriding these methods
	public default boolean canMove() {
		return true;
	}

	// side stepping is when it can move out of way to let friendly piece pass
	public default boolean canSideStep() {
		return true;
	}

	public abstract int getMoveRange();
	// TODO probably delete these out dated ability methods.

	public abstract boolean canUseAbilityOn(Object... args);

	/**
	 * Simply tells the unit to perform its ability given some arguments
	 * 
	 * @param args
	 */
	public abstract void performAbility(Object... args);

	/**
	 * Subclasses can choose to implement and call this method from
	 * performAbility(Object..) to make casting abilities easier.
	 * 
	 * @param sqr
	 */
	public abstract void abilityInteract(Square sqr);

}
