package game.unit.property.ability;

import game.unit.Unit;

public abstract class Ability {

    private final Unit unitOwner;

    public Ability(Unit unitOwner) {
	this.unitOwner = unitOwner;
    }

    public Unit getUnitOwner() {
	return unitOwner;
    }

}
