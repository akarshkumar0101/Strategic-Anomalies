package game.unit.property;

import game.Player;
import game.Team;
import game.unit.Unit;

public class OwnerProperty extends Property<Player> {

    public OwnerProperty(Unit unit, Player player) {
	super(unit, player);
    }

    public Team getTeam() {
	return getValue().getTeam();
    }
}
