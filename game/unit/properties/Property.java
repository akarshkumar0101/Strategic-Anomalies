package game.unit.properties;

import java.util.ArrayList;
import java.util.List;

import game.interaction.effect.AffectableProp;
import game.unit.Unit;

public abstract class Property<T> extends AffectableProp<T> {

	protected final Unit unit;
	private final List<PropertyListener<T>> listeners;

	protected T property;

	public Property(Unit unit, T initValue) {
		this.unit = unit;
		this.property = initValue;
		listeners = new ArrayList<>(2);
	}

	public T getProp() {
		return getAffectedProp(property);
	}

	public void addPropertyListener(PropertyListener<T> pl) {
		listeners.add(pl);
	}

	protected void propertyChanged(T oldValue, T newValue) {
		for (PropertyListener<T> pl : listeners) {
			pl.propertyChanged(unit, this, oldValue, newValue);
		}
	}
}
