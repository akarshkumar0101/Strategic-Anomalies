package game.unit.properties;

import java.util.ArrayList;
import java.util.List;

import game.unit.Unit;

public abstract class Property<T> {

	protected final Unit unit;
	private final List<PropertyListener<T>> listeners;

	public Property(Unit unit) {
		this.unit = unit;
		listeners = new ArrayList<>(4);
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
