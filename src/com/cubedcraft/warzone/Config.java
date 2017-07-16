package com.cubedcraft.warzone;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;

public class Config {
    static Plugin plugin = Main.getPlugin();
    private static FileConfiguration config = plugin.getConfig();
    static Logger log = Logger.getLogger("Minecraft.GUIKits");
    static String KitType = "Kit";

    public static String getWorldName() {
        return config.getString("World");
    }

    public static int CoinsPerKill() {
        return config.getInt("CoinsPerKill");
    }

    public static int CoinsPerWool() {
        return config.getInt("CoinsPerWool");
    }

    public static int CoinsPerWin() {
        return config.getInt("CoinsPerWin");
    }

    public static int ExpPerKill() {
        return config.getInt("ExpPerKill");
    }

    public static int ExpPerWool() {
        return config.getInt("ExpPerWool");
    }

    public static int ExpPerWin() {
        return config.getInt("ExpPerWin");
    }

    public static int getRadius() {
        return config.getInt("WoolRadius");
    }

    public static List<String> getWorlds() {
        return config.getStringList("WorldNames");
    }

    public static Location getRedSpawn() {
        String worldname = Main.getCurrentWorldName();
        Double x = config.getDouble(String.valueOf(worldname) + ".RedSpawn.x");
        Double y = config.getDouble(String.valueOf(worldname) + ".RedSpawn.y");
        Double z = config.getDouble(String.valueOf(worldname) + ".RedSpawn.z");
        int pitch = config.getInt(String.valueOf(worldname) + ".RedSpawn.pitch");
        int yaw = config.getInt(String.valueOf(worldname) + ".RedSpawn.yaw");
        return new Location(Bukkit.getWorld((String)Main.ActiveWorld), x.doubleValue(), y.doubleValue(), z.doubleValue(), (float)yaw, (float)pitch);
    }

    public static Location getBlueSpawn() {
        String worldname = Main.getCurrentWorldName();
        Double x = config.getDouble(String.valueOf(worldname) + ".BlueSpawn.x");
        Double y = config.getDouble(String.valueOf(worldname) + ".BlueSpawn.y");
        Double z = config.getDouble(String.valueOf(worldname) + ".BlueSpawn.z");
        int pitch = config.getInt(String.valueOf(worldname) + ".BlueSpawn.pitch");
        int yaw = config.getInt(String.valueOf(worldname) + ".BlueSpawn.yaw");
        return new Location(Bukkit.getWorld((String)Main.ActiveWorld), x.doubleValue(), y.doubleValue(), z.doubleValue(), (float)yaw, (float)pitch);
    }

    public static Location getObserverSpawn() {
        String worldname = Main.getCurrentWorldName();
        Double x = config.getDouble(String.valueOf(worldname) + ".ObserverSpawn.x");
        Double y = config.getDouble(String.valueOf(worldname) + ".ObserverSpawn.y");
        Double z = config.getDouble(String.valueOf(worldname) + ".ObserverSpawn.z");
        int pitch = config.getInt(String.valueOf(worldname) + ".ObserverSpawn.pitch");
        int yaw = config.getInt(String.valueOf(worldname) + ".ObserverSpawn.yaw");
        log.info("Debug: Active-World: " + Bukkit.getWorld(Main.ActiveWorld).getName());
        log.info("Debug: World-Name: " + worldname);
        log.info("Debug: Cords: x: " + x + " y: " + y + " z: " + z);
        return new Location(Bukkit.getWorld((String)Main.ActiveWorld), x.doubleValue(), y.doubleValue(), z.doubleValue(), (float)yaw, (float)pitch);
    }

    public static String getPrefix(Player p) {
        WarZonePlayer wz = Main.getWarZonePlayer(p.getUniqueId());
        int i = wz.getExp();
        while (i <= wz.getExp()) {
            if (config.getString("Ranks." + String.valueOf(i)) != null) {
                String Prefix = config.getString("Ranks." + String.valueOf(i));
                
                Prefix = wz.isBlue() ? 
                		Prefix.replaceAll("%team%", ChatColor.BLUE.toString()) 
                		
                		: (wz.isRed() ? Prefix.replaceAll("%team%", ChatColor.RED.toString()) 
                				: Prefix.replaceAll("%team%", ChatColor.AQUA.toString()));
                
                return ChatColor.translateAlternateColorCodes('&', Prefix);
            }
            if (i == 0) {
                return null;
            }
            --i;
        }
        return null;
    }

