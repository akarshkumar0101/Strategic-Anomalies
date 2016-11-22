package game.interaction.effect;

import java.util.ArrayList;
import java.util.Iterator;
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
		Iterator<Effect> it = effects.iterator();
		while(it.hasNext()) {
			Effect effect = it.next();
			if(!effect.shouldExistance()){
				it.remove();
			}
		}
	}

}
