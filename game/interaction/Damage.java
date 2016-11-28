package game.interaction;

import game.interaction.effect.Affectable;
import game.unit.Unit;

/**
 * 
 * The effects on the Damage object should only be altering the damage
 * properties, passed the Object
 * 
 * @author akars
 *
 */
public class Damage extends Affectable {

	private int damageAmount;

	private DamageType damageType;

	private Unit source, target;

	private boolean wasBlocked;

	public Damage(int damageAmount, DamageType damageType, Unit source, Unit target) {
		this(damageAmount, damageType, source, target, false);
	}

	public Damage(int damageAmount, DamageType damageType, Unit source, Unit target, boolean wasBlocked) {
		this.damageAmount = damageAmount;
		this.damageType = damageType;
		this.source = source;
		this.target = target;

		this.wasBlocked = wasBlocked;
	}

	public int getDamageAmount() {
		return damageAmount;
	}

	public void setDamageAmount(int damageAmount) {
		this.damageAmount = damageAmount;
	}

	public DamageType getDamageType() {
		return damageType;
	}

	public void setDamageType(DamageType damageType) {
		this.damageType = damageType;
	}

	public Unit getSource() {
		return source;
	}

	public void setSource(Unit source) {
		this.source = source;
	}

	public Unit getTarget() {
		return target;
	}

	public void setTarget(Unit target) {
		this.target = target;
	}

	public void setBlocked(boolean blocked) {
		this.wasBlocked = blocked;
	}

	public boolean wasBlocked() {
		return wasBlocked;
	}

	@Override
	public boolean equals(Object another) {
		if (!(another instanceof Damage))
			return false;
		Damage other = (Damage) another;
		if (other.damageAmount == damageAmount && other.damageType.equals(damageType) && other.source.equals(source)
				&& other.target.equals(target) && other.wasBlocked == wasBlocked)
			return true;
		return false;
	}
}
