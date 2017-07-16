package com.cubedcraft.warzone.Events;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.cubedcraft.warzone.Config;
import com.cubedcraft.warzone.Main;
import com.cubedcraft.warzone.Mysql;
import com.cubedcraft.warzone.WarZonePlayer;

public class PlayerJoin
implements Listener {
    @EventHandler
    public void PlayerJoinEv(PlayerJoinEvent ev) {
        WarZonePlayer Warplayer = Mysql.getWarZonePlayer(ev.getPlayer().getUniqueId());
        Warplayer.setObserver();
        final Player p = ev.getPlayer();
        p.setTotalExperience(0);
        p.setLevel(0);
        p.setExp(0.0f);
        p.setHealth(20.0);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setDisplayName(String.valueOf(Config.getPrefix(p)) + " " + ChatColor.WHITE + p.getName());
        Main.addWarZonePlayer(p.getUniqueId(), Warplayer);
        Main.giveStartItems(p);
        Main.CreateScoreBoard(p);
        p.setGameMode(GameMode.SURVIVAL);
        p.setPlayerListName(String.valueOf(Config.getPrefix(p)) + " " + ChatColor.AQUA + p.getName());
        Main.Fixinvis();
        new BukkitRunnable(){

            public void run() {
                p.teleport(Config.getObserverSpawn());
                Main.TeamSelect(p);
            }
        }.runTaskLater(Main.getPlugin(), 5);
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent ev) {
        Mysql.UpdateWarZonePlayer(ev.getPlayer().getUniqueId());
        Main.removeWarZonePlayer(ev.getPlayer().getUniqueId());
    }

}

