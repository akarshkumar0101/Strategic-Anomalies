package game.unit.properties;

import game.Turn;
import game.interaction.Damage;
import game.interaction.DamageType;
import game.interaction.incident.IncidentReporter;
import game.unit.Unit;

public class ArmorProperty extends Property<Integer> {

    private final IncidentReporter blockReporter;

    private Turn turnPreviouslyBlockedOn;

    public ArmorProperty(Unit unit, int initialArmor) {
	super(unit, initialArmor);

	blockReporter = new IncidentReporter();
	turnPreviouslyBlockedOn = null;

    }

    public Damage filterDamage(Damage damage) {
	if (damage.getDamageType() == DamageType.MAGIC) {
	    return damage;
	} else if (damage.getDamageType() == DamageType.PHYSICAL) {
	    double blockPercent = determineBlockPercentage(damage);
	    if (Math.random() < blockPercent) {
		triggerBlock(damage, getUnitOwner());
		return new Damage(0, damage.getDamageType(), damage.getSource(), getUnitOwner(), true);
	    } else {
		return new Damage(filterThroughArmor(damage.getDamageAmount()), damage.getDamageType(),
			damage.getSource(), getUnitOwner());
	    }
	} else {
	    return null;
	}
    }

    private double determineBlockPercentage(Damage damage) {
	// TODO make algorithm for determining whether it blocks it based on
	// previous blocks, direction of incoming damage, etc.
	return .65;
    }

    private int filterThroughArmor(int damageAmount) {
	// TODO make algorithm for damage through armor
	return damageAmount;
    }

    private void triggerBlock(Damage damage, Unit target) {
	blockReporter.reportIncident(damage, target);
	turnPreviouslyBlockedOn = getUnitOwner().getGame().getCurrentTurn();
    }

    public IncidentReporter getBlockReporter() {
	return blockReporter;
    }

    @Override
    protected void propertyChanged(Integer oldValue, Integer newValue) {
	super.notifyPropertyChanged(oldValue, newValue);
    }

}
