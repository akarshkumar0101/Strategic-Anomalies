package game.interaction.effect;

public interface Effect {

	public abstract Object performEffect(Object... args);

	public abstract EffectType getEffectType();

	public abstract void updateExistance();

}
