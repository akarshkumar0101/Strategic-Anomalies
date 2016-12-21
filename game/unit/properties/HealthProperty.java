package game.unit.properties;

import game.interaction.Damage;
import game.unit.Unit;

public class HealthProperty extends Property<Integer> {

	private final ArmorProperty armorProp;

	public HealthProperty(Unit unit, int startingHealth) {
		super(unit, startingHealth);
		armorProp = new ArmorProperty(unit);
	}

	public ArmorProperty getArmorProp() {
		return armorProp;
	}

	public double percentageHealth() {
		return (double) (getCurrentPropertyValue() / defaultPropValue);
	}

	public void takeDamage(Damage damage) {
		armorProp.filterDamage(damage);
		if (!damage.wasBlocked())
			takeRawDamage(damage);
	}

	private void takeRawDamage(Damage damage) {
		int damageAmount = damage.getDamageAmount();

		if (damageAmount == 0)
			return;
		Integer oldVal = getCurrentPropertyValue();

		if (damageAmount > getCurrentPropertyValue()) {
			// dies
			property = 0;
			propertyChanged(oldVal, getCurrentPropertyValue());
			// TODO trigger death of unit
		} else {
			property -= damageAmount;
			propertyChanged(oldVal, getCurrentPropertyValue());
		}
	}
}
