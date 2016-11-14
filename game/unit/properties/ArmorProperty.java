package game.unit.properties;

import game.unit.Unit;

public class ArmorProperty extends Property<Integer> {

	private int armor;

	public ArmorProperty(Unit unit) {
		super(unit);
		this.armor = unit.getDefaultArmor();
	}

	public int getArmor() {
		return armor;
	}
}
