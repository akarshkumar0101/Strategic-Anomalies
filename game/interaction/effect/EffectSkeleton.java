package game.interaction.effect;

import game.unit.Unit;

interface EffectSkeleton {

	public abstract EffectType getEffectType();

	public abstract Unit getSource();

	public abstract boolean shouldExistance();

}
