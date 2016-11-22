package game.unit.properties;

import game.interaction.Damage;
import game.unit.Unit;

public class HealthProperty extends Property<Integer> {

	public HealthProperty(Unit unit, int startingHealth) {
		super(unit, startingHealth);
	}

	public double percentageHealth() {
		return (double) (property / unit.getDefaultHealth());
	}

	public void takeRawDamage(Damage damage) {
		int damageAmount = damage.getDamageAmount();

		if (damageAmount == 0)
			return;
		Integer oldVal = property;

		if (damageAmount > property) {
			// dies
			property = 0;
			propertyChanged(oldVal, property);
			// TODO trigger death of unit
		} else {
			property -= damageAmount;
			propertyChanged(oldVal, property);
		}
	}
}
