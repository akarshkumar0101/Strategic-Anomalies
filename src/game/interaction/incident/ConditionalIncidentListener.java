package game.interaction.incident;

public abstract class ConditionalIncidentListener implements IncidentListener {

    private final Condition shouldExistCondition;

    public ConditionalIncidentListener(Condition shouldExistCondition) {
	this.shouldExistCondition = shouldExistCondition;
    }

    @Override
    public abstract void incidentReported(Object... specifications);

    @Override
    public Condition shouldExistCondition() {
	return shouldExistCondition;
    }

}
