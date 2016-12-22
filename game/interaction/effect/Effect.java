package game.interaction.effect;

import java.util.HashMap;

import game.interaction.incident.Condition;
import game.interaction.incident.IncidentListener;
import game.interaction.incident.IncidentReporter;
import game.unit.Unit;

/**
 * Effect is the interface to perform an action or series of actions some time
 * in the future. The performEffect() method will always be triggered by a
 * IncidentListener. The casts done inside of the performEffect() method should
 * take into consideration what will be calling the method and with what
 * arguments.
 *
 * @author Akarsh
 *
 */
public abstract class Effect extends EffectSkeleton {

    /**
     * The IncidentReporters and IncidentListeners that can currently trigger
     * this Effect.
     */
    private final HashMap<IncidentReporter, IncidentListener> triggers;

    /**
     * Default initializer for Effect.
     *
     * @param effectType
     *            the type of Effect it is.
     * @param source
     *            the source of the Effect.
     * @param shouldExist
     *            the Condition in which it will still exist.
     */
    public Effect(EffectType effectType, Unit source, Condition shouldExist) {
	super(effectType, source, shouldExist);
	triggers = new HashMap<>(1);
    }

    /**
     * Runs the implemented code when the incident is reported. Be careful when
     * casting the objects by knowing what will call it.
     *
     * @param args
     *            the arguments sent to the Effect by a IncidentListener.
     */
    public abstract void performEffect(Object... args);

    /**
     * Removes all of the IncidentListeners that can trigger this effect from
     * their respective IncidentReporters. The Effect object should be forgotten
     * by the Affectable by removing it from the list.
     */
    public void deleteEffect() {
	for (IncidentReporter reporter : triggers.keySet()) {
	    reporter.remove(triggers.get(reporter));
	}
    }

    /**
     * Marks the reporter and listener as a type of trigger for this Effect.
     *
     * @param reporter
     *            the IncidentReporter that reports to the IncidentListener that
     *            triggers this
     * @param listener
     *            the IncidentListener that triggers this.
     */
    public void addTrigger(IncidentReporter reporter, IncidentListener listener) {
	triggers.put(reporter, listener);
    }

    /**
     * Removes the IncidentReporter from the list of triggers and removes the
     * IncidentListener from the IncidentReporter.
     *
     * @param reporter
     *            the IncidentReporter that reports to the IncidentListener that
     *            triggers this
     * @param listener
     *            the IncidentListener that triggers this.
     */
    public void removeTrigger(IncidentReporter reporter, IncidentListener listener) {
	if (triggers.remove(reporter, listener)) {
	    reporter.remove(listener);
	}
    }

}
