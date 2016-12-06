package game.unit.properties;

import game.interaction.effect.EffectSkeleton;
import game.interaction.effect.EffectType;
import game.interaction.incident.Condition;
import game.unit.Unit;

/**
 * PropertyEffects are used to add an effect that is altering the value of a
 * property for some time period/other limit (Example: doubling a unit's armor
 * while it is in range of a golem). Adding a PropertyEffect to a Property will
 * change its value according to the affectProperty() method implementation
 * below. It will expire according to the shouldExist() method of
 * EffectSkeleton.
 * 
 * @author Akarsh
 *
 * @param <T>
 *            should match up with Property<T> in order to affect it.
 */
public abstract class PropertyEffect<T> extends EffectSkeleton {

	/**
	 * Default initializer for PropertyEffect.
	 * 
	 * @param effectType
	 *            the type of Effect it is.
	 * @param source
	 *            the source of the Effect.
	 * @param shouldExist
	 *            the Condition in which it will still exist.
	 */
	public PropertyEffect(EffectType effectType, Unit source, Condition shouldExist) {
		super(effectType, source, shouldExist);
	}

	/**
	 * Affects the property value in a certain way and hands it back.
	 * 
	 * @param init
	 *            the given value of the property to alter.
	 * @return the affected value of the property.
	 */
	public abstract T affectProperty(T init);

}
