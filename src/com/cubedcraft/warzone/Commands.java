package com.cubedcraft.warzone;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor {
	static Logger log = Logger.getLogger("Minecraft.QueuSigns");
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            WarZonePlayer wztoSet;
            Player playertoSet;
            Player p = (Player)sender;
            if (command.getName().equalsIgnoreCase("team")) {
                Main.TeamSelect(p);
                return true;
            }
            if (command.getName().equalsIgnoreCase("kit")) {
                Main.KitSelect(p);
                return true;
            }
            if (!command.getName().equalsIgnoreCase("warzone")) return false;
            if (!sender.hasPermission("warzone.admin")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[&c&lWarzone&6&l] &cYou don't have permission to use that kit!"));
                return true;
            }
            if (args.length != 4) {
                return false;
            }
            if (args[0].equalsIgnoreCase("coins")) {
                if (Bukkit.getPlayer((String)args[2]) != null && args[1] != null && args[3] != null) {
                    playertoSet = Bukkit.getPlayer((String)args[2]);
                    wztoSet = Main.getWarZonePlayer(playertoSet.getUniqueId());
                    if (args[1].equalsIgnoreCase("set")) {
                        try {
                            int num = Integer.parseInt(args[3]);
                            wztoSet.setCoins(num);
                            sender.sendMessage(String.valueOf(playertoSet.getName()) + " coins have been set to: " + wztoSet.getCoins());
                            Mysql.UpdateWarZonePlayer(playertoSet.getUniqueId());
                            Main.CreateScoreBoard(playertoSet);
                            return true;
                        }
                        catch (NumberFormatException e) {
                            sender.sendMessage("You must provide a valid int");
                            return true;
                        }
                    }
                    if (args[1].equalsIgnoreCase("add")) {
                        try {
                            int num = Integer.parseInt(args[3]);
                            wztoSet.setCoins(wztoSet.getCoins() + num);
                            sender.sendMessage(String.valueOf(playertoSet.getName()) + " coins have been set to: " + wztoSet.getCoins());
                            Mysql.UpdateWarZonePlayer(playertoSet.getUniqueId());
                            Main.CreateScoreBoard(playertoSet);
                            return true;
                        }
                        catch (NumberFormatException e) {
                            sender.sendMessage("You must provide a valid int");
                            return true;
                        }
                    }
                    if (args[1].equalsIgnoreCase("remove")) {
                        try {
                            int num = Integer.parseInt(args[3]);
                            wztoSet.setCoins(wztoSet.getCoins() - num);
                            sender.sendMessage(String.valueOf(playertoSet.getName()) + " coins have been set to: " + wztoSet.getCoins());
                            Mysql.UpdateWarZonePlayer(playertoSet.getUniqueId());
                            Main.CreateScoreBoard(playertoSet);
                            return true;
                        }
                        catch (NumberFormatException e) {
                            sender.sendMessage("You must provide a valid int");
                            return true;
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid arugments!");
                    return false;
                }
            }
            if (!args[0].equalsIgnoreCase("exp")) return false;
            if (Bukkit.getPlayer((String)args[2]) != null && args[1] != null && args[3] != null) {
                playertoSet = Bukkit.getPlayer((String)args[2]);
                wztoSet = Main.getWarZonePlayer(playertoSet.getUniqueId());
                if (args[1].equalsIgnoreCase("set")) {
                    try {
                        int num = Integer.parseInt(args[3]);
                        wztoSet.setExp(num);
                        sender.sendMessage(String.valueOf(playertoSet.getName()) + " exp have been set to: " + wztoSet.getExp());
                        Mysql.UpdateWarZonePlayer(playertoSet.getUniqueId());
                        Main.CreateScoreBoard(playertoSet);
                        return true;
                    }
                    catch (NumberFormatException e) {
                        sender.sendMessage("You must provide a valid int");
                        return true;
                    }
                }
                if (args[1].equalsIgnoreCase("add")) {
                    try {
                        int num = Integer.parseInt(args[3]);
                        wztoSet.setExp(wztoSet.getExp() + num);
                        sender.sendMessage(String.valueOf(playertoSet.getName()) + " exp have been set to: " + wztoSet.getCoins());
                        Mysql.UpdateWarZonePlayer(playertoSet.getUniqueId());
                        Main.CreateScoreBoard(playertoSet);
                        return true;
                    }
                    catch (NumberFormatException e) {
                        sender.sendMessage("You must provide a valid int");
                        return true;
                    }
                }
                if (!args[1].equalsIgnoreCase("remove")) return false;
                try {
                    int num = Integer.parseInt(args[3]);
                    wztoSet.setExp(wztoSet.getExp() - num);
                    sender.sendMessage(String.valueOf(playertoSet.getName()) + " exp have been set to: " + wztoSet.getCoins());
                    Mysql.UpdateWarZonePlayer(playertoSet.getUniqueId());
                    Main.CreateScoreBoard(playertoSet);
                    return true;
                }
                catch (NumberFormatException e) {
                    sender.sendMessage("You must provide a valid int");
                    return true;
                }
            }
            sender.sendMessage((Object)ChatColor.RED + "Invalid arugments!");
            return false;
        }
        sender.sendMessage("You must be player to send that command!");
        return false;
    }
}

