package game.interaction;

import game.unit.Unit;

public class Damage {

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

}
