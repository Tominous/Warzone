package com.cubedcraft.warzone;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommands implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player)sender;
            if (command.getName().equalsIgnoreCase("wardebug")) {
                Main.Debug(p);
                return true;
            }
            if (command.getName().equalsIgnoreCase("endgame")) {
                Main.endGameCount();
                return true;
            }
            if (command.getName().equalsIgnoreCase("rotate")) {
                Main.Rotate();
                return true;
            }
        }
        sender.sendMessage("You must be player to send that command!");
        return false;
    }
}