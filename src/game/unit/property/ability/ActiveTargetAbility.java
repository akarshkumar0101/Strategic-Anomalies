package game.unit.property.ability;

import java.util.List;

import game.board.Square;
import game.unit.Unit;

public abstract class ActiveTargetAbility extends ActiveAbility {

    public ActiveTargetAbility(Unit unitOwner, int maxWaitTime) {
	super(unitOwner, maxWaitTime);
    }

    public abstract List<Square> getAOESqaures(Square target);

    public abstract boolean canUseAbilityOn(Square target);

    public void useAbility(Square square) {
	super.useAbility(square);
    }

}
