package game.unit.property.ability;

import game.board.Square;
import game.unit.Unit;

public abstract class ActiveTargetAbilityProperty extends ActiveAbilityProperty {

    public ActiveTargetAbilityProperty(Unit unitOwner, int initialPower, int initialAttackRange, int maxWaitTime) {
	super(unitOwner, initialPower, initialAttackRange, maxWaitTime);
    }

    public abstract boolean canUseAbilityOn(Square target);

}
