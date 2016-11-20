package game.unit.properties;

import game.Player;
import game.Team;
import game.unit.Unit;

public class OwnerProperty extends Property<Player> {

	public OwnerProperty(Unit unit, Player player) {
		super(unit, player);
	}

	public Player getPlayer() {
		return property;
	}

	public Team getTeam() {
		return property.getTeam();
	}

	public void setOwner(Player newplayer) {
		if (property.equals(newplayer))
			return;
		Player oldPlayer = this.property;
		this.property = newplayer;
		super.propertyChanged(oldPlayer, this.property);
	}
}
