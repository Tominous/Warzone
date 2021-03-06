package com.cubedcraft.warzone;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import com.cubedcraft.warzone.events.BlockEvents;
import com.cubedcraft.warzone.events.InventoryEvents;
import com.cubedcraft.warzone.events.PlayerJoin;
import com.cubedcraft.warzone.events.PlayerKill;
import com.cubedcraft.warzone.teams.Team.ETeam;
import com.google.common.collect.Maps;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;

public class Main extends JavaPlugin implements Listener {
	
    static Logger log = Logger.getLogger("Minecraft.QueuSigns");
    static int RedTeamScore = 100;
    static int BlueTeamScore = 100;
    private static List<String> worlds;
    private static int activeworldint;
    public static String currentWorldName;
    private static boolean gamestartingwithtimer;
    private static Main plugin;
    private static boolean GameEndedDuetoWoolDestroyed;
    public static Boolean GameStarted;
    public static String ActiveWorld;
    public static String NextWorld;
    static HashMap<UUID, WarZonePlayer> WarZonePlayers;
    private static StartCountdown cd;
    static Scoreboard board;
    static int time;
    String character_heart;
    private static TitleManagerAPI titleManagerAPI;
    public static Map<ETeam, com.cubedcraft.warzone.teams.Team> teams = Maps.newHashMap();
    
    static {
        activeworldint = -1;
        gamestartingwithtimer = false;
        GameEndedDuetoWoolDestroyed = false;
        GameStarted = false;
        ActiveWorld = "WarzoneWorld";
        WarZonePlayers = new HashMap<>();
        time = 0;
    }
    
    public static com.cubedcraft.warzone.teams.Team searchTeam() {
    	com.cubedcraft.warzone.teams.Team team = null;
    	for(com.cubedcraft.warzone.teams.Team t : teams.values()) {
    		if(team == null) {
    			team = t;
    			continue;
    		}
    		if(t.size() < team.size()) {
    			team = t;
    		}
    	}
    	return team;
    }
    
    public static HashMap<UUID, WarZonePlayer> getWarZonePlayers() {
        return WarZonePlayers;
    }

    public static String getCurrentWorldName() {
        return currentWorldName;
    }

    public void onEnable() {
        plugin = this;
        titleManagerAPI = (TitleManagerAPI) getServer().getPluginManager().getPlugin("TitleManager");
        cd = new StartCountdown();
        ActiveWorld = "WarzoneWorld";
        if (Config.getWorldName() == null) {
            saveDefaultConfig();
        }
        worlds = Config.getWorlds();
        Bukkit.getServer().unloadWorld("WarzoneWorld", false);
        Bukkit.getServer().unloadWorld("WarzoneWorld2", false);
        Mysql.CheckConnection();
        this.getServer().getPluginManager().registerEvents(new PlayerKill(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryEvents(), this);
        this.getServer().getPluginManager().registerEvents(new BlockEvents(), this);
        this.getCommand("team").setExecutor(new Commands());
        this.getCommand("kit").setExecutor(new Commands());
        this.getCommand("warzone").setExecutor(new Commands());
        this.getCommand("next").setExecutor(new AdminCommands());
        this.getCommand("wardebug").setExecutor(new AdminCommands());
        this.loadWorlds();
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.teleport(Bukkit.getWorld(ActiveWorld).getSpawnLocation());
            if (WarZonePlayers.containsKey(p.getUniqueId())) {
            	continue;
            }
            Main.addWarZonePlayer(p.getUniqueId(), Mysql.getWarZonePlayer(p.getUniqueId()));
        }
        this.runTimer();
        this.character_heart = StringEscapeUtils.unescapeJava((String)"\u2764");
        if(Bukkit.getPluginManager().getPlugin("LeaderHeads") != null) {
        	new LeaderHeadsIntegration();
        }
        
        for(ETeam team : ETeam.values()) {
        	teams.put(team, new com.cubedcraft.warzone.teams.Team(team));
        }
    }