    public static Inventory GetKitListInvetory(Player player) {
        String KitType = "Kit";
        String colour = ChatColor.translateAlternateColorCodes((char)'&', (String)((Object)ChatColor.RED + KitType));
        Inventory myInventory = Bukkit.createInventory((InventoryHolder)null, (int)config.getInt(String.valueOf(KitType) + ".Size"), (String)colour);
        int i = 0;
        while (i < config.getInt(String.valueOf(KitType) + ".Size")) {
            String kitName = Integer.toString(i);
            String ONOFF = "Off";
            if (config.getString(String.valueOf(KitType) + "." + kitName + ".Name") != null) {
                if (player.hasPermission(String.valueOf(KitType) + "." + kitName + ".Permission")) {
                    ONOFF = "On";
                }
                int amount = config.getInt(String.valueOf(KitType) + "." + kitName + "." + ONOFF + ".Amount");
                String Name = ChatColor.translateAlternateColorCodes((char)'&', (String)config.getString(String.valueOf(KitType) + "." + kitName + ".Name"));
                Short DataValue = (short)config.getInt(String.valueOf(KitType) + "." + kitName + "." + ONOFF + ".DataValue");
                ItemStack Item = new ItemStack(Material.getMaterial((String)config.getString(String.valueOf(KitType) + "." + kitName + "." + ONOFF + ".Item")));
                Item.setAmount(amount);
                Item.setDurability(DataValue.shortValue());
                ItemMeta md = Item.getItemMeta();
                if (Name != null) {
                    md.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)Name));
                }
                ArrayList<String> lore = new ArrayList<String>();
                lore.add(ChatColor.translateAlternateColorCodes((char)'&', (String)config.getString(String.valueOf(KitType) + "." + kitName + "." + ONOFF + ".Lore")));
                md.setLore(lore);
                Item.setItemMeta(md);
                myInventory.setItem(i, Item);
            }
            ++i;
        }
        return myInventory;
    }

    public static List<ItemStack> getItem(int ItemSlot) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        int i = 0;
        while (i < config.getInt("Kit." + Integer.toString(ItemSlot) + ".KitSize")) {
            String kitName = Integer.toString(ItemSlot);
            if (config.getString(String.valueOf(KitType) + "." + kitName + ".Name") != null) {
                int amount = config.getInt(String.valueOf(KitType) + "." + kitName + ".Item" + i + ".Amount");
                String Name = config.getString(String.valueOf(KitType) + "." + kitName + ".Item" + i + ".Name");
                Short DataValue = (short)config.getInt(String.valueOf(KitType) + "." + kitName + ".Item" + i + ".DataValue");
                ItemStack Item = new ItemStack(Material.getMaterial((String)config.getString(String.valueOf(KitType) + "." + kitName + ".Item" + i + ".Item")));
                if (config.getString(String.valueOf(KitType) + "." + kitName + ".Item" + i + ".Enchantment") != null && Enchantment.getByName((String)config.getString(String.valueOf(KitType) + "." + kitName + ".Item" + i + ".Enchantment")) != null) {
                    Item.addUnsafeEnchantment(Enchantment.getByName((String)config.getString(String.valueOf(KitType) + "." + kitName + ".Item" + i + ".Enchantment")), config.getInt(String.valueOf(KitType) + "." + kitName + ".Item" + i + ".Enchantment-Level"));
                }
                if (config.getString(String.valueOf(KitType) + "." + kitName + ".Item" + i + ".Enchantment2") != null && Enchantment.getByName((String)config.getString(String.valueOf(KitType) + "." + kitName + ".Item" + i + ".Enchantment2")) != null) {
                    Item.addUnsafeEnchantment(Enchantment.getByName((String)config.getString(String.valueOf(KitType) + "." + kitName + ".Item" + i + ".Enchantment2")), config.getInt(String.valueOf(KitType) + "." + kitName + ".Item" + i + ".Enchantment2-Level"));
                }
                Item.setAmount(amount);
                Item.setDurability(DataValue.shortValue());
                ItemMeta md = Item.getItemMeta();
                if (Name != null) {
                    md.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)Name));
                }
                Item.setItemMeta(md);
                items.add(Item);
            }
            ++i;
        }
        return items;
    }

    public static void getArmour(int ItemSlot, WarZonePlayer wzp, Player p) {
        ItemStack[] items = new ItemStack[4];
        ArrayList<String> ItemNames = new ArrayList<String>();
        ItemNames.add("Helmet");
        ItemNames.add("ChestPlate");
        ItemNames.add("Leggings");
        ItemNames.add("Boots");
        String kitName = Integer.toString(ItemSlot);
        ItemStack Helmet = new ItemStack(Material.getMaterial((String)config.getString(String.valueOf(KitType) + "." + kitName + ".Helmet.Item")));
        ItemStack ChestPlate = new ItemStack(Material.getMaterial((String)config.getString(String.valueOf(KitType) + "." + kitName + ".ChestPlate.Item")));
        ItemStack Leggings = new ItemStack(Material.getMaterial((String)config.getString(String.valueOf(KitType) + "." + kitName + ".Leggings.Item")));
        ItemStack Boots = new ItemStack(Material.getMaterial((String)config.getString(String.valueOf(KitType) + "." + kitName + ".Boots.Item")));
        items[0] = Helmet;
        items[1] = ChestPlate;
        items[2] = Leggings;
        items[3] = Boots;
        if (config.getString(String.valueOf(KitType) + "." + kitName + ".Helmet.Enchantment") != null) {
            Helmet.addUnsafeEnchantment(Enchantment.getByName((String)config.getString(String.valueOf(KitType) + "." + kitName + ".Helmet.Enchantment")), config.getInt(config.getString(String.valueOf(KitType) + "." + kitName + ".Helmet.Enchantment")));
        }
        if (config.getString(String.valueOf(KitType) + "." + kitName + ".ChestPlate.Enchantment") != null) {
            ChestPlate.addUnsafeEnchantment(Enchantment.getByName((String)config.getString(String.valueOf(KitType) + "." + kitName + ".ChestPlate.Enchantment")), config.getInt(config.getString(String.valueOf(KitType) + "." + kitName + ".ChestPlate.Enchantment")));
        }
        if (config.getString(String.valueOf(KitType) + "." + kitName + ".Leggings.Enchantment") != null) {
            Leggings.addUnsafeEnchantment(Enchantment.getByName((String)config.getString(String.valueOf(KitType) + "." + kitName + ".Leggings.Enchantment")), config.getInt(config.getString(String.valueOf(KitType) + "." + kitName + ".Leggings.Enchantment")));
        }
        if (config.getString(String.valueOf(KitType) + "." + kitName + ".Boots.Enchantment") != null) {
            Boots.addUnsafeEnchantment(Enchantment.getByName((String)config.getString(String.valueOf(KitType) + "." + kitName + ".Boots.Enchantment")), config.getInt(config.getString(String.valueOf(KitType) + "." + kitName + ".Boots.Enchantment")));
        }
        ItemStack[] arritemStack = items;
        int n = arritemStack.length;
        int n2 = 0;
        while (n2 < n) {
            ItemStack i = arritemStack[n2];
            if (i.getType().equals((Object)Material.LEATHER_CHESTPLATE) || i.getType().equals((Object)Material.LEATHER_BOOTS) || i.getType().equals((Object)Material.LEATHER_HELMET) || i.getType().equals((Object)Material.LEATHER_LEGGINGS)) {
                LeatherArmorMeta lam = (LeatherArmorMeta)i.getItemMeta();
                if (wzp.isBlue()) {
                    lam.setColor(Color.BLUE);
                } else {
                    lam.setColor(Color.RED);
                }
                i.setItemMeta((ItemMeta)lam);
            }
            ++n2;
        }
        p.getInventory().setHelmet(Helmet);
        p.getInventory().setChestplate(ChestPlate);
        p.getInventory().setLeggings(Leggings);
        p.getInventory().setBoots(Boots);
    }

    public static int getKitCost(int KitName) {
        return config.getInt(String.valueOf(KitType) + "." + Integer.toString(KitName) + ".Cost");
    }

    public static String getKitName(int KitName) {
        return config.getString(String.valueOf(KitType) + "." + Integer.toString(KitName) + ".Name");
    }

    public static void HasKit(WarZonePlayer wz, String KitName) {
        wz.getKits().contains(KitName);
    }

    public static int getDefaultKit() {
        return config.getInt("DefaultKit");
    }

    public static int getGameLength() {
        return config.getInt("MatchTime");
    }

    public static boolean HasPermissionForKit(int ItemSlot, Player player) {
        if (config.getString(String.valueOf(KitType) + "." + Integer.toString(ItemSlot) + ".Permission").equalsIgnoreCase("none")) {
            return true;
        }
        if (player.hasPermission(config.getString(String.valueOf(KitType) + "." + Integer.toString(ItemSlot) + ".Permission"))) {
            return true;
        }
        return false;
    }
}

