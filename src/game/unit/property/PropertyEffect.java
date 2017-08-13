package game.unit.property;

import game.interaction.effect.AbstractEffect;
import game.interaction.effect.EffectType;

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
 *            should match up with the Property<T> in order to affect it.
 */
public abstract class PropertyEffect<T> extends AbstractEffect {

    /**
     * This should be a value [0,10] basing on how important it is that this effect
     * have an effect on the property. The higher the priority, the more "last say"
     * this effect gets when affecting a property. Ex. if priority = 10, then it
     * will always get the last say when effecting the property.
     */
    private final double priority;

    /**
     * Default initializer for PropertyEffect.
     * 
     * @param effectType
     *            the type of Effect it is.
     * @param source
     *            the source of the Effect.
     */
    public PropertyEffect(EffectType effectType, Object source, double priority) {
	super(effectType, source);

	if (effectType == EffectType.PERMANENT) {
	    this.priority = 0;
	} else {
	    this.priority = priority;
	}

    }

    public double getPriority() {
	return priority;
    }

    /**
     * Affects the property value in a certain way and hands it back.
     * 
     * @param init
     *            the given value of the property to alter.
     * @return the affected value of the property.
     */
    public abstract T affectProperty(T initValue);

}
