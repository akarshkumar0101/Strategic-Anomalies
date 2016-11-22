package game.interaction.effect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AffectableProp<T> {

	private final List<PropEffect<T>> propEffects = new ArrayList<>(2);

	public List<PropEffect<T>> getEffects() {
		return propEffects;
	}

	public void addEffect(PropEffect<T> effect) {
		propEffects.add(effect);
	}

	public void removeEffect(PropEffect<T> effect) {
		propEffects.remove(effect);
	}

	public T getAffectedProp(T prop) {
		for (PropEffect<T> propEffect : propEffects) {
			prop = propEffect.affectProperty(prop);
		}
		return prop;
	}

	public void updateEffectExistances() {
		Iterator<PropEffect<T>> it = propEffects.iterator();
		while (it.hasNext()) {
			PropEffect<T> propEffect = it.next();
			if (!propEffect.shouldExistance()) {
				it.remove();
			}
		}
	}
}