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

    public boolean attemptBlock(Damage damage) {
	// determine if it blocked the damage
	double blockPercent = determineBlockPercentage(damage);
	if (damage.getDamageType().equals(DamageType.PHYSICAL) && Math.random() < blockPercent) {
	    triggerBlock(damage);
	    return true;
	}

	return false;
    }

    public Damage filterDamage(Damage damage) {
	return new Damage(filterThroughArmor(damage.getDamageAmount()), damage.getDamageType(), damage.getSource(),
		getUnitOwner());
    }

    private double determineBlockPercentage(Damage damage) {
	// TODO make algorithm for determining whether it blocks it based on
	// damage type,previous blocks, direction of incoming damage, whether it
	// is stunned, etc.
	return .65;
    }

    private int filterThroughArmor(int damageAmount) {
	// TODO make algorithm for damage through armor
	return damageAmount;
    }

    private void triggerBlock(Damage damage) {
	blockReporter.reportIncident(damage);
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
