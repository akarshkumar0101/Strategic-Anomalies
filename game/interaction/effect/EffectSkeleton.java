package game.interaction.effect;

import game.interaction.incident.Condition;
import game.unit.Unit;

/**
 * EffectSkeleton is a skeleton backbone that all effects have, such as an
 * EffectType, source Unit, and when the effect should disappear. The
 * EffectSkeleton subclasses can either choose to give a default EffectType,
 * source Unit, and Condition or just give null values and manually override and
 * implement these methods.
 * 
 * @author Akarsh
 */
public abstract class EffectSkeleton {

    /**
     * How the Effect will die out, or if it will at all.
     */
    private final EffectType effectType;

    /**
     * The source that generated this Effect.
     */
    private final Unit source;

    /**
     * Tells when the Effect goes away.
     */
    private final Condition shouldExist;

    /**
     * Default initializer for EffectSkeleton.
     * 
     * @param effectType
     *            the type of Effect it is.
     * @param source
     *            the source of the Effect.
     * @param shouldExist
     *            the Condition in which it will still exist.
     */
    public EffectSkeleton(EffectType effectType, Unit source, Condition shouldExist) {
	this.effectType = effectType;
	this.source = source;
	this.shouldExist = shouldExist;
    }

    /**
     * If using this constructor to initialize the EffectSkeleton, then the
     * implementation of the EffectSkeleton should manually override and
     * implement the other methods of this class.
     */
    public EffectSkeleton() {
	effectType = null;
	source = null;
	shouldExist = null;
    }

    /**
     * @return the type of Effect
     */
    public EffectType getEffectType() {
	return effectType;
    }

    /**
     * @return the source of the Effect
     */
    public Unit getSource() {
	return source;
    }

    /**
     * @return true if this effect has a condition of existence. If the
     *         condition is null, return false;
     */
    public boolean hasExistenceCondition() {
	return !(shouldExist == null);
    }

    /**
     * 
     * @return whether the effect should still exist
     */
    public boolean shouldExist() {
	return shouldExist.performCondition();
    }

}
