package me.orbitium;

import me.clip.placeholderapi.PlaceholderAPI;
import me.orbitium.command.PriorityCommandExecutor;
import me.orbitium.database.MYSQL;
import me.orbitium.database.PoolManager;
import me.orbitium.database.PriorityType;
import me.orbitium.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimplyPriority extends JavaPlugin {

    private static SimplyPriority instance;
    private static MYSQL database;
    private static PoolManager poolManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        instance = this;
        ((database = new MYSQL())).connect();
        poolManager = new PoolManager(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getCommand("priority").setExecutor(new PriorityCommandExecutor());

        if (Bukkit.getOnlinePlayers().size() > 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Bukkit.getScheduler().runTaskAsynchronously(SimplyPriority.getInstance(), () -> {
                    boolean hasPriority = SimplyPriority.getDatabase().playerHasPriority(player.getUniqueId().toString());

                    if (hasPriority)
                        SimplyPriority.getPoolManager().addNewPlayerToPool(player.getUniqueId(), PriorityType.PRIORITY);
                    else
                        SimplyPriority.getPoolManager().addNewPlayerToPool(player.getUniqueId(), PriorityType.NORMAL);
                });
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        database.close();
    }

    public static SimplyPriority getInstance() {
        return instance;
    }

    public static MYSQL getDatabase() {
        return database;
    }

    public static PoolManager getPoolManager() {
        return poolManager;
    }

}
