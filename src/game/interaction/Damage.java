package game.interaction;

import game.unit.Unit;

/**
 * 
 * The effects on the Damage object should only be altering the damage
 * properties, passed the Object
 * 
 * @author akars
 *
 */
public class Damage {

    private final int damageAmount;

    private final DamageType damageType;

    private final Object source;
    private final Unit target;

    public Damage(int damageAmount, DamageType damageType, Object source, Unit target) {
	this.damageAmount = damageAmount;
	this.damageType = damageType;
	this.source = source;
	this.target = target;

    }

    public int getDamageAmount() {
	return damageAmount;
    }

    public DamageType getDamageType() {
	return damageType;
    }

    public Object getSource() {
	return source;
    }

    public Unit getTarget() {
	return target;
    }

    @Override
    public boolean equals(Object another) {
	if (!(another instanceof Damage)) {
	    return false;
	}
	Damage damage = (Damage) another;
	if (damage.damageAmount == damageAmount && damage.damageType.equals(damageType) && damage.source.equals(source)
		&& damage.target.equals(target)) {
	    return true;
	}
	return false;
    }
}
