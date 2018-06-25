package game.unit.listofunits;

import java.util.List;

import game.Game;
import game.Player;
import game.Turn;
import game.board.Coordinate;
import game.board.Direction;
import game.board.Square;
import game.interaction.effect.EffectType;
import game.interaction.incident.IncidentListener;
import game.unit.Unit;
import game.unit.UnitStat;
import game.unit.property.PropertyEffect;
import game.unit.property.ability.Ability;
import game.unit.property.ability.AbilityAOE;
import game.unit.property.ability.ActiveAbility;

public class Chanty extends Unit {

    public Chanty(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	super(game, playerOwner, directionFacing, coor);
    }

    @Override
    public Ability getDefaultAbility() {
	UnitStat defaultStat = getDefaultStat();
	Ability ability = new ChantyAbility(this, defaultStat.defaultAttackRange, defaultStat.defaultWaitTime);
	return ability;
    }
}

class ChantyAbility extends ActiveAbility implements AbilityAOE {

    public ChantyAbility(Unit unitOwner, int initialRange, int maxWaitTime) {
	super(unitOwner, maxWaitTime);

    }

    @Override
    public List<Square> getAOESqaures(Square target) {
	return getUnitOwner().getGame().getBoard().squaresInRange(target, 2);
    }

    @Override
    protected void performAbility(Object... specs) {
	Square target = (Square) specs[0];
	for (Square s : getAOESqaures(target)) {
	    if (!s.isEmpty()) {
		PropertyEffect<Boolean> effect = new PropertyEffect<Boolean>(EffectType.TEMPORARY_ACTIVE,
			ChantyAbility.this, 1) {

		    @Override
		    public Boolean affectProperty(Boolean initValue) {
			return true;
		    }
		};
		s.getUnitOnTop().getStunnedProp().addPropEffect(effect);

		int spellEndTurnNumber = getUnitOwner().getGame().getCurrentTurn().getTurnNumber() + 2;

		getUnitOwner().getGame().turnEndReporter.add(new IncidentListener() {
		    @Override
		    public void incidentReported(Object... specifications) {
			Turn currentTurn = (Turn) specifications[0];
			if (currentTurn.getTurnNumber() == spellEndTurnNumber) {
			    s.getUnitOnTop().getStunnedProp().removePropEffect(effect);
			    getUnitOwner().getGame().turnEndReporter.remove(this);
			}
		    }
		});
	    }
	}
    }

}
