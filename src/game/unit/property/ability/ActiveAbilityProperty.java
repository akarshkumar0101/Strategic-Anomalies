package game.unit.property.ability;

import java.util.List;

import game.board.Square;
import game.interaction.effect.EffectType;
import game.interaction.incident.IncidentReporter;
import game.unit.Unit;
import game.unit.property.Property;
import game.unit.property.PropertyEffect;
import game.unit.property.WaitProperty;

public abstract class ActiveAbilityProperty extends AbilityProperty {

    private final WaitProperty waitProp;

    private final IncidentReporter onAbilityUseReporter;

    private final Property<Boolean> canUseProperty;

    public ActiveAbilityProperty(Unit unitOwner, int initialPower, int initialAttackRange, int maxWaitTime) {
	super(unitOwner, initialPower, initialAttackRange);
	waitProp = new WaitProperty(unitOwner, 0, maxWaitTime);
	onAbilityUseReporter = new IncidentReporter();

	canUseProperty = new Property<Boolean>(unitOwner, true) {
	    @Override
	    protected Object[] getSpecificationsOfPropertyChange(Boolean oldValue, Boolean newValue) {
		return null;
	    }
	};
	canUseProperty.addPropEffect(getStunDeniesAbilityEffect());
	canUseProperty.addPropEffect(getWaitDeniesAbilityEffect());
    }

    private PropertyEffect<Boolean> getStunDeniesAbilityEffect() {
	PropertyEffect<Boolean> effect = new PropertyEffect<Boolean>(EffectType.PERMANENT, ActiveAbilityProperty.this,
		null, 1.0) {
	    @Override
	    public Boolean affectProperty(Boolean init) {
		return init && !getUnitOwner().getStunnedProp().getCurrentPropertyValue();
	    }
	};
	return effect;
    }

    private PropertyEffect<Boolean> getWaitDeniesAbilityEffect() {
	PropertyEffect<Boolean> effect = new PropertyEffect<Boolean>(EffectType.PERMANENT, ActiveAbilityProperty.this,
		null, 1.0) {
	    @Override
	    public Boolean affectProperty(Boolean init) {
		return init && !waitProp.isWaiting();
	    }
	};
	return effect;
    }

    public WaitProperty getWaitProp() {
	return waitProp;
    }

    public IncidentReporter getOnUseReporter() {
	return onAbilityUseReporter;
    }

    @Override
    public boolean canCurrentlyUseAbility() {
	return canUseProperty.getCurrentPropertyValue();
    }

    public abstract List<Square> getAOESqaures(Square target);

    public final void useAbility(Square target) {
	performAbility(target);
	onAbilityUseReporter.reportIncident(this, target);
    }

    public abstract void performAbility(Square target);

}
