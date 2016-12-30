package game.unit.property;

import game.unit.UnitClass;
import game.unit.property.ability.AbilityProperty;

public interface UnitDefaults {

    public abstract int getDefaultHealth();

    public abstract int getDefaultArmor();

    public abstract double getDefaultSideBlock();

    public abstract double getDefaultFrontBlock();

    // TODO make sure all units should consider overriding these methods
    public default boolean canDefaultMove() {
	return true;
    }

    public default boolean canDefaultTeleport() {
	return false;
    }

    // side stepping is when it can move out of way to let friendly piece pass
    public default boolean isDefaultStoic() {
	return true;
    }

    public abstract int getDefaultMoveRange();

    public abstract int getDefaultAttackRange();

    public abstract int getDefaultPower();

    public abstract AbilityProperty getDefaultAbilityProperty();

    public abstract int getMaxWaitTime();

    public abstract UnitClass getUnitClass();

}
