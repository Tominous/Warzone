package com.cubedcraft.warzone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.jline.internal.Log;

public class Mysql {
    static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static String DB_URL = "jdbc:mysql://localhost/EMP";
    static String USER = "username";
    static String PASS = "password";
    static String dbname = "dbname";

    public static void CheckConnection() {
        DB_URL = Main.getPlugin().getConfig().getString("DataBaseHost");
        USER = Main.getPlugin().getConfig().getString("DataBaseUser");
        PASS = Main.getPlugin().getConfig().getString("DataBasePassword");
        dbname = Main.getPlugin().getConfig().getString("DataBaseName");
        String CreateTableStatement = "CREATE TABLE warzone (uuid varchar(255) NOT NULL, coins int NOT NULL DEFAULT '0', kits TEXT NOT NULL, kills int NOT NULL DEFAULT '0', deaths int NOT NULL DEFAULT '0', wins int NOT NULL DEFAULT '0', wool int NOT NULL DEFAULT '0', exp int NOT NULL DEFAULT '0',username varchar(255)NOT NULL , PRIMARY KEY (uuid) )";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connect = DriverManager.getConnection("jdbc:mysql://" + DB_URL + "/" + dbname, USER, PASS);
            ResultSet tables = connect.getMetaData().getTables(null, null, "warzone", null);
            if (tables.next()) {
                tables.close();
            } else {
                tables.close();
                connect.createStatement().executeUpdate(CreateTableStatement);
            }
            Log.info((Object[])new Object[]{"[WarZone]Succesfully connected to the database!"});
            connect.close();
        }
        catch (Exception e) {
            Log.error((Object[])new Object[]{e.toString()});
        }
    }

    public static WarZonePlayer getWarZonePlayer(UUID uuid) {
        WarZonePlayer wz = null;
        try {
            Connection connect = DriverManager.getConnection("jdbc:mysql://" + DB_URL + "/" + dbname, USER, PASS);
            PreparedStatement getPlayer = connect.prepareStatement("SELECT * FROM warzone WHERE uuid = ?");
            getPlayer.setString(1, uuid.toString());
            ResultSet rs = getPlayer.executeQuery();
            if (rs.next()) {
                String id = rs.getString("uuid");
                int coins = rs.getInt("coins");
                String kits = rs.getString("kits");
                int kills = rs.getInt("kills");
                int deaths = rs.getInt("deaths");
                int wins = rs.getInt("wins");
                int wool = rs.getInt("wool");
                int exp = rs.getInt("exp");
                Log.info((Object[])new Object[]{"wool:" + wool});
                rs.close();
                getPlayer.close();
                connect.close();
                return new WarZonePlayer(id, coins, kills, deaths, wins, wool, exp, kits, Bukkit.getPlayer((UUID)uuid).getName());
            }
            rs.close();
            getPlayer.close();
            connect.close();
            return Mysql.CreateRow(uuid);
        }
        catch (Exception e) {
            Log.error((Object[])new Object[]{e.toString()});
            return wz;
        }
    }

    public static WarZonePlayer CreateRow(UUID uuid) {
        try {
            Connection connect = DriverManager.getConnection("jdbc:mysql://" + DB_URL + "/" + dbname, USER, PASS);
            PreparedStatement RegisterUser = connect.prepareStatement("INSERT INTO warzone (uuid, coins, kits, kills, deaths, wins, wool, exp, username)VALUES(?,?,?,?,?,?,?,?,?)");
            RegisterUser.setString(1, uuid.toString());
            RegisterUser.setInt(2, 0);
            RegisterUser.setString(3, "Default");
            RegisterUser.setInt(4, 0);
            RegisterUser.setInt(5, 0);
            RegisterUser.setInt(6, 0);
            RegisterUser.setInt(7, 0);
            RegisterUser.setInt(8, 0);
            RegisterUser.setString(9, Bukkit.getPlayer((UUID)uuid).getName());
            RegisterUser.executeUpdate();
            RegisterUser.close();
            connect.close();
            return new WarZonePlayer(uuid.toString(), 0, 0, 0, 0, 0, 0, "%%Default", Bukkit.getPlayer((UUID)uuid).getName());
        }
        catch (Exception e) {
            Log.error((Object[])new Object[]{e.toString()});
            return new WarZonePlayer(uuid.toString(), 0, 0, 0, 0, 0, 0, "%%Default", Bukkit.getPlayer((UUID)uuid).getName());
        }
    }

    public static void UpdateWarZonePlayer(UUID uuid) {
        try {
            WarZonePlayer wz = Main.getWarZonePlayer(uuid);
            Connection connect = DriverManager.getConnection("jdbc:mysql://" + DB_URL + "/" + dbname, USER, PASS);
            PreparedStatement UpdatePlayer = connect.prepareStatement("UPDATE warzone SET uuid = ?, coins = ?, kits = ?, kills = ?, deaths = ?, wins = ?, wool = ?, exp = ?, username = ? WHERE uuid = ?");
            UpdatePlayer.setString(1, uuid.toString());
            UpdatePlayer.setInt(2, wz.getCoins());
            UpdatePlayer.setString(3, wz.getKits());
            UpdatePlayer.setInt(4, wz.getKills());
            UpdatePlayer.setInt(5, wz.getDeaths());
            UpdatePlayer.setInt(6, wz.getWins());
            UpdatePlayer.setInt(7, wz.getWool());
            UpdatePlayer.setInt(8, wz.getExp());
            UpdatePlayer.setString(9, Bukkit.getPlayer((UUID)uuid).getName());
            UpdatePlayer.setString(10, uuid.toString());
            UpdatePlayer.executeUpdate();
            UpdatePlayer.close();
            connect.close();
        }
        catch (Exception e) {
            Log.error((Object[])new Object[]{e.toString()});
        }
    }
}

