package game.unit.property;

import game.Turn;
import game.board.Coordinate;
import game.board.Direction;
import game.interaction.Damage;
import game.interaction.DamageType;
import game.interaction.effect.EffectType;
import game.interaction.incident.IncidentReporter;
import game.unit.Unit;
import game.unit.property.ability.Ability;

public class ArmorProperty extends Property<Integer> {

    private final IncidentReporter onAttackReporter;
    private final IncidentReporter blockReporter;

    private Turn lastBlockingIncident;
    private boolean didBlock;

    private final Property<Double> frontBlockProperty;
    private final Property<Double> sideBlockProperty;

    public ArmorProperty(Unit unitOwner, int initialArmor, double frontBlockPercent, double sideBlockPercent) {
	super(unitOwner, initialArmor);

	frontBlockProperty = new Property<>(unitOwner, frontBlockPercent);
	sideBlockProperty = new Property<>(unitOwner, sideBlockPercent);

	onAttackReporter = new IncidentReporter();
	blockReporter = new IncidentReporter();

	lastBlockingIncident = null;
	didBlock = false;

	unitOwner.getGame().gameStartReporter.add(specifications -> setupNaturalPropEffects());
    }

    private void setupNaturalPropEffects() {
	PropertyEffect<Double> overTurnsBlockingPercentEffect = new PropertyEffect<Double>(EffectType.PERMANENT_ACTIVE,
		this, 1) {
	    @Override
	    public Double affectProperty(Double init) {
		return ArmorProperty.blockingAlgorithmOverTurns(lastBlockingIncident, didBlock,
			getUnitOwner().getGame().getCurrentTurn(), init);
	    }
	};
	PropertyEffect<Double> cantBlockWhenStunnedEffect = new PropertyEffect<Double>(EffectType.PERMANENT_ACTIVE,
		getUnitOwner().getStunnedProp(), 2) {
	    @Override
	    public Double affectProperty(Double initValue) {
		if (getUnitOwner().getStunnedProp().getValue()) {
		    return 0.0;
		} else {
		    return initValue;
		}
	    }
	};
	frontBlockProperty.addPropEffect(overTurnsBlockingPercentEffect);
	sideBlockProperty.addPropEffect(overTurnsBlockingPercentEffect);
	frontBlockProperty.updateValueOnReporter(getUnitOwner().getGame().turnStartReporter);
	sideBlockProperty.updateValueOnReporter(getUnitOwner().getGame().turnStartReporter);

	frontBlockProperty.addPropEffect(cantBlockWhenStunnedEffect);
	sideBlockProperty.addPropEffect(cantBlockWhenStunnedEffect);
	frontBlockProperty.updateValueOnReporter(getUnitOwner().getStunnedProp().getChangeReporter());
	sideBlockProperty.updateValueOnReporter(getUnitOwner().getStunnedProp().getChangeReporter());
    }

    public Property<Double> getFrontBlockProperty() {
	return frontBlockProperty;
    }

    public Property<Double> getSideBlockProperty() {
	return sideBlockProperty;
    }

    public boolean attemptBlock(Damage damage) {

	onAttackReporter.reportIncident(damage);
	// determine if it blocked the damage
	if (DamageType.PHYSICAL.equals(damage.getDamageType())) {

	    Direction dirDamage = getIncomingDamageDirection(damage);
	    Direction dirFacing = getUnitOwner().getPosProp().getDirFacingProp().getValue();

	    if (!dirFacing.getOpposite().equals(dirDamage)) {

		double blockPercent = 0;
		if (dirFacing.equals(dirDamage)) {
		    blockPercent = frontBlockProperty.getValue();
		} else {
		    blockPercent = sideBlockProperty.getValue();
		}
		if (blockPercent != 0) {
		    double random = getUnitOwner().getGame().random.nextDouble();

		    boolean didBlock = random < blockPercent;

		    if (didBlock) {
			triggerBlock(damage);
		    }
		    triggerBlockingIncident(getUnitOwner().getGame().getCurrentTurn(), didBlock);

		    return this.didBlock;
		}
	    }

	}
	return false;
    }

    public Damage filterDamage(Damage damage) {
	return new Damage(filterThroughArmor(damage.getDamageAmount()), damage.getDamageType(), damage.getSource(),
		getUnitOwner());
    }

    private void triggerBlockingIncident(Turn turn, boolean didBlock) {
	lastBlockingIncident = turn;
	this.didBlock = didBlock;

	frontBlockProperty.updateValue();
	sideBlockProperty.updateValue();
    }

    private int filterThroughArmor(int damageAmount) {
	// TODO make algorithm for damage through armor
	return damageAmount;
    }

    private Direction getIncomingDamageDirection(Damage damage) {
	Coordinate thiscoor = getUnitOwner().getPosProp().getValue();
	Coordinate othercoor = null;
	if (damage.getSource() instanceof Unit) {
	    othercoor = ((Unit) damage.getSource()).getPosProp().getValue();
	} else if (damage.getSource() instanceof Ability) {
	    othercoor = ((Ability) damage.getSource()).getUnitOwner().getPosProp().getValue();
	}

	Direction damageDir = Coordinate.inGeneralDirection(thiscoor, othercoor);
	return damageDir;
    }

    private void triggerBlock(Damage damage) {
	// turn this unit to block

	Direction damageDir = getIncomingDamageDirection(damage);
	getUnitOwner().getPosProp().getDirFacingProp().setValue(damageDir, this);

	blockReporter.reportIncident(damage);

    }

    public IncidentReporter getOnAttackReporter() {
	return onAttackReporter;
    }

    public IncidentReporter getBlockReporter() {
	return blockReporter;
    }

    private static final double amountChange = 1;
    private static final double decayBase = Math.E;

    private static double blockingAlgorithmOverTurns(Turn lastIncident, boolean didBlock, Turn currentTurn,
	    double normalBlockPercentage) {
	if (lastIncident == null) {
	    return normalBlockPercentage;
	}
	int x = currentTurn.getTurnNumber() - lastIncident.getTurnNumber();

	// percent if it did block
	double currentBlockPercent = 0;
	if (didBlock) {
	    currentBlockPercent = normalBlockPercentage
		    * (-ArmorProperty.amountChange * Math.pow(ArmorProperty.decayBase, -x) + 1);
	} else {
	    currentBlockPercent = ArmorProperty.amountChange * (1 - normalBlockPercentage)
		    * Math.pow(ArmorProperty.decayBase, -x) + normalBlockPercentage;
	}

	return currentBlockPercent;
    }

}
