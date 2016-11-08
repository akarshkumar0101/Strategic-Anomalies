package game;

import java.util.ArrayList;
import java.util.List;

public class Team {
	
	private final List<Player> players;
	
	public Team(Player...playersarr){
		players = new ArrayList<>(playersarr.length);
		for(Player player: playersarr){
			players.add(player);
		}
	}
	public Team(List<Player> players){
		this.players =players;
	}
	
	public List<Player> getPlayers(){
		return players;
	}
}
