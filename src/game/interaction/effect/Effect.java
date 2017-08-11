package game.interaction.effect;

/**
 * Effect is the interface to perform an action or series of actions some time
 * in the future. The performEffect() method will always be triggered by a
 * IncidentListener. The casts done inside of the performEffect() method should
 * take into consideration what will be calling the method and with what
 * arguments.<br>
 * <br>
 * 
 * The same Effect can be tied to different Affectable objects because
 * Affectable now keeps track of the triggers.
 * 
 * @author Akarsh
 *
 */
public abstract class Effect extends AbstractEffect {

    /**
     * Default initializer for Effect.
     *
     * @param effectType
     *            the type of Effect it is.
     * @param source
     *            the source of the Effect.
     */
    public Effect(EffectType effectType, Object source) {
	super(effectType, source);
    }

    /**
     * Runs the implemented code when the incident is reported. Be careful when
     * casting the objects by knowing what will call it.
     *
     * @param args
     *            the arguments sent to the Effect by a IncidentListener.
     */
    public abstract void performEffect(Affectable affectableObject, Object... args);

}
