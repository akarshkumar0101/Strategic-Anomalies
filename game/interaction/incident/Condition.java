package game.interaction.incident;

/**
 * An interface to test a if a condition is being met given certain arguments.
 * The casts done inside of this method should take into consideration what will
 * be calling the method and with what arguments.
 * 
 * @author Akarsh
 *
 */
@FunctionalInterface
public interface Condition {

    /**
     * Performs calculations to determine whether the condition is being met. Be
     * careful when casting the objects by knowing what will call it.
     * 
     * @param args
     *            the caller of the method will send these arguments to it
     * @return true if the condition is met
     */
    public abstract boolean performCondition(Object... args);

}
