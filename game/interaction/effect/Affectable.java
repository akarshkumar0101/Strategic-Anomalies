package game.interaction.effect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	 * The list of effects to keep track of for the Affectable object.
	 */
	private final List<Effect> effects = new ArrayList<>(2);

	/**
	 * @return the list of Effects currently acting on the Affectable.
	 */
	public List<Effect> getEffects() {
		return effects;
	}

	/**
	 * Adds the given Effect to the list of Effects and manages all of the
	 * trigger issues about when, how, and what triggers it by adding the an
	 * IncidentListener to the IncidentReporter trigger. The Effect will
	 * automatically timeout and all ties will be removed by regularly calling
	 * the updateEffectExistances() method.
	 *
	 * @param effect
	 *            the effect to perform
	 * @param trigger
	 *            what must trigger for this to run
	 * @param conditionToRunOn
	 *            will trigger the effect only if this condition is met at the
	 *            time of the incident report
	 */
	public void addEffect(Effect effect, IncidentReporter trigger, Condition conditionToRunOn) {
		effects.add(effect);
		IncidentListener listener = specifications -> {
			if (conditionToRunOn.performCondition(specifications)) {
				effect.performEffect(specifications);
			}
		};
		trigger.add(listener);
		effect.addTrigger(trigger, listener);
	}

	/**
	 * Adds the given Effect to the list of Effects and manages all of the
	 * trigger issues about when, how, and what triggers it by adding the an
	 * IncidentListener to the IncidentReporter trigger. The Effect will
	 * automatically timeout and all ties will be removed by regularly calling
	 * the updateEffectExistances() method.
	 *
	 * @param effect
	 *            the effect to perform
	 * @param trigger
	 *            what must trigger for this to run
	 */
	public void addEffect(Effect effect, IncidentReporter trigger) {
		addEffect(effect, trigger, args -> true);
	}

	/**
	 * Removes and deletes the Effect from existence by deleting all ties
	 * associated with it.
	 *
	 * @param effect
	 */
	public void removeEffect(Effect effect) {
		effects.remove(effect);
		effect.deleteEffect();
	}

	/**
	 * Updates the existences of the Effects. This method should be called
	 * regularly to keep the Effects up to date.
	 */
	public void updateEffectExistences() {
		Iterator<Effect> it = effects.iterator();
		while (it.hasNext()) {
			Effect effect = it.next();
			if (!effect.shouldExist()) {
				it.remove();
				effect.deleteEffect();
			}
		}
	}

}
