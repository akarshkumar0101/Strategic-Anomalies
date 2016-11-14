package game.stat;

import java.util.ArrayList;
import java.util.List;

import game.unit.Unit;

public abstract class Property<T> {

	private final List<PropertyListener<T>> listeners = new ArrayList<>(2);

	public void addPropertyListener(PropertyListener<T> pl) {
		listeners.add(pl);
	}

	protected void propertyChanged(Unit unit, T oldValue, T newValue) {
		for (PropertyListener<T> pl : listeners) {
			pl.propertyChanged(unit, this, oldValue, newValue);
		}
	}
}
