package game.unit.property;

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

    @Override
    protected void propertyChanged(Player oldValue, Player newValue) {
	super.notifyPropertyChanged(oldValue, newValue, !newValue.getTeam().equals(oldValue.getTeam()));
    }
}
