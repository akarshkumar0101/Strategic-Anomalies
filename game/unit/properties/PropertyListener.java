package game.unit.properties;

import game.unit.Unit;

public interface PropertyListener<T> {

	public abstract void propertyChanged(Unit unit, Property<T> property, T oldValue, T newValue);

}
