package game.unit.property.ability;

import game.interaction.effect.EffectType;
import game.interaction.incident.IncidentReporter;
import game.unit.Unit;
import game.unit.property.Property;
import game.unit.property.PropertyEffect;
import game.unit.property.WaitProperty;

public abstract class ActiveAbility extends Ability {

    private final WaitProperty waitProp;

    private final Property<Boolean> canUseProperty;

    private final IncidentReporter onAbilityUseReporter;

    public ActiveAbility(Unit unitOwner, int maxWaitTime) {
	super(unitOwner);
	waitProp = new WaitProperty(unitOwner, 0, maxWaitTime);
	onAbilityUseReporter = new IncidentReporter();

	canUseProperty = new Property<>(unitOwner, true);

	canUseProperty.addPropEffect(new PropertyEffect<Boolean>(EffectType.PERMANENT, ActiveAbility.this, 1.0) {
	    @Override
	    public Boolean affectProperty(Boolean init) {
		return init && !getUnitOwner().getStunnedProp().getValue();
	    }
	});
	canUseProperty.addPropEffect(new PropertyEffect<Boolean>(EffectType.PERMANENT, ActiveAbility.this, 1.0) {
	    @Override
	    public Boolean affectProperty(Boolean init) {
		return init && !waitProp.isWaiting();
	    }
	});
    }

    public Property<Boolean> getCanUseProperty() {
	return canUseProperty;
    }

    public WaitProperty getWaitProp() {
	return waitProp;
    }

    public IncidentReporter getOnUseReporter() {
	return onAbilityUseReporter;
    }

    public boolean canUseAbility() {
	return canUseProperty.getValue();
    }

    protected void useAbility(Object... specs) {
	performAbility(specs);
	onAbilityUseReporter.reportIncident(this, specs);
    }

    protected abstract void performAbility(Object... specs);

}
