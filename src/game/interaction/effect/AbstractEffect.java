package game.interaction.effect;

/**
 * AbstractEffect is a skeleton backbone that all effects have, such as an
 * EffectType and source Unit. The EffectSkeleton subclasses can either choose
 * to give a default EffectType, source Unit, and Condition or just give null
 * values and manually override and implement these methods.
 * 
 * @author Akarsh
 */
public abstract class AbstractEffect {

    /**
     * How the Effect will die out, or if it will at all.
     */
    private final EffectType effectType;

    /**
     * The source that generated this Effect.
     */
    private final Object source;

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
    public AbstractEffect(EffectType effectType, Object source) {
	this.effectType = effectType;
	this.source = source;
    }

    /**
     * If using this constructor to initialize the EffectSkeleton, then the
     * implementation of the EffectSkeleton should manually override and implement
     * the other methods of this class.
     */
    public AbstractEffect() {
	this(null, null);
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
    public Object getSource() {
	return source;
    }
}
