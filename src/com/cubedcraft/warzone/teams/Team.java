package com.cubedcraft.warzone.teams;

import java.util.List;

import com.cubedcraft.warzone.WarZonePlayer;
import com.google.common.collect.Lists;

public class Team {

	public enum ETeam { RED, BLUE }
	
	ETeam team;
	List<WarZonePlayer> players;
	
	public Team(ETeam team) {
		this.team = team;
		players = Lists.newArrayList();
	}
	
	public List<WarZonePlayer> getPlayers() {
		return players;
	}
	
	public Integer size() {
		return players.size();
	}
	
	public ETeam getTeam() {
		return team;
	}
}