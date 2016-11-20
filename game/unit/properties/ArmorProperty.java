package game.unit.properties;

import java.util.ArrayList;
import java.util.List;

import game.Turn;
import game.interaction.Damage;
import game.interaction.DamageType;
import game.unit.Unit;
import game.util.IncidentListener;

public class ArmorProperty extends Property<Integer> {

	private final List<IncidentListener> blockListeners;

	private Turn turnPreviouslyBlockedOn;

	public ArmorProperty(Unit unit) {
		super(unit, unit.getDefaultArmor());

		blockListeners = new ArrayList<>(3);
		turnPreviouslyBlockedOn = null;

	}

	public int getArmor() {
		return property;
	}

	public Damage filterDamage(Damage damage) {
		if (damage.getDamageType() == DamageType.MAGIC) {
			return damage;
		} else if (damage.getDamageType() == DamageType.PHYSICAL) {
			double blockPercent = determineBlockPercentage(damage);
			if (Math.random() < blockPercent) {
				triggerBlock(damage, unit);
				return new Damage(0, damage.getDamageType(), damage.getSource(), unit, true);
			} else {
				return new Damage(filterThroughArmor(damage.getDamageAmount()), damage.getDamageType(), damage.getSource(),
						unit);
			}
		} else
			return null;
	}

	private double determineBlockPercentage(Damage damage) {
		// TODO make algorithm for determining whether it blocks it based on
		// previous blocks, direction of incoming damage, etc.
		return .65;
	}

	private static int filterThroughArmor(int damageAmount) {
		// TODO make algorithm for damage through armor
		return damageAmount;
	}

	private void triggerBlock(Damage damage, Unit target) {
		for (IncidentListener il : blockListeners) {
			il.incidentReported(damage, target);
		}
		turnPreviouslyBlockedOn = unit.getGame().getCurrentTurn();
	}

}
