package game.unit.property;

import game.Turn;
import game.board.Coordinate;
import game.board.Direction;
import game.interaction.Damage;
import game.interaction.DamageType;
import game.interaction.incident.IncidentReporter;
import game.unit.Unit;
import game.unit.property.ability.Ability;

public class ArmorProperty extends Property<Integer> {

    private final IncidentReporter blockReporter;

    public Turn turnPreviouslyBlockedOn;

    public ArmorProperty(Unit unit, int initialArmor) {
	super(unit, initialArmor);

	blockReporter = new IncidentReporter();
	turnPreviouslyBlockedOn = null;
    }

    public boolean attemptBlock(Damage damage) {
	// determine if it blocked the damage
	double blockPercent = determineBlockPercentage(damage);
	// double random = Math.random();
	double random = getUnitOwner().getGame().random.nextDouble();
	if (damage.getDamageType().equals(DamageType.PHYSICAL) && random < blockPercent) {
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
	// previous blocks, direction of incoming damage, whether it
	// is stunned, etc.

	// fix this lol
	return 1;
    }

    private int filterThroughArmor(int damageAmount) {
	// TODO make algorithm for damage through armor
	return damageAmount;
    }

    private void triggerBlock(Damage damage) {
	// turn this unit to block
	System.out.println("block");
	if (damage.getSource() instanceof Unit || damage.getSource() instanceof Ability) {
	    System.out.println("unit source");
	    Coordinate thiscoor = getUnitOwner().getPosProp().getValue();
	    Coordinate othercoor = null;
	    if (damage.getSource() instanceof Unit) {
		othercoor = ((Unit) damage.getSource()).getPosProp().getValue();
	    } else if (damage.getSource() instanceof Ability) {
		othercoor = ((Ability) damage.getSource()).getUnitOwner().getPosProp().getValue();
	    }

	    Direction damageDir = Coordinate.inGeneralDirection(thiscoor, othercoor);

	    getUnitOwner().getPosProp().getDirFacingProp().setValue(damageDir, this);

	}
	turnPreviouslyBlockedOn = getUnitOwner().getGame().getCurrentTurn();
	blockReporter.reportIncident(damage);

    }

    public IncidentReporter getBlockReporter() {
	return blockReporter;
    }

}
