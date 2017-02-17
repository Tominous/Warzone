package com.cubedcraft.warzone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class StartCountdown {

    private int countdownTimer;
 
    public void start(final int time, final String msg) {
        this.countdownTimer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
            int i = time;
 
            public void run() {
                Bukkit.broadcastMessage(ChatColor.GREEN + "Game " + msg + " in " + i + " seconds");
                this.i--;
                if (this.i <= 0) {
                    StartCountdown.this.cancel();
                }
            }
        }
        , 0L, 20L);
    }
 
    public void cancel() {
        Bukkit.getScheduler().cancelTask(this.countdownTimer);
    }
}