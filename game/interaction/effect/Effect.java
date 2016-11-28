package game.interaction.effect;

/**
 * Effect is the interface for an effect, any effect to take place in the future
 * instantaneously or over time
 * 
 * @author akars
 *
 */
public interface Effect extends EffectSkeleton {

	public abstract Object performEffect(Object... args);

}
