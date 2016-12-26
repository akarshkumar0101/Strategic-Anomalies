package game.unit.ability;

import game.unit.Unit;

//can be implemented by aoe passive, or turn based passive, or other type of passive.
public abstract class PassiveAbilityProperty extends AbilityProperty {

    public PassiveAbilityProperty(Unit unitOwner, int initialPower, int initialAttackRange) {
	super(unitOwner, initialPower, initialAttackRange);
    }

}
