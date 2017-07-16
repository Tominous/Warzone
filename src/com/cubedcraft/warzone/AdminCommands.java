package com.cubedcraft.warzone;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;


public class AdminCommands implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (!sender.hasPermission("warzone.admin")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[&c&lWarzone&6&l] &cYou don't have permission to use this command"));
                return true;
            }
        	Player p = (Player)sender;
            if (command.getName().equalsIgnoreCase("next")) {
                Main.endGameCount();
                return true;
            }
            if (command.getName().equalsIgnoreCase("wardebug")) {
                Main.Debug(p);
                return true;
            }
        } else {
        sender.sendMessage("You must be player to send that command!");
        return false;
        }
		return false;
    }
}