package com.cubedcraft.warzone;

import java.util.Arrays;

import org.bukkit.entity.Player;

import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;

public class LeaderHeadsIntegration {
	public LeaderHeadsIntegration() {
		new OnlineDataCollector("warzone-kills", "Warzone", BoardType.DEFAULT, "&bKills", "openkills", Arrays.asList(null, null, "&e{amount} kills", null)) {
			@Override
			public Double getScore(Player player) {
				WarZonePlayer wzp = Main.WarZonePlayers.get(player.getUniqueId());
				if(wzp == null) return null;
				return (double) wzp.getKills();
			}
		};
		new OnlineDataCollector("warzone-deaths", "Warzone", BoardType.DEFAULT, "&bDeaths", "opendeaths", Arrays.asList(null, null, "&e{amount} deaths", null)) {
			@Override
			public Double getScore(Player player) {
				WarZonePlayer wzp = Main.WarZonePlayers.get(player.getUniqueId());
				if(wzp == null) return null;
				return (double) wzp.getDeaths();
			}
		};
		new OnlineDataCollector("warzone-coins", "Warzone", BoardType.DEFAULT, "&bCoins", "opencoins", Arrays.asList(null, null, "&e{amount} coins", null)) {
			@Override
			public Double getScore(Player player) {
				WarZonePlayer wzp = Main.WarZonePlayers.get(player.getUniqueId());
				if(wzp == null) return null;
				return (double) wzp.getCoins();
			}
		};
		new OnlineDataCollector("warzone-wins", "Warzone", BoardType.DEFAULT, "&bWins", "openwins", Arrays.asList(null, null, "&e{amount} wins", null)) {
			@Override
			public Double getScore(Player player) {
				WarZonePlayer wzp = Main.WarZonePlayers.get(player.getUniqueId());
				if(wzp == null) return null;
				return (double) wzp.getWins();
			}
		};
		new OnlineDataCollector("warzone-wool", "Warzone", BoardType.DEFAULT, "&bWool", "openwool", Arrays.asList(null, null, "&e{amount} wool", null)) {
			@Override
			public Double getScore(Player player) {
				WarZonePlayer wzp = Main.WarZonePlayers.get(player.getUniqueId());
				if(wzp == null) return null;
				return (double) wzp.getWool();
			}
		};
		new OnlineDataCollector("warzone-exp", "Warzone", BoardType.DEFAULT, "&bExp", "openexp", Arrays.asList(null, null, "&e{amount} exp", null)) {
			@Override
			public Double getScore(Player player) {
				WarZonePlayer wzp = Main.WarZonePlayers.get(player.getUniqueId());
				if(wzp == null) return null;
				return (double) wzp.getExp();
			}
		};
	}
}