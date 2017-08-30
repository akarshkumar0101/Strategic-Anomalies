package game.interaction.effect;

/**
 * The expire type for an Effect. This dictates how an Effect will eventually
 * expire.
 * 
 * @author Akarsh
 *
 */
public enum EffectType {
    PERMANENT_BASE, PERMANENT_ACTIVE, TURN_BASED, RANGE_BASED, OTHER;
}
