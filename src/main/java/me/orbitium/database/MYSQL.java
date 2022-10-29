package me.orbitium.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import me.orbitium.SimplyPriority;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MYSQL {
    SimplyPriority plugin = SimplyPriority.getInstance();
    String tableName;
    Connection connection;
    Statement statement;

    public MYSQL() {
        this.tableName = this.plugin.getConfig().getString("database.playerNameStorageTableName");
    }

    public synchronized void connect() {
        try {
            this.plugin.getLogger().log(Level.INFO, "Connecting to MYSQL server, please wait...");
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(this.plugin.getConfig().getString("database.url"), this.plugin.getConfig().getString("database.username"), this.plugin.getConfig().getString("database.password"));
            this.statement = this.connection.createStatement();
            String tableCheckQuery = String.format("    CREATE TABLE IF NOT EXISTS `%s` (     `id` int unsigned NOT NULL AUTO_INCREMENT,     `uuid` char(36) NOT NULL,  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,     `updated_at` timestamp ,     PRIMARY KEY (`id`),     UNIQUE KEY `uuid` (`uuid`) )\n", this.tableName);
            this.statement.executeUpdate(tableCheckQuery);
            this.plugin.getLogger().log(Level.INFO, "Connected to the MYSQL server!");
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        if (this.connection == null || this.statement == null) {
            this.plugin.getLogger().log(Level.SEVERE, "Database connection is not stable. Plugin disabling...");
            this.plugin.getLogger().log(Level.SEVERE, "Please check your database. And config file.");
            this.plugin.getPluginLoader().disablePlugin(this.plugin);
        }
    }

    public boolean playerHasPriority(String playerUUID) {
        String playerSearchQuery = String.format("SELECT * FROM `%s` WHERE `uuid` =?", this.tableName);

        try {
            PreparedStatement playerSearchQueryPS = this.connection.prepareStatement(playerSearchQuery);
            playerSearchQueryPS.setString(1, playerUUID);
            ResultSet rs = playerSearchQueryPS.executeQuery();
            if (rs.next()) {
                return rs.getString("uuid") != null && rs.getString("uuid").equals(playerUUID);
            }
        } catch (Exception var6) {
            this.plugin.getLogger().log(Level.SEVERE, "Unable to getPlayerNameData...");
            var6.printStackTrace();
        }

        return false;
    }

    public boolean addPlayerToDatabase(String playerUUID) {
        String playerListUpdateQuery = String.format("INSERT INTO `%s` (`uuid`) VALUES (?)", this.tableName);

        if (playerHasPriority(playerUUID))
            return false;

        try {
            PreparedStatement playerListUpdateQueryPS = this.connection.prepareStatement(playerListUpdateQuery);
            playerListUpdateQueryPS.setString(1, playerUUID);
            playerListUpdateQueryPS.executeUpdate();
        } catch (Exception var5) {
            this.plugin.getLogger().log(Level.SEVERE, "Unable to updatePlayerNameData...");
            var5.printStackTrace();
        }
        return true;
    }

    public boolean removePlayerFromDatabase(String playerUUID) {
        if (!playerHasPriority(playerUUID))
            return false;

        String playerSearchQuery = String.format("SELECT * FROM `%s` WHERE `uuid` =?", this.tableName);

        try {
            PreparedStatement playerSearchQueryPS = this.connection.prepareStatement(playerSearchQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            playerSearchQueryPS.setString(1, playerUUID);
            ResultSet rs = playerSearchQueryPS.executeQuery();
            if (rs.next()) {
                rs.deleteRow();
            }
        } catch (Exception var6) {
            this.plugin.getLogger().log(Level.SEVERE, "Unable to getPlayerNameData...");
            var6.printStackTrace();
        }
        return true;
    }

    public void close() {
        try {
            this.connection.close();
        } catch (Exception var2) {
            this.plugin.getLogger().log(Level.INFO, "MYSQL database is closing...");
            var2.printStackTrace();
        }

    }
}