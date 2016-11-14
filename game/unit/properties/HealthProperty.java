package game.unit.properties;

import game.interaction.Damage;
import game.unit.Unit;

public class HealthProperty extends Property<Integer> {

	private int health;

	public HealthProperty(Unit unit, int startingHealth) {
		super(unit);
		this.health = startingHealth;
	}

	public double percentageHealth() {
		return (double) (health / unit.getDefaultHealth());
	}

	public void takeDamage(Damage damage) {

	}
}
