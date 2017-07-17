package com.cubedcraft.warzone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class StartCountdown {

    private int countdownTimer;
 
    public void start(int time, String msg) {
        this.countdownTimer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
            int i = time;
 
            public void run() {
                Bukkit.broadcastMessage(ChatColor.GREEN + "Game " + msg + " in " + i + " seconds");
                i--;
                if (i <= 0) {
                    cancel();
                }
            }
        }
        , 0L, 20L);
    }
 
    public void cancel() {
        Bukkit.getScheduler().cancelTask(countdownTimer);
    }
}