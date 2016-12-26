package game.unit.ability;

import game.board.Square;
import game.unit.Unit;

public abstract class ActiveTargetAbilityProperty extends ActiveAbilityProperty {

    public ActiveTargetAbilityProperty(Unit unitOwner, int initialPower, int initialAttackRange) {
	super(unitOwner, initialPower, initialAttackRange);
    }

    public abstract boolean canUseAbilityOn(Square target);

}
