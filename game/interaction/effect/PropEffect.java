package game.interaction.effect;

public interface PropEffect<T> {

	public abstract T affectProperty(T init);

	public abstract EffectType getEffectType();

	public abstract void updateExistance();

}
