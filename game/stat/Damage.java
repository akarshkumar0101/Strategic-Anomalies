package game.stat;

import game.unit.Unit;

public class Damage {

	private final int damageAmount;
	private final DamageType damageType;
	private final Unit source;

	public Damage(int damageAmount, DamageType damageType, Unit source) {
		this.damageAmount = damageAmount;
		this.damageType = damageType;
		this.source = source;
	}

	public int getDamageAmount() {
		return damageAmount;
	}

	public DamageType damageType() {
		return damageType;
	}

	public Unit getSource() {
		return source;
	}

}
