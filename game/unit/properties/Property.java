package game.unit.properties;

import game.interaction.effect.PropAffectable;
import game.interaction.effect.PropEffect;
import game.interaction.incident.IncidentListener;
import game.interaction.incident.IncidentReporter;
import game.unit.Unit;

public abstract class Property<T> extends PropAffectable<T> {

	protected final Unit unit;

	private final IncidentReporter changeReporter;

	protected T property;

	public Property(Unit unit, T initValue) {
		this.unit = unit;
		this.property = initValue;
		changeReporter = new IncidentReporter();
	}

	public Unit getUnitOwner() {
		return unit;
	}

	@Override
	public void addPropEffect(PropEffect<T> effect) {
		T before = getProp();
		super.addPropEffect(effect);
		T after = getProp();
		if (!after.equals(before)) {
			propertyChanged(before, after);
		}
	}

	@Override
	public void removePropEffect(PropEffect<T> effect) {
		T before = getProp();
		super.removePropEffect(effect);
		T after = getProp();
		if (!after.equals(before)) {
			propertyChanged(before, after);
		}
	}

	public T getProp() {
		return getAffectedProp(property);
	}

	public void addPropertyListener(PropertyListener<T> pl) {
		changeReporter.add(pl);
	}

	protected void propertyChanged(T oldValue, T newValue, Object... specifications) {
		for (IncidentListener il : changeReporter) {
			il.incidentReported(oldValue, newValue, this, unit, specifications);
		}
	}
}
