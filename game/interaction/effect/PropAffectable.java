package game.interaction.effect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class PropAffectable<T> {

	private final List<PropEffect<T>> propEffects = new ArrayList<>(2);

	public List<PropEffect<T>> getPropEffects() {
		return propEffects;
	}

	public void addPropEffect(PropEffect<T> effect) {
		propEffects.add(effect);
	}

	public void removePropEffect(PropEffect<T> effect) {
		propEffects.remove(effect);
	}

	public T getAffectedProp(T prop) {
		for (PropEffect<T> propEffect : propEffects) {
			prop = propEffect.affectProperty(prop);
		}
		return prop;
	}

	public void updatePropEffectExistances() {
		Iterator<PropEffect<T>> it = propEffects.iterator();
		while (it.hasNext()) {
			PropEffect<T> propEffect = it.next();
			if (!propEffect.shouldExistance()) {
				it.remove();
			}
		}
	}
}
