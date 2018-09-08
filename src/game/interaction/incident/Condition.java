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
    public static final Condition trueCondition = args -> true;
    public static final Condition falseCondition = args -> false;
    // public static final Condition randomCondition = args -> Math.random() > .5;

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
