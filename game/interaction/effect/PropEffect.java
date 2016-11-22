package game.interaction.effect;

public interface PropEffect<T> extends EffectSkeleton {

	public abstract T affectProperty(T init);

}
