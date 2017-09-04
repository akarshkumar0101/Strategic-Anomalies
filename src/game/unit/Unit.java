package game.unit;

import game.Game;
import game.Player;
import game.board.Coordinate;
import game.board.Direction;
import game.board.Path;
import game.board.PathFinder;
import game.board.Square;
import game.interaction.effect.Affectable;
import game.interaction.incident.IncidentListener;
import game.interaction.incident.IncidentReporter;
import game.unit.listofunits.Aquamancer;
import game.unit.listofunits.Archer;
import game.unit.listofunits.Assassin;
import game.unit.listofunits.BarrierWard;
import game.unit.listofunits.Basilisk;
import game.unit.listofunits.Beasty;
import game.unit.listofunits.Berserker;
import game.unit.listofunits.BronzeGolem;
import game.unit.listofunits.Chanty;
import game.unit.listofunits.ClayGolem;
import game.unit.listofunits.Cleric;
import game.unit.listofunits.DarkMagicWitch;
import game.unit.listofunits.DragonTyrant;
import game.unit.listofunits.EagleTyrant;
import game.unit.listofunits.Furgon;
import game.unit.listofunits.GiantMole;
import game.unit.listofunits.GolemAmbusher;
import game.unit.listofunits.Guardian;
import game.unit.listofunits.Hunter;
import game.unit.listofunits.LavaGolem;
import game.unit.listofunits.LightMagicWitch;
import game.unit.listofunits.LightningWard;
import game.unit.listofunits.Lightningmancer;
import game.unit.listofunits.Monk;
import game.unit.listofunits.MudGolem;
import game.unit.listofunits.Poise;
import game.unit.listofunits.Pyromancer;
import game.unit.listofunits.RockBeast;
import game.unit.listofunits.Sage;
import game.unit.listofunits.SandGolem;
import game.unit.listofunits.Scout;
import game.unit.listofunits.Siren;
import game.unit.listofunits.StoneGolem;
import game.unit.listofunits.TurtleTyrant;
import game.unit.listofunits.VineWard;
import game.unit.listofunits.Warrior;
import game.unit.property.HealthProperty;
import game.unit.property.MovingProperty;
import game.unit.property.OwnerProperty;
import game.unit.property.PositionProperty;
import game.unit.property.StunnedProperty;
import game.unit.property.WaitProperty;
import game.unit.property.ability.Ability;
import game.unit.property.ability.ActiveAbility;
import game.unit.property.ability.ActiveTargetAbility;

//36
public abstract class Unit extends Affectable {

    // TODO go through and document EVERYTHING
    // TODO go through and determine visibility of ALL members in every class.
    @SuppressWarnings("unchecked")
    public static final Class<? extends Unit>[] UNITCLASSES = (Class<? extends Unit>[]) new Class<?>[] { Warrior.class,
	    Guardian.class, Pyromancer.class, Aquamancer.class, Lightningmancer.class, Scout.class, Archer.class,
	    Hunter.class, DarkMagicWitch.class, LightMagicWitch.class, Cleric.class, Sage.class, Monk.class,
	    Assassin.class, Berserker.class, Siren.class, Chanty.class, BarrierWard.class, LightningWard.class,
	    VineWard.class, Basilisk.class, RockBeast.class, Beasty.class, Furgon.class, GiantMole.class, Poise.class,
	    DragonTyrant.class, TurtleTyrant.class, EagleTyrant.class, MudGolem.class, GolemAmbusher.class,
	    SandGolem.class, LavaGolem.class, StoneGolem.class, BronzeGolem.class, ClayGolem.class };

    private final Game game;

    private final UnitStat defaultStat;

    private final OwnerProperty ownerProp;
    private final PositionProperty posProp;
    private final HealthProperty healthProp;
    private final MovingProperty movingProp;
    private final StunnedProperty stunnedProp;

    private final Ability ability;

    private final IncidentReporter deathReporter;

    public Unit(Game game, Player playerOwner, Direction directionFacing, Coordinate coor) {
	super();
	this.game = game;

	defaultStat = UnitDefaults.getStat(this.getClass());

	ownerProp = new OwnerProperty(this, playerOwner);
	posProp = new PositionProperty(this, coor, directionFacing);
	healthProp = new HealthProperty(this, defaultStat.defaultHealth, defaultStat.defaultArmor,
		1 - defaultStat.defaultFrontBlock, 1 - defaultStat.defaultSideBlock);
	movingProp = new MovingProperty(this, defaultStat.defaultMoveRange, defaultStat.canDefaultTeleport);
	stunnedProp = new StunnedProperty(this, false);

	deathReporter = new IncidentReporter() {
	    @Override
	    public void add(IncidentListener listener, boolean onlyOnce) {
		super.add(listener, true);
	    }
	};

	ability = getDefaultAbility();

	// TODO add the inital wait time if going first etc.
    }

