package game.interaction.effect;

import java.util.ArrayList;
import java.util.List;

public abstract class Affectable {

	private final List<Effect> effects = new ArrayList<>(2);

	public List<Effect> getEffects() {
		return effects;
	}

	public void addEffect(Effect effect) {
		effects.add(effect);
	}

	public void removeEffect(Effect effect) {
		effects.remove(effect);
	}

	public void updateEffectExistances() {
		for (Effect effect : effects) {
			effect.updateExistance();
		}
	}

}
