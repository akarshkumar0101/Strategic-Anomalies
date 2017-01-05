package game.interaction;

import game.unit.Unit;

/**
 * 
 * The effects on the healing object should only be altering the healing
 * properties, passed the Object
 * 
 * @author akars
 *
 */
public class Healing {

    private final int healingAmount;

    private final Object source;
    private final Unit target;

    public Healing(int healingAmount, Object source, Unit target) {
	this.healingAmount = healingAmount;
	this.source = source;
	this.target = target;

    }

    public int getHealingAmount() {
	return healingAmount;
    }

    public Object getSource() {
	return source;
    }

    public Unit getTarget() {
	return target;
    }

    @Override
    public boolean equals(Object another) {
	if (!(another instanceof Healing)) {
	    return false;
	}
	Healing healing = (Healing) another;
	if (healing.healingAmount == healingAmount && healing.source.equals(source) && healing.target.equals(target)) {
	    return true;
	}
	return false;
    }
}
