package game.unit.listofunits;

import game.Game;
import game.Player;
import game.board.Coordinate;
import game.board.Direction;
import game.interaction.Healing;
import game.unit.Unit;
import game.unit.UnitStat;
import game.unit.property.Property;
import game.unit.property.ability.Ability;
import game.unit.property.ability.AbilityPower;
import game.unit.property.ability.ActiveAbility;

public class Cleric extends Unit {

    public Cleric(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	super(game, playerOwner, directionFacing, coor);
    }

    @Override
    public Ability getDefaultAbility() {
	UnitStat defaultStat = getDefaultStat();
	Ability ability = new HealerAbility(this, defaultStat.defaultPower, defaultStat.defaultWaitTime);
	return ability;
    }
}

class HealerAbility extends ActiveAbility implements AbilityPower {
    private final Property<Integer> abilityPowerProp;

    public HealerAbility(Unit unitOwner, int initialPower, int maxWaitTime) {
	super(unitOwner, maxWaitTime);

	abilityPowerProp = new Property<>(unitOwner, initialPower);
    }

    @Override
    public Property<Integer> getAbilityPowerProperty() {
	return abilityPowerProp;
    }

    @Override
    protected void performAbility(Object... specs) {
	for (Unit unit : getUnitOwner().getGame().getAllUnits()) {
	    if (Unit.areAllies(getUnitOwner(), unit)) {
		Healing healing = new Healing(abilityPowerProp.getValue(), this, unit);
		unit.getHealthProp().takeHealing(healing);
	    }
	}
    }

}
