package game.unit.property;

import game.board.Board;
import game.board.Coordinate;
import game.interaction.effect.EffectType;
import game.unit.Unit;

public class MovingProperty extends Property<Integer> {

    private final Property<Boolean> canMoveProp;
    private final Property<Boolean> teleportingProp;
    // stoic true means they do not move aside
    private final Property<Boolean> stoicProp;

    public MovingProperty(Unit unitOwner, Integer initMoveRange, Boolean initTeleportingValue) {
	super(unitOwner, initMoveRange);

	canMoveProp = new Property<>(unitOwner, true);
	teleportingProp = new Property<>(unitOwner, initTeleportingValue);
	stoicProp = new Property<>(unitOwner, true);

	unitOwner.getGame().gameStartReporter.add(specifications -> setupNaturalPropEffects());
    }

    private void setupNaturalPropEffects() {
	canMoveProp.addPropEffect(
		new PropertyEffect<Boolean>(EffectType.PERMANENT_ACTIVE, getUnitOwner().getStunnedProp(), 10) {
		    @Override
		    public Boolean affectProperty(Boolean initValue) {
			return initValue && !getUnitOwner().getStunnedProp().getValue();
		    }
		});
	canMoveProp.updateValueOnReporter(getUnitOwner().getStunnedProp().getChangeReporter());
	canMoveProp.addPropEffect(
		new PropertyEffect<Boolean>(EffectType.PERMANENT_ACTIVE, getUnitOwner().getWaitProp(), 10) {
		    @Override
		    public Boolean affectProperty(Boolean initValue) {
			return initValue && !getUnitOwner().getWaitProp().isWaiting();
		    }
		});
	canMoveProp.updateValueOnReporter(getUnitOwner().getWaitProp().getChangeReporter());
	stoicProp.addPropEffect(
		new PropertyEffect<Boolean>(EffectType.PERMANENT_ACTIVE, getUnitOwner().getStunnedProp(), 10) {
		    @Override
		    public Boolean affectProperty(Boolean initValue) {
			return initValue || getUnitOwner().getStunnedProp().getValue();
		    }
		});
	stoicProp.updateValueOnReporter(getUnitOwner().getStunnedProp().getChangeReporter());
    }

    public Property<Boolean> getCanMoveProp() {
	return canMoveProp;
    }

    public Property<Boolean> getTeleportingProp() {
	return teleportingProp;
    }

    public Property<Boolean> getStoicProp() {
	return stoicProp;
    }

    public boolean isInRangeOfWalking(Coordinate moveToCoor) {
	return Board.walkDist(moveToCoor, getUnitOwner().getPosProp().getValue()) <= getValue();
    }
}