    public Game getGame() {
	return game;
    }

    protected abstract Ability getDefaultAbility();

    public OwnerProperty getOwnerProp() {
	return ownerProp;
    }

    public PositionProperty getPosProp() {
	return posProp;
    }

    public HealthProperty getHealthProp() {
	return healthProp;
    }

    public MovingProperty getMovingProp() {
	return movingProp;
    }

    public Ability getAbility() {
	return ability;
    }

    public StunnedProperty getStunnedProp() {
	return stunnedProp;
    }

    public WaitProperty getWaitProp() {
	return ability instanceof ActiveAbility ? ((ActiveAbility) ability).getWaitProp() : null;
    }

    public IncidentReporter getDeathReporter() {
	return deathReporter;
    }

    public UnitStat getDefaultStat() {
	return defaultStat;
    }

    public void triggerDeath() {
	deathReporter.reportIncident(this);
    }

    public void moveTakePath(Path path, Object sourceOfMovement) {
	Direction newdirFacing = null;
	try {
	    newdirFacing = Coordinate.inGeneralDirection(path.getPreviousPath().getEndCoor(), path.getEndCoor());
	} catch (Exception e) {
	    newdirFacing = Coordinate.inGeneralDirection(getPosProp().getValue(), path.getEndCoor());
	}
	getPosProp().setValue(path.getEndCoor(), sourceOfMovement);
	getPosProp().getDirFacingProp().setValue(newdirFacing, path);
    }

    public void useAbility(Square sqr) {
	((ActiveTargetAbility) getAbility()).useAbility(sqr);
	Direction newdirFacing = Coordinate.inGeneralDirection(getPosProp().getValue(), sqr.getCoor());
	if (newdirFacing == null) {
	    newdirFacing = getPosProp().getDirFacingProp().getValue();
	}
	getPosProp().getDirFacingProp().setValue(newdirFacing, getAbility());
    }

    // TODO make sure all units should consider overriding these methods
    public Path getGamePathTo(Coordinate moveToCoor) {
	if (!movingProp.getCanMoveProp().getValue() || !movingProp.isInRangeOfWalking(moveToCoor)) {
	    return null;
	} else if (movingProp.getTeleportingProp().getValue()) {
	    return PathFinder.getTeleportedPath(this, moveToCoor);
	} else {
	    return PathFinder.getPath(this, moveToCoor);
	}
    }

    public static boolean areAllies(Unit unit1, Unit unit2) {
	if (unit1 == null || unit2 == null) {
	    return false;
	}
	return unit1.ownerProp.getTeam().equals(unit2.ownerProp.getTeam());
    }
}

// class UnitProperty {
// public final Property<Player> ownerProp;
//
// public final Property<Coordinate> posProp;
// public final Property<Direction> dirFacingProp;
//
// public final Property<Integer> healthProp;
// public final Property<Integer> maxHealthProp;
// public final Property<Integer> armorProp;
//
// public final Property<Boolean> canMoveProp;
// public final Property<Integer> movingProp;
// public final Property<Boolean> teleportingProp;
// // stoic true means they do not move aside
// public final Property<Boolean> stoicProp;
//
// public final Property<Boolean> stunnedProp;
//
// public final Ability ability;
//
// public final IncidentReporter deathReporter;
//
// public UnitProperty(Unit unit, Player playerOwner, Coordinate coor, Direction
// dirFacing) {
// ownerProp = new Property<>(unit, playerOwner);
//
// posProp = new Property<>(unit, coor);
// dirFacingProp = new Property<>(unit, dirFacing);
//
// healthProp = new Property<>(unit, unit.getDefaultStat().defaultHealth);
// maxHealthProp = new Property<>(unit, unit.getDefaultStat().defaultHealth);
// armorProp = new Property<>(unit, unit.getDefaultStat().defaultArmor);
//
// canMoveProp = new Property<>(unit, unit.getDefaultStat().canDefaultMove);
// movingProp = new Property<>(unit, unit.getDefaultStat().defaultMoveRange);
// teleportingProp = new Property<>(unit,
// unit.getDefaultStat().canDefaultTeleport);
// stoicProp = new Property<>(unit, unit.getDefaultStat().isDefaultStoic);
//
// stunnedProp = new Property<>(unit, false);
//
// ability = unit.getDefaultAbility();
//
// deathReporter = new IncidentReporter() {
// @Override
// public void add(IncidentListener listener, boolean onlyOnce) {
// super.add(listener, true);
// }
// };
//
// // TODO add the inital wait time if going first etc.
// }
// }