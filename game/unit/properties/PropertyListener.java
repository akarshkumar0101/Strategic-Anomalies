package game.unit.properties;

import game.unit.Unit;

public interface PropertyListener<T> {

	/**
	 * @param unit
	 * @param property
	 * @param oldValue
	 * @param newValue
	 * @param aditionalArgs
	 *            optional additional arguments to specify the change
	 */
	public abstract void propertyChanged(Unit unit, Property<T> property, T oldValue, T newValue,
			Object... aditionalArgs);

}