    private void loadWorlds() {
        activeworldint = activeworldint < worlds.size() - 1 && activeworldint != -1 ? ++activeworldint : 0;
        currentWorldName = Main.NextWorld = worlds.get(activeworldint);
        File backup = new File(Bukkit.getWorldContainer() + "/WarzoneWorld");
        File backup2 = new File(Bukkit.getWorldContainer() + "/WarzoneWorld2");
        try {
            if (backup.exists()) {
                FileUtils.deleteDirectory((File)backup);
            }
            if (backup2.exists()) {
                FileUtils.deleteDirectory((File)backup2);
            }
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        File toDelete = new File(Bukkit.getWorldContainer() + "/WarzoneWorld/uid.dat");
        File toDelete2 = new File(Bukkit.getWorldContainer() + "/WarzoneWorld2/uid.dat");
        log.info(worlds.toString());
        log.info(worlds.get(activeworldint));
        try {
            FileUtils.copyDirectory((File)new File(Bukkit.getWorldContainer() + "/" + worlds.get(activeworldint)), (File)backup);
            activeworldint = activeworldint < worlds.size() - 1 ? ++activeworldint : 0;
            FileUtils.copyDirectory((File)new File(Bukkit.getWorldContainer() + "/" + worlds.get(activeworldint)), (File)backup2);
            if (toDelete.exists()) {
                toDelete.delete();
            }
            if (toDelete2.exists()) {
                toDelete2.delete();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.getServer().createWorld(new WorldCreator("WarzoneWorld"));
        Bukkit.getServer().getWorld("WarzoneWorld").setAutoSave(false);
        Bukkit.getServer().createWorld(new WorldCreator("WarzoneWorld2"));
        Bukkit.getServer().getWorld("WarzoneWorld2").setAutoSave(false);
    }

    public void runTimer() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable(){

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Override
            public void run() {
                if (++time >= Config.getGameLength() * 60) {
                    if (GameStarted) {
                        endGameCount();
                        return;
                    }
                    time = 0;
                    return;
                }
                if (!isEnoughPlayers()) {
                    if (!GameStarted.booleanValue()) {
                        if (time % 101 != 0) return;
                        time = 0;
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[&c&lWarzone&6&l] &cNot enough players in each team"));
                        return;
                    }
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&l[&c&lWarzone&6&l] &cGame ended due to not enough players"));
                    access$0(true);
                    endGameCount();
                    time = 0;
                    GameStarted = false;
                    return;
                }
                if (Main.GameStarted.booleanValue() || gamestartingwithtimer) {
                	return;
                }
                StartGame();
                access$2(true);
            }
        }, 20, 20);
    }

    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Mysql.UpdateWarZonePlayer(p.getUniqueId());
        }
        WarZonePlayers.clear();
    }

    public static void giveStartItems(Player p) {
        p.getInventory().clear();
        ItemStack TeamSelect = new ItemStack(Material.SIGN);
        ItemMeta TeamSelectim = TeamSelect.getItemMeta();
        TeamSelectim.setDisplayName(ChatColor.GREEN + "Team Selection");
        TeamSelect.setItemMeta(TeamSelectim);
        ItemStack KitSelect = new ItemStack(Material.BOOK);
        ItemMeta KitSelectim = KitSelect.getItemMeta();
        KitSelectim.setDisplayName(ChatColor.GREEN + "Kit Selection");
        KitSelect.setItemMeta(KitSelectim);
        p.getInventory().addItem(new ItemStack[]{TeamSelect});
        p.getInventory().addItem(new ItemStack[]{KitSelect});
    }

    public static void StartGame() {
        plugin.getServer().broadcastMessage(ChatColor.GREEN + "Game starts in 30 seconds");
        new BukkitRunnable(){

            public void run() {
                cd.start(10, "starting");
                new BukkitRunnable(){

                    public void run() {
                        Main.time = 0;
                        Main.log.info("Debug 1: Game started? " + Main.GameStarted);
                        if (Main.GameStarted.booleanValue()) {
                            return;
                        }
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            WarZonePlayer wz = Main.getWarZonePlayer(p.getUniqueId());
                            if (wz.getLocation() != null) {
                                p.teleport(wz.getLocation());
                                wz.getLocation().getBlock().setType(Material.AIR);
                                wz.setLocation(null);
                                if (wz.getHologram() != null && wz.getHologram().isSpawned()) {
                                    wz.getHologram().despawn();
                                    wz.setHologram(null);
                                }
                            }
                            Main.startPlayerGame(p);
                        }
                        Main.GameStarted = true;
                        plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&aGame started"));
                        Main.log.info("game length:" + Config.getGameLength());
                    }
                }.runTaskLater(plugin, 200);
            }

        }.runTaskLater(plugin, 400);
    }

    public static void startPlayerGame(Player p) {
        WarZonePlayer wz = Main.getWarZonePlayer(p.getUniqueId());
        if (wz.isBlue() || wz.isRed()) {
            log.info("Debug 2:" + p.getName());
            p.setGameMode(GameMode.SURVIVAL);
            p.getInventory().clear();
            p.setHealth(20.0);
            p.setFoodLevel(20);
            Config.getArmour(wz.getCurrentKit(), wz, p);
            for (ItemStack i : Config.getItem(wz.getCurrentKit())) {
                p.getInventory().addItem(new ItemStack[]{i});
            }
            for (Player pl : Bukkit.getOnlinePlayers()) {
                Main.CreateScoreBoard(pl);
            }
            Main.Fixinvis();
            if (wz.getLocation() != null) {
                p.teleport(wz.getLocation());
                wz.getLocation().getBlock().setType(Material.AIR);
                wz.setLocation(null);
                if (wz.getHologram() != null && wz.getHologram().isSpawned()) {
                    wz.getHologram().despawn();
                    wz.setHologram(null);
                }
                return;
            }
            if (wz.isRed()) {
                p.teleport(Config.getRedSpawn());
                return;
            }
            if (wz.isBlue()) {
                p.teleport(Config.getBlueSpawn());
                return;
            }
        }
    }

    public static void Fixinvis() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            WarZonePlayer wz = Main.getWarZonePlayer(p.getUniqueId());
            for (Player player : Bukkit.getOnlinePlayers()) {
                WarZonePlayer wzplayer = Main.getWarZonePlayer(player.getUniqueId());
                if (wz.isObserver()) {
                    p.showPlayer(player);
                }
                if (!wz.isBlue() && !wz.isRed()) continue;
                if (wzplayer.isObserver()) {
                    p.hidePlayer(player);
                    continue;
                }
                p.showPlayer(player);
            }
        }
    }

    public static void endGameCount() {
        time = 0;
        if (!GameStarted) {
            return;
        }
        GameStarted = false;
        if (!GameEndedDuetoWoolDestroyed) {
            Bukkit.broadcastMessage((ChatColor.translateAlternateColorCodes('&', "&6&l[&c&lWarzone&6&l] &cGame ended due to time limit being reached")));
            Bukkit.broadcastMessage((ChatColor.translateAlternateColorCodes('&', "&6&l[&c&lWarzone&6&l] &cOr a staff member ended the game!")));
        }
        cd.start(10, "restarting");
        for (Player p : Bukkit.getOnlinePlayers()) {
            WarZonePlayer wz = Main.getWarZonePlayer(p.getUniqueId());
            wz.setObserver();
            p.getInventory().clear();
            p.setGameMode(GameMode.SURVIVAL);
            p.setPlayerListName(String.valueOf(Config.getPrefix(p)) + " " + ChatColor.AQUA + p.getName());
            Mysql.UpdateWarZonePlayer(p.getUniqueId());
            for (Player toshow : Bukkit.getOnlinePlayers()) {
                p.showPlayer(toshow);
            }
        }
        new BukkitRunnable(){

            public void run() {
                if (GameStarted) {
                    return;
                }
                endGame();
            }
        }.runTaskLater(plugin, 200);
    }
    
    public static void endGame() {
        File toDelete;
        File backup;
        time = 0;
        String tounload = ActiveWorld;
        if (ActiveWorld == "WarzoneWorld") {
            ActiveWorld = "WarzoneWorld2";
            backup = new File(Bukkit.getWorldContainer() + "/WarzoneWorld");
            toDelete = new File(Bukkit.getWorldContainer() + "/WarzoneWorld/uid.dat");
        } else {
            ActiveWorld = "WarzoneWorld";
            backup = new File(Bukkit.getWorldContainer() + "/WarzoneWorld2");
            toDelete = new File(Bukkit.getWorldContainer() + "/WarzoneWorld2/uid.dat");
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (WarZonePlayers.containsKey(p.getUniqueId())) {
                WarZonePlayer wz = WarZonePlayers.get(p.getUniqueId());
                wz.setObserver();
            }
            p.teleport(Config.getObserverSpawn());
            p.setHealth(20.0);
            Main.giveStartItems(p);
        }
        Bukkit.getServer().unloadWorld(tounload, false);
        activeworldint = activeworldint < worlds.size() - 1 && activeworldint != -1 ? ++activeworldint : 0;
        currentWorldName = Main.NextWorld = worlds.get(activeworldint);
        try {
            FileUtils.deleteDirectory((File)backup);
            FileUtils.copyDirectory((File)new File(Bukkit.getWorldContainer() + "/" + worlds.get(activeworldint)), (File)backup);
            FileUtils.forceDelete((File)toDelete);
            log.info("Debug 3: deleted:" + tounload + " activeworld:" + ActiveWorld);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.getServer().createWorld(new WorldCreator(tounload));
        Bukkit.getServer().getWorld(tounload).setAutoSave(false);
        for(com.cubedcraft.warzone.teams.Team team : teams.values()) {
        	team.getPlayers().clear();
        }
        GameStarted = false;
        gamestartingwithtimer = false;
        ResetScore();
    }

    public static int getRedTeamScore() {
        return RedTeamScore;
    }

    public static void setRedTeamScore(int score) {
        RedTeamScore = score;
    }

    public static int getBlueTeamScore() {
        return BlueTeamScore;
    }

    public static void setBlueTeamScore(int score) {
        BlueTeamScore = score;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static void addWarZonePlayer(UUID uuid, WarZonePlayer wz) {
        WarZonePlayers.put(uuid, wz);
    }

    public static boolean isEnoughPlayers() {
        int red = 0;
        int blue = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            WarZonePlayer wz = Main.getWarZonePlayer(p.getUniqueId());
            if (wz.isBlue()) {
                ++blue;
            }
            if (!wz.isRed()) continue;
            ++red;
        }
        if (red >= 1 && blue >= 1) {
            return true;
        }
        return false;
    }

    public static WarZonePlayer getWarZonePlayer(UUID uuid) {
        if (WarZonePlayers.get(uuid) == null) {
            WarZonePlayer wz = Mysql.getWarZonePlayer(uuid);
            Main.addWarZonePlayer(uuid, wz);
            return wz;
        }
        return WarZonePlayers.get(uuid);
    }

    public static void removeWarZonePlayer(UUID uuid) {
        if (WarZonePlayers.containsKey(uuid)) {
        	WarZonePlayer wzp = WarZonePlayers.get(uuid);
        	if(wzp.team != null) {
        		teams.get(wzp.team).getPlayers().remove(wzp);
        	}
            WarZonePlayers.remove(uuid);
        }
    }

    public static void CreateScoreBoard(Player p) {
        WarZonePlayer wz = Main.getWarZonePlayer(p.getUniqueId());
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective(ChatColor.YELLOW + "Warzone", p.getName());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Objective objective2 = board.registerNewObjective("HEALTH", "health");
        objective2.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective2.setDisplayName(ChatColor.DARK_RED + StringEscapeUtils.unescapeJava("\u2764"));
        Team Blue = board.registerNewTeam("BlueTeam");
        Team red = board.registerNewTeam("RedTeam");
        Blue.setAllowFriendlyFire(false);
        red.setAllowFriendlyFire(false);
        for (Player pl : Bukkit.getOnlinePlayers()) {
            WarZonePlayer wzp = Main.getWarZonePlayer(pl.getUniqueId());
            if (wzp.isBlue()) {
                Blue.addEntry(pl.getName());
                red.removeEntry(pl.getName());
                continue;
            }
            if (!wzp.isRed()) continue;
            red.addEntry(pl.getName());
            Blue.removeEntry(pl.getName());
        }
        Score Objectives = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&eObjectives"));
        Score Coins = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&a> &fCoins:&a " + wz.getCoins()));
        Score Stats = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&eStats"));
        Score blank = objective.getScore("  ");
        Score Kills = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&a> &fKills:&a " + wz.getKills()));
        Score Deaths = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&a> &fDeaths:&a " + wz.getDeaths()));
        Score Wins = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&a> &fWins:&a " + wz.getWins()));
        Score Wool = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&a> &fScore:&a " + wz.getExp()));
        Score Score = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&a> &fWool:&a " + wz.getWool()));
        Score RedTeamScored = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&cRed: " + RedTeamScore + "%"));
        Score BlueTeamScored = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&9Blue: " + BlueTeamScore + "%"));
        Objectives.setScore(11);
        RedTeamScored.setScore(10);
        BlueTeamScored.setScore(9);
        blank.setScore(8);
        Stats.setScore(7);
        Coins.setScore(6);
        Kills.setScore(5);
        Deaths.setScore(4);
        Wins.setScore(3);
        Score.setScore(2);
        Wool.setScore(1);
        p.setDisplayName(String.valueOf(Config.getPrefix(p)) + " " + ChatColor.WHITE + p.getName());
        p.setScoreboard(board);
    }

    public static void ResetScore() {
        Main.setBlueTeamScore(100);
        Main.setRedTeamScore(100);
        for (Player p : Bukkit.getOnlinePlayers()) {
            Main.CreateScoreBoard(p);
        }
    }

    public static void updateBlueScore() {
        setBlueTeamScore(Main.getBlueTeamScore() - 10);
        if (getBlueTeamScore() <= 0) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                WarZonePlayer wz = Main.getWarZonePlayer(p.getUniqueId());
                int xp = Config.ExpPerWool();
                int coins = Config.CoinsPerWool();
                if (p.hasPermission("warzone.vip")) {
                    xp *= 2;
                    coins *= 2;
                }
                if (wz.isRed()) {
                    wz.setWins(wz.getWins() + 1);
                    wz.setCoins(wz.getCoins() + coins);
                    wz.setExp(wz.getExp() + xp);
                    Bukkit.broadcastMessage(ChatColor.RED + "The red team won!");
                    Bukkit.broadcastMessage(ChatColor.RED + "The red team won!");
                    Bukkit.broadcastMessage(ChatColor.RED + "The red team won!");
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        titleManagerAPI.sendTitle(player, ChatColor.RED + "Red team won!");
                    });
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.getPlugin().getConfig().getString("commands.playerWin")
                    		.replace("%player%", p.getName()));
                }
                CreateScoreBoard(p);
            }
            GameEndedDuetoWoolDestroyed = true;
            endGameCount();
            return;
        }
    }

    public static void UpdateRedScore() {
        setRedTeamScore(Main.getRedTeamScore() - 10);
        if (getRedTeamScore() <= 0) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                WarZonePlayer wz = Main.getWarZonePlayer(p.getUniqueId());
                int xp = Config.ExpPerWool();
                int coins = Config.CoinsPerWool();
                if (p.hasPermission("warzone.vip")) {
                    xp *= 2;
                    coins *= 2;
                }
                if (wz.isBlue()) {
                    wz.setWins(wz.getWins() + 1);
                    wz.setCoins(wz.getCoins() + coins);
                    wz.setExp(wz.getExp() + xp);
                    Bukkit.broadcastMessage(ChatColor.BLUE + "The blue team won!");
                    Bukkit.broadcastMessage(ChatColor.BLUE + "The blue team won!");
                    Bukkit.broadcastMessage(ChatColor.BLUE + "The blue team won!");
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        titleManagerAPI.sendTitle(player, ChatColor.BLUE + "Blue team won!");
                    });
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.getPlugin().getConfig().getString("commands.playerWin")
                    		.replace("%player%", p.getName()));
                }
                CreateScoreBoard(p);
            }
            GameEndedDuetoWoolDestroyed = true;
            endGameCount();
            return;
        }
    }

    public static void TeamSelect(Player p) {
        Inventory TeamSelectInv = Bukkit.createInventory(null, 9, ChatColor.RED + "Team Selection");
        ItemStack AutoSelect = new ItemStack(Material.BOOK);
        ItemMeta AutoSelectim = AutoSelect.getItemMeta();
        AutoSelectim.setDisplayName(ChatColor.GREEN + "Auto Select");
        AutoSelect.setItemMeta(AutoSelectim);
        ItemStack TeamBlue = new ItemStack(Material.WOOL);
        TeamBlue.setDurability((short) 11);
        ItemMeta blueim = TeamBlue.getItemMeta();
        blueim.setDisplayName(ChatColor.BLUE + "Blue Team");
        TeamBlue.setItemMeta(blueim);
        ItemStack TeamRed = new ItemStack(Material.WOOL);
        TeamRed.setDurability((short) 14);
        ItemMeta redim = TeamRed.getItemMeta();
        redim.setDisplayName(ChatColor.RED + "Red Team");
        TeamRed.setItemMeta(redim);
        
        TeamSelectInv.addItem(AutoSelect);
        TeamSelectInv.addItem(TeamBlue);
        TeamSelectInv.addItem(TeamRed);
        p.openInventory(TeamSelectInv);
    }

    public static void KitSelect(Player p) {
        p.openInventory(Config.GetKitListInvetory(p));
    }

    static /* synthetic */ void access$0(boolean bl) {
        GameEndedDuetoWoolDestroyed = bl;
    }

    static /* synthetic */ void access$2(boolean bl) {
        gamestartingwithtimer = bl;
    }

	public static void Debug(Player p) {
        log.info("DEBUG: " + worlds.toString());
        log.info("DEBUG: " + worlds.get(activeworldint));
        log.info("DEBUG: " + currentWorldName);
        log.info("DEBUG: " + Main.NextWorld);
	}
}