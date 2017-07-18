package com.cubedcraft.warzone;

import org.bukkit.Location;

import com.cubedcraft.warzone.teams.Team.ETeam;

import de.inventivegames.hologram.Hologram;

public class WarZonePlayer {
	
    private int coins;
    private int kills;
    private int deaths;
    private int wins;
    private int wool;
    private int exp;
    private boolean isObserver;
    private String Kits;
    private String uuid;
    private String MinecraftName;
    private Hologram Hologram;
    private Location SpawnPoint;
    int CurrentKit;
    ETeam team;
    public Long lastDeathTime = 0L;

    public WarZonePlayer(String uuid, int coins, int kills, int deaths, int wins, int wool, int exp, String kits, String MinecraftUserName) {
        this.setUuid(uuid);
        this.coins = coins;
        this.kills = kills;
        this.deaths = deaths;
        this.wins = wins;
        this.exp = exp;
        this.Kits = kits;
        this.isObserver = true;
        this.MinecraftName = MinecraftUserName;
        this.CurrentKit = Config.getDefaultKit();
        this.wool = wool;
    }

    public int getCurrentKit() {
        return this.CurrentKit;
    }

    public void setCurrentKit(int currentKit) {
        this.CurrentKit = currentKit;
    }

    public String getMinecraftName() {
        return this.MinecraftName;
    }

    public void setMinecraftName(String minecraftName) {
        this.MinecraftName = minecraftName;
    }

    public int getWool() {
        return this.wool;
    }

    public void setWool(int wool) {
        this.wool = wool;
    }

    public int getCoins() {
        return this.coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getKills() {
        return this.kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getWins() {
        return this.wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getExp() {
        return this.exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public boolean isBlue() {
        return team != null && team.equals(ETeam.BLUE);
    }

    public void setBlue() {
        this.team = ETeam.BLUE;
        Main.teams.get(team).getPlayers().add(this);
        this.isObserver = false;
    }

    public boolean isRed() {
        return team != null && team.equals(ETeam.RED);
    }

    public void setRed() {
        this.team = ETeam.RED;
        Main.teams.get(team).getPlayers().add(this);
        this.isObserver = false;
    }

    public boolean isObserver() {
        return this.isObserver;
    }

    public void setObserver() {
        this.isObserver = true;
        this.team = null;
    }

    public String getKits() {
        return this.Kits;
    }

    public boolean hasKit(String kitName) {
        String[] arrstring = this.getKits().split("%%");
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String s = arrstring[n2];
            if (s.equalsIgnoreCase(kitName)) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public void giveKit(String kit) {
        String s = String.valueOf(this.getKits()) + "%%" + kit;
        this.setKits(s);
    }

    public void setKits(String kits) {
        this.Kits = kits;
    }

    public Hologram getHologram() {
        return this.Hologram;
    }

    public void setHologram(Hologram hologram) {
        this.Hologram = hologram;
    }

    public Location getLocation() {
        return this.SpawnPoint;
    }

    public void setLocation(Location location) {
        this.SpawnPoint = location;
    }

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}

