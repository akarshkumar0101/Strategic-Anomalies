package game.unit.properties;

import game.board.Square;
import game.interaction.incident.IncidentReporter;
import game.unit.Unit;
import game.unit.ability.AbilityType;

//the value of the property IS the power.
public abstract class AbilityProperty extends Property<Integer> {

    private final Property<Integer> abilityRangeProp;

    private final IncidentReporter onUseReporter;

    public AbilityProperty(Unit unitOwner, int initialPower, int initialAttackRange) {
	super(unitOwner, initialPower);
	abilityRangeProp = new Property<>(unitOwner, initialAttackRange);

	onUseReporter = new IncidentReporter();
    }

    public abstract AbilityType getAbilityType();

    public abstract boolean canUseAbilityOn(Square target);

    public final void useAbility(Square target) {
	onUseReporter.reportIncident();
	performAbility(target);
    }

    public abstract void performAbility(Square target);

    public Property<Integer> getAbilityRangeProperty() {
	return abilityRangeProp;
    }

    public IncidentReporter getOnUseReporter() {
	return onUseReporter;
    }

}
