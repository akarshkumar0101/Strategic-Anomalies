package game.unit.property.ability;

import java.util.List;

import game.board.Square;
import game.interaction.incident.IncidentReporter;
import game.unit.Unit;
import game.unit.property.WaitProperty;

public abstract class ActiveAbilityProperty extends AbilityProperty {

    private final WaitProperty waitProp;

    private final IncidentReporter onAbilityUseReporter;

    public ActiveAbilityProperty(Unit unitOwner, int initialPower, int initialAttackRange, int maxWaitTime) {
	super(unitOwner, initialPower, initialAttackRange);
	waitProp = new WaitProperty(unitOwner, 0, maxWaitTime);
	onAbilityUseReporter = new IncidentReporter();
    }

    public WaitProperty getWaitProp() {
	return waitProp;
    }

    public IncidentReporter getOnUseReporter() {
	return onAbilityUseReporter;
    }

    public boolean canCurrentlyUseAbility() {
	return !(getUnitOwner().getStunnedProp().getCurrentPropertyValue() || getUnitOwner().getWaitProp().isWaiting());
    }

    public abstract List<Square> getAOESqaures(Square target);

    public final void useAbility(Square target) {
	performAbility(target);
	onAbilityUseReporter.reportIncident(this, target);
    }

    public abstract void performAbility(Square target);

}
