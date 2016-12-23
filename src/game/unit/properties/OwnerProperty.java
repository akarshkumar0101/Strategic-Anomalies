package game.unit.properties;

import game.Player;
import game.Team;
import game.unit.Unit;

public class OwnerProperty extends Property<Player> {

    public OwnerProperty(Unit unit, Player player) {
	super(unit, player);
    }

    public Team getTeam() {
	return getCurrentPropertyValue().getTeam();
    }

    /**
     * This will call the propertyChanged() method with the following arguments:
     * Player oldPropertyValue, Player newPropertyValue, boolean on whether or
     * not the team changed.
     */
    @Override
    protected void propertyChanged(Player oldValue, Player newValue, Object... specifications) {
	super.propertyChanged(oldValue, newValue, !newValue.getTeam().equals(oldValue.getTeam()));
    }
}
