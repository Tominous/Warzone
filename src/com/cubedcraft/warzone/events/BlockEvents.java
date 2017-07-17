package com.cubedcraft.warzone.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import com.cubedcraft.warzone.Config;
import com.cubedcraft.warzone.Main;
import com.cubedcraft.warzone.WarZonePlayer;
import de.inventivegames.hologram.Hologram;
import de.inventivegames.hologram.HologramAPI;

public class BlockEvents
implements Listener {
    @EventHandler
    public void onItemSpawn(PlayerDropItemEvent event) {
        WarZonePlayer wz = Main.getWarZonePlayer(event.getPlayer().getUniqueId());
        if (wz.isObserver()) {
            event.setCancelled(true);
        }
        if (!Main.GameStarted.booleanValue()) {
            event.setCancelled(true);
        }
        if (event.getItemDrop().getItemStack().getType().equals(Material.TNT)) {
            event.setCancelled(true);
        }
        if (event.getItemDrop().getItemStack().getType().equals(Material.REDSTONE_TORCH_ON)) {
            event.setCancelled(true);
        }
        if (event.getItemDrop().getItemStack().getType().equals(Material.REDSTONE_TORCH_OFF)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
	@EventHandler
    public void BlockBreakEvent(BlockBreakEvent ev) {
        WarZonePlayer wz = Main.getWarZonePlayer(ev.getPlayer().getUniqueId());
        if (wz.isObserver() || !Main.GameStarted.booleanValue()) {
            ev.setCancelled(true);
            return;
        }
        int radius = Config.getRadius();
        Block block = ev.getBlock();
        int x = - radius;
        while (x <= radius) {
            int y = - radius;
            while (y <= radius) {
                int z = - radius;
                while (z <= radius) {
                    Block b = block.getRelative(x, y, z);
                    if (this.isSpawnPoint(b.getX(), b.getY(), b.getZ())) {
                        ev.setCancelled(true);
                        return;
                    }
                    if (b.getType() == Material.WOOL && (DyeColor.getByWoolData((byte)b.getData()) == DyeColor.RED || DyeColor.getByWoolData((byte)b.getData()) == DyeColor.BLUE)) {
                        int xp = Config.ExpPerWool();
                        int coins = Config.CoinsPerWool();
                        if (ev.getPlayer().hasPermission("warzone.vip")) {
                            xp *= 2;
                            coins *= 2;
                        }
                        if (DyeColor.getByWoolData((byte)ev.getBlock().getData()) == DyeColor.RED && ev.getBlock().getType().equals(Material.WOOL)) {
                            if (wz.isBlue() && Main.GameStarted.booleanValue()) {
                                Location loc = b.getLocation();
                                Firework firework = (Firework)b.getWorld().spawn(loc, Firework.class);
                                FireworkMeta data = firework.getFireworkMeta();
                                data.addEffects(new FireworkEffect[]{FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).build()});
                                data.setPower(2);
                                firework.setFireworkMeta(data);
                                if (Main.getRedTeamScore() <= 10) {
                                    ev.setCancelled(true);
                                }
                                Bukkit.broadcastMessage((String)ChatColor.translateAlternateColorCodes('&', "&fA &4&lRed Wool&f was destroyed by &9&l" + ev.getPlayer().getName()));
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.getPlugin().getConfig().getString("commands.woolDestroyed")
                                		.replace("%player%", ev.getPlayer().getName()));
                                wz.setExp(wz.getExp() + xp);
                                wz.setCoins(wz.getCoins() + coins);
                                wz.setWool(wz.getWool() + 1);
                                ev.getBlock().setType(Material.AIR);
                                Main.UpdateRedScore();
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    Main.CreateScoreBoard(p);
                                }
                                ev.setCancelled(true);
                                return;
                            }
                        } else if (DyeColor.getByWoolData((byte)ev.getBlock().getData()) == DyeColor.BLUE && ev.getBlock().getType().equals(Material.WOOL) && wz.isRed() && Main.GameStarted.booleanValue()) {
                            Location loc = b.getLocation();
                            Firework firework = (Firework)b.getWorld().spawn(loc, Firework.class);
                            FireworkMeta data = firework.getFireworkMeta();
                            data.addEffects(new FireworkEffect[]{FireworkEffect.builder().withColor(Color.BLUE).with(FireworkEffect.Type.BALL_LARGE).build()});
                            data.setPower(2);
                            firework.setFireworkMeta(data);
                            if (Main.getBlueTeamScore() <= 10) {
                                ev.setCancelled(true);
                            }
                            Bukkit.broadcastMessage((String)ChatColor.translateAlternateColorCodes('&', "&fA &1&lBlue Wool&f was destroyed by &4&l" + ev.getPlayer().getName()));
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.getPlugin().getConfig().getString("commands.woolDestroyed")
                            		.replace("%player%", ev.getPlayer().getName()));
                            wz.setExp(wz.getExp() + xp);
                            wz.setCoins(wz.getCoins() + coins);
                            wz.setWool(wz.getWool() + 1);
                            Main.updateBlueScore();
                            ev.getBlock().setType(Material.AIR);
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                Main.CreateScoreBoard(p);
                            }
                            ev.setCancelled(true);
                            return;
                        }
                        ev.setCancelled(true);
                        ev.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[&c&lWarzone&6&l] &cYou cannot break blocks around the wool!"));
                        return;
                    }
                    ++z;
                }
                ++y;
            }
            ++x;
        }
        Location l = ev.getBlock().getLocation();
        this.SurroundedinTorches(l);
    }

    public void SurroundedinTorches(Location l) {
        ArrayList<Block> listofBlocks = new ArrayList<Block>();
        listofBlocks.add(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
        listofBlocks.add(l.getWorld().getBlockAt(l.getBlockX() + 1, l.getBlockY(), l.getBlockZ()));
        listofBlocks.add(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY() + 1, l.getBlockZ()));
        listofBlocks.add(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 1));
        listofBlocks.add(l.getWorld().getBlockAt(l.getBlockX() - 1, l.getBlockY(), l.getBlockZ()));
        listofBlocks.add(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 1));
        for (Block b : listofBlocks) {
            if (!b.getType().equals((Object)Material.REDSTONE_TORCH_ON) && !b.getType().equals((Object)Material.REDSTONE_TORCH_OFF)) continue;
            for (Map.Entry<UUID, WarZonePlayer> w : Main.getWarZonePlayers().entrySet()) {
                if (w.getValue().getLocation() == null || !w.getValue().getLocation().equals(b.getLocation()) && !w.getValue().getLocation().equals(b.getLocation())) continue;
                if (w.getValue().getHologram().isSpawned()) {
                    w.getValue().getHologram().despawn();
                }
                w.getValue().getLocation().getBlock().setType(Material.AIR);
                w.getValue().setLocation(null);
            }
            b.getDrops().clear();
        }
    }

    public boolean isSpawnPoint(int x, int y, int z) {
        Location bluespawn = Config.getBlueSpawn();
        Location redspawn = Config.getRedSpawn();
        int bluex = (int)bluespawn.getX();
        int bluey = (int)bluespawn.getY();
        int bluez = (int)bluespawn.getZ();
        int redx = (int)redspawn.getX();
        int redy = (int)redspawn.getY();
        int redz = (int)redspawn.getZ();
        if (bluex == x && bluey == y && bluez == z) {
            return true;
        }
        if (redx == x && redy == y && redz == z) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
	@EventHandler
    public void BlockPlace(BlockPlaceEvent ev) {
        WarZonePlayer wz = Main.getWarZonePlayer(ev.getPlayer().getUniqueId());
        if (wz.isObserver() || !Main.GameStarted) {
            ev.setCancelled(true);
            return;
        }
        int radius = Config.getRadius();
        Block block = ev.getBlock();
        int x = - radius;
        while (x <= radius) {
            int y = - radius;
            while (y <= radius) {
                int z = - radius;
                while (z <= radius) {
                    Block b = block.getRelative(x, y, z);
                    if (this.isSpawnPoint(b.getX(), b.getY(), b.getZ())) {
                        ev.setCancelled(true);
                        return;
                    }
                    if (b.getType() == Material.WOOL && (DyeColor.getByWoolData((byte)b.getData()) == DyeColor.RED || DyeColor.getByWoolData((byte)b.getData()) == DyeColor.BLUE)) {
                        ev.setCancelled(true);
                        ev.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[&c&lWarzone&6&l] &cYou cannot place blocks around the wool!"));
                        return;
                    }
                    ++z;
                }
                ++y;
            }
            ++x;
        }
        if (ev.getBlock().getType().equals(Material.TNT)) {
            ev.getPlayer().getInventory().addItem(new ItemStack[]{new ItemStack(Material.TNT, 1)});
            ev.getPlayer().updateInventory();
            ev.setCancelled(true);
            return;
        }
        if (ev.getBlock().getType().equals(Material.REDSTONE_TORCH_ON) || ev.getBlock().getType().equals(Material.REDSTONE_TORCH_OFF)) {
            if (wz.getHologram() != null && wz.getHologram().isSpawned()) {
                wz.getHologram().despawn();
            }
            Location Hologgramelocation = new Location(ev.getBlock().getWorld(), (double)ev.getBlock().getX(), (double)ev.getBlock().getY(), (double)ev.getBlock().getZ());
            Hologram hologram = HologramAPI.createHologram((Location)Hologgramelocation, (String)(ChatColor.AQUA + ev.getPlayer().getName() + "'s spawn point"));
            hologram.spawn();
            if (wz.getLocation() != null && !wz.getLocation().equals(ev.getBlock().getLocation())) {
                wz.getLocation().getBlock().setType(Material.AIR);
            }
            wz.setHologram(hologram);
            wz.setLocation(ev.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent ev) {
        if (ev.getEntity().getType() == EntityType.PRIMED_TNT) {
            List<Block> blockListCopy = new ArrayList<>();
            blockListCopy.addAll(ev.blockList());
            for (Block block : blockListCopy) {
                if (!this.blockBlowUp(block)) continue;
                ev.blockList().remove(block);
            }
        }
    }

    private boolean blockBlowUp(Block b) {
        Material m = b.getType();
        ArrayList<Material> blocks = new ArrayList<Material>();
        blocks.add(Material.ACACIA_STAIRS);
        blocks.add(Material.BIRCH_WOOD_STAIRS);
        blocks.add(Material.BRICK_STAIRS);
        blocks.add(Material.COBBLESTONE_STAIRS);
        blocks.add(Material.DARK_OAK_STAIRS);
        blocks.add(Material.JUNGLE_WOOD_STAIRS);
        blocks.add(Material.NETHER_BRICK_STAIRS);
        blocks.add(Material.PURPUR_STAIRS);
        blocks.add(Material.QUARTZ_STAIRS);
        blocks.add(Material.RED_SANDSTONE_STAIRS);
        blocks.add(Material.SMOOTH_STAIRS);
        blocks.add(Material.SPRUCE_WOOD_STAIRS);
        blocks.add(Material.WOOD_STAIRS);
        blocks.add(Material.GRASS);
        blocks.add(Material.WOOD);
        blocks.add(Material.DOUBLE_STONE_SLAB2);
        blocks.add(Material.PURPUR_DOUBLE_SLAB);
        blocks.add(Material.PURPUR_SLAB);
        blocks.add(Material.STONE_SLAB2);
        blocks.add(Material.LOG);
        blocks.add(Material.LOG_2);
        blocks.add(Material.LEAVES);
        blocks.add(Material.LEAVES_2);
        if (blocks.contains((Object)m)) {
            return false;
        }
        return true;
    }
}

