package com.cubedcraft.warzone.events;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.cubedcraft.warzone.Config;
import com.cubedcraft.warzone.Main;
import com.cubedcraft.warzone.WarZonePlayer;

public class PlayerKill implements Listener {

    @EventHandler(priority=EventPriority.HIGH)
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            WarZonePlayer wz = Main.getWarZonePlayer(event.getEntity().getUniqueId());
            
            if(wz.lastDeathTime + TimeUnit.SECONDS.toMillis(2) > System.currentTimeMillis()) {
            	event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void PlayerDeathEv(PlayerDeathEvent ev) {
        final Player p = ev.getEntity();
        ev.getDrops().clear();
        p.setHealth(20.0);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        WarZonePlayer wz = Main.getWarZonePlayer(p.getUniqueId());
        wz.lastDeathTime = System.currentTimeMillis();
        if (wz.isObserver() || !Main.GameStarted) {
            new BukkitRunnable(){

                public void run() {
                    Main.giveStartItems(p);
                }
                
            }.runTaskLater(Main.getPlugin(), 5);
            p.teleport(Config.getObserverSpawn());
            return;
        }
        WarZonePlayer deathwz = Main.getWarZonePlayer(p.getUniqueId());
        deathwz.setDeaths(deathwz.getDeaths() + 1);
        Main.CreateScoreBoard(p);
        new BukkitRunnable(){

            public void run() {
                Main.startPlayerGame(p);
            }
            
        }.runTaskLater(Main.getPlugin(), 5);
        if (p.getKiller() instanceof Player) {
            WarZonePlayer wzp = Main.getWarZonePlayer(p.getKiller().getUniqueId());
            int xp = Config.ExpPerKill();
            int coins = Config.CoinsPerKill();
            if (p.getKiller().hasPermission("warzone.vip")) {
                xp *= 2;
                coins *= 2;
            }
            wzp.setExp(wzp.getExp() + xp);
            wzp.setCoins(wzp.getCoins() + coins);
            wzp.setKills(wzp.getKills() + 1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.getPlugin().getConfig().getString("commands.playerKilled").replace("%killer%", p.getKiller().getName()).replace("%player%", p.getName()));
            Main.CreateScoreBoard(p.getKiller());
        }
    }

    @EventHandler
    public void entitydamage(EntityDamageByEntityEvent ev) {
        if (ev.getEntity() instanceof Player && ev.getDamager() instanceof Player) {
            Player p = (Player)ev.getDamager();
            WarZonePlayer wz = Main.getWarZonePlayer(p.getUniqueId());
            if (!wz.isBlue() && !wz.isRed()) {
                ev.setCancelled(true);
                return;
            }
        }
    }

}

