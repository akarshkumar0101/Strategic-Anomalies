package game.unit.listofunits;

import java.util.ArrayList;
import java.util.List;

import game.Game;
import game.Player;
import game.board.Coordinate;
import game.board.Direction;
import game.board.Square;
import game.interaction.Damage;
import game.interaction.DamageType;
import game.unit.Unit;
import game.unit.UnitStat;
import game.unit.property.ability.AbilityProperty;
import game.unit.property.ability.ActiveTargetAbilityProperty;

public class Cleric extends Unit {

    public Cleric(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	super(game, playerOwner, directionFacing, coor);
    }

    @Override
    public AbilityProperty getDefaultAbilityProperty() {
	UnitStat defaultStat = getDefaultStat();
	AbilityProperty abilityProp = new HealerAbilityProperty(this, defaultStat.defaultPower,
		defaultStat.defaultAttackRange, defaultStat.defaultWaitTime);
	return abilityProp;
    }
}

class HealerAbilityProperty extends ActiveTargetAbilityProperty {

    public HealerAbilityProperty(Unit unitOwner, int initialPower, int initialAttackRange, int maxWaitTime) {
	super(unitOwner, initialPower, initialAttackRange, maxWaitTime);
    }

    @Override
    public boolean canUseAbilityOn(Square target) {
	return true;
    }

    @Override
    public List<Square> getAOESqaures(Square target) {
	List<Square> list = new ArrayList<>(1);

	return list;
    }

    @Override
    public void performAbility(Square target) {
	if (!canUseAbilityOn(target)) {
	    return;
	}
	List<Square> targets = getAOESqaures(target);
	for (Square ss : targets) {
	    Damage damage = new Damage(getCurrentPropertyValue(), DamageType.MAGIC, getUnitOwner(), ss.getUnitOnTop());
	    ss.getUnitOnTop().getHealthProp().takeDamage(damage);
	}
    }

}
