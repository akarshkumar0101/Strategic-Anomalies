package game.interaction.effect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import game.interaction.incident.Condition;
import game.interaction.incident.IncidentListener;
import game.interaction.incident.IncidentReporter;

public abstract class Affectable {

	private final List<Effect> effects = new ArrayList<>(2);

	public List<Effect> getEffects() {
		return effects;
	}

	public void addEffect(Effect effect, IncidentReporter trigger, Condition conditionToRunOn) {
		effects.add(effect);
		trigger.add(new IncidentListener() {
			@Override
			public void incidentReported(Object... specifications) {
				if (conditionToRunOn.performCondition(specifications)) {
					effect.performEffect(specifications);
				}
			}
		});
	}

	public void removeEffect(Effect effect) {
		effects.remove(effect);
	}

	public void updateEffectExistances() {
		Iterator<Effect> it = effects.iterator();
		while (it.hasNext()) {
			Effect effect = it.next();
			if (!effect.shouldExistance()) {
				it.remove();
			}
		}
	}

}
