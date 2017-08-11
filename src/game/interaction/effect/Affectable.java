package game.interaction.effect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import game.interaction.incident.Condition;
import game.interaction.incident.IncidentListener;
import game.interaction.incident.IncidentReporter;

/**
 * All parts of the game that can be affected by Effects that cause something to
 * happen in the future should extend this Affectable object.
 *
 * @author Akarsh
 *
 */
public abstract class Affectable {

    /**
     * The list of effects to keep track of for the Affectable object along with
     * what triggers it.
     */
    private final Map<Effect, EffectTrigger> effects;

    public Affectable() {
	effects = new HashMap<>(2);
    }

    /**
     * @return the list of Effects currently acting on the Affectable.
     */
    public Set<Effect> getEffects() {
	return effects.keySet();
    }

    /**
     * Adds the given Effect to the list of Effects and manages all of the trigger
     * issues about when, how, and what triggers it by adding the an
     * IncidentListener to the IncidentReporter trigger. The Effect will
     * automatically timeout and all ties will be removed by regularly calling the
     * updateEffectExistances() method.
     *
     * @param effect
     *            the effect to perform
     * @param trigger
     *            what must trigger for this to run
     */
    public void addEffect(Effect effect, IncidentReporter... triggers) {
	addEffect(effect, Condition.trueCondition, triggers);
    }

    /**
     * Adds the given Effect to the list of Effects and manages all of the trigger
     * issues about when, how, and what triggers it by adding the an
     * IncidentListener to the IncidentReporter trigger. The Effect will
     * automatically timeout and all ties will be removed by regularly calling the
     * updateEffectExistances() method.
     *
     * @param effect
     *            the effect to perform
     * @param trigger
     *            what must trigger for this to run
     * @param conditionToRunOn
     *            will trigger the effect only if this condition is met at the time
     *            of the incident report
     */
    public void addEffect(Effect effect, Condition conditionToRunOn, IncidentReporter... triggers) {
	EffectTrigger effectTrigger = new EffectTrigger(effect, conditionToRunOn, triggers);
	effects.put(effect, effectTrigger);
    }

    /**
     * Removes and deletes the Effect from existence by deleting all ties associated
     * with it.
     *
     * @param effect
     */
    public void removeEffect(Effect effect) {
	EffectTrigger effectTrigger = effects.get(effect);

	effectTrigger.removeAllTriggers();
	effects.remove(effect, effectTrigger);
    }

    public void addTriggersToEffect(Effect effect, IncidentReporter... triggers) {
	EffectTrigger effectTrigger = effects.get(effect);
	if (effectTrigger != null) {
	    for (IncidentReporter trigger : triggers) {
		effectTrigger.addTrigger(trigger);
	    }
	}
    }

    public void removeTriggersFromEffect(Effect effect, IncidentReporter... triggers) {
	EffectTrigger effectTrigger = effects.get(effect);
	if (effectTrigger != null) {
	    for (IncidentReporter trigger : triggers) {
		effectTrigger.removeTrigger(trigger);
	    }
	}
    }

    class EffectTrigger {

	private final Condition conditionToRunOn;

	private final List<IncidentReporter> triggers;

	private final IncidentListener listener;

	public EffectTrigger(Effect effect, Condition condRunOn, IncidentReporter... triggers) {
	    this.triggers = new ArrayList<>(triggers.length);
	    conditionToRunOn = condRunOn;
	    listener = specifications -> {
		if (conditionToRunOn.performCondition(specifications)) {
		    effect.performEffect(Affectable.this, specifications);
		}
	    };

	    for (IncidentReporter trigger : triggers) {
		addTrigger(trigger);
	    }
	}

	private void addTrigger(IncidentReporter trigger) {
	    triggers.add(trigger);
	    trigger.add(listener);
	}

	private void removeTrigger(IncidentReporter trigger) {
	    triggers.remove(trigger);
	    trigger.remove(listener);
	}

	private void removeAllTriggers() {
	    for (int i = triggers.size() - 1; i >= 0; i--) {
		removeTrigger(triggers.get(i));
	    }
	}

    }
}
