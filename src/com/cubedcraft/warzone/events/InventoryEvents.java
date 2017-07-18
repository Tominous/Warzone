package com.cubedcraft.warzone.events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.cubedcraft.warzone.Config;
import com.cubedcraft.warzone.Main;
import com.cubedcraft.warzone.WarZonePlayer;
import com.cubedcraft.warzone.teams.Team.ETeam;

public class InventoryEvents implements Listener {
	
    List<UUID> TntCooldowns = new ArrayList<UUID>();

    @EventHandler
    public void PlayerClickEvent(InventoryClickEvent ev) {
        if (ev.getWhoClicked() instanceof Player) {
            Player player = (Player)ev.getWhoClicked();
            WarZonePlayer wz = Main.getWarZonePlayer(player.getUniqueId());
            ItemStack clicked = ev.getCurrentItem();
            Inventory inventory = ev.getInventory();
            int InvetorySlot = ev.getSlot();
            if (inventory.getName().equalsIgnoreCase(ChatColor.RED + "Team Selection")) {
                if (clicked == null) {
                    ev.setCancelled(true);
                    return;
                }
                if (clicked.getType().equals(Material.BOOK)) {
                	if (wz.isObserver()) {
	                    if (Main.searchTeam().getTeam().equals(ETeam.BLUE)) {
	                        wz.setBlue();
	                        player.setPlayerListName(String.valueOf(Config.getPrefix(player)) + " " + ChatColor.BLUE + player.getName());
	                        player.sendMessage(ChatColor.BLUE + "You have been added to the blue team");
	                        if (Main.GameStarted) {
	                            Main.startPlayerGame(player);
	                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', player.getName() + " joined &9BLUE&f team"));
	                        }
	                    } else {
	                        wz.setRed();
	                        player.setPlayerListName(String.valueOf(Config.getPrefix(player)) + " " + ChatColor.RED + player.getName());
	                        player.sendMessage(ChatColor.RED + "You have been added to the red team");
	                        if (Main.GameStarted) {
	                            Main.startPlayerGame(player);
	                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', player.getName() + " joined &4RED&f team"));
	                        }
	                    }
                	} else {
                		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[&c&lWarzone&6&l] &cYou have already selected a team"));
                	}
                    ev.setCancelled(true);
                    player.closeInventory();
                }
                if (clicked.getType().equals(Material.WOOL) && clicked.getDurability() == 11) {
                    if (player.hasPermission("warzone.teamselect")) {
                        ev.setCancelled(true);
                        player.closeInventory();
                        wz.setBlue();
                        player.setPlayerListName(String.valueOf(Config.getPrefix(player)) + " " + ChatColor.BLUE + player.getName());
                        player.sendMessage(ChatColor.BLUE + "You have been added to the blue team");
                        if (Main.GameStarted) {
                            Main.startPlayerGame(player);
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', player.getName() + " joined &9BLUE&f team"));
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[&c&lWarzone&6&l] &cOnly donators can choose their team"));
                    }
                    ev.setCancelled(true);
                }
                if (clicked.getType().equals(Material.WOOL) && clicked.getDurability() == 14) {
                    if (player.hasPermission("warzone.teamselect")) {
                        ev.setCancelled(true);
                        player.closeInventory();
                        wz.setRed();
                        player.setPlayerListName(String.valueOf(Config.getPrefix(player)) + " " + ChatColor.RED + player.getName());
                        player.sendMessage(ChatColor.RED + "You have been added to the red team");
                        if (Main.GameStarted) {
                            Main.startPlayerGame(player);
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', player.getName() + " joined &4RED&f team"));
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[&c&lWarzone&6&l] &cOnly donators can choose their team"));
                    }
                    ev.setCancelled(true);
                }
                if (Main.GameStarted) {
                    Main.startPlayerGame(player);
                }
            }
            if (inventory.getName().equalsIgnoreCase(ChatColor.RED + "Kit")) {
                if (Config.getKitName(InvetorySlot) == null) {
                    ev.setCancelled(true);
                    return;
                }
                if (Config.HasPermissionForKit(InvetorySlot, player)) {
                    String KitName = Config.getKitName(InvetorySlot);
                    if (wz.hasKit(KitName)) {
                        wz.setCurrentKit(InvetorySlot);
                        ev.setCancelled(true);
                        player.closeInventory();
                        player.sendMessage(ChatColor.GREEN + "You selected " + KitName);
                    } else if (wz.getCoins() >= Config.getKitCost(InvetorySlot)) {
                        wz.giveKit(KitName);
                        wz.setCoins(wz.getCoins() - Config.getKitCost(InvetorySlot));
                        wz.setCurrentKit(InvetorySlot);
                        ev.setCancelled(true);
                        player.closeInventory();
                        player.sendMessage(ChatColor.GREEN + "You selected " + KitName);
                        player.sendMessage(ChatColor.GREEN + "You now have " + wz.getCoins() + " coins left");
                    } else {
                        player.sendMessage(ChatColor.RED + "You can't afford " + KitName + "!");
                        ev.setCancelled(true);
                        return;
                    }
                    ev.setCancelled(true);
                    return;
                }
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[&c&lWarzone&6&l] &cYou don't have permission to use that kit!"));
                ev.setCancelled(true);
                player.closeInventory();
                return;
            }
        }
    }

    @SuppressWarnings("deprecation")
	@EventHandler
    public void PlayerInteractEvent(final PlayerInteractEvent ev) {
        Player p = ev.getPlayer();
        WarZonePlayer wz = Main.getWarZonePlayer(p.getUniqueId());
        if (ev.getAction().equals(Action.RIGHT_CLICK_AIR) || ev.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (p.getInventory().getItemInMainHand().getType().equals(Material.SIGN)) {
                Main.TeamSelect(p);
                ev.setCancelled(true);
                return;
            }
            if (p.getInventory().getItemInMainHand().getType().equals(Material.BOOK)) {
                Main.KitSelect(p);
                ev.setCancelled(true);
                return;
            }
            if (p.getInventory().getItemInHand().getType().equals(Material.TNT) && Main.GameStarted.booleanValue() && (wz.isBlue() || wz.isRed())) {
                if (this.TntCooldowns.contains(ev.getPlayer().getUniqueId())) {
                    ev.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[&c&lWarzone&6&l] &cYou have to wait 15 sec before you can use tnt again!"));
                    ev.setCancelled(true);
                    return;
                }
                TNTPrimed tnt = (TNTPrimed) p.getLocation().getWorld().spawn(p.getLocation(), TNTPrimed.class);
                Vector direction = ev.getPlayer().getLocation().getDirection().multiply(1.2);
                tnt.setFuseTicks(100);
                direction.setY(direction.getY() + 0.5);
                tnt.setVelocity(direction);
                ev.setCancelled(true);
                this.TntCooldowns.add(ev.getPlayer().getUniqueId());
                new BukkitRunnable(){

                    public void run() {
                        InventoryEvents.this.TntCooldowns.remove(ev.getPlayer().getUniqueId());
                    }
                }.runTaskLater(Main.getPlugin(), 300);
            }
        }
        if (!wz.isRed() && !wz.isBlue()) {
            ev.setCancelled(true);
            return;
        }
    }
}