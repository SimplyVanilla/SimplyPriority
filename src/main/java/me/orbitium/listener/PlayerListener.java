package me.orbitium.listener;

import me.orbitium.SimplyPriority;
import me.orbitium.database.PoolManager;
import me.orbitium.database.PriorityType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void asyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        if (Bukkit.getOfflinePlayer(event.getUniqueId()).isOp())
            return;

        boolean hasPriority = SimplyPriority.getDatabase().playerHasPriority(event.getUniqueId().toString());
        boolean value;

        if (hasPriority)
            value = SimplyPriority.getPoolManager().addNewPlayerToPool(event.getUniqueId(), PriorityType.PRIORITY);
        else
            value = SimplyPriority.getPoolManager().addNewPlayerToPool(event.getUniqueId(), PriorityType.NORMAL);

        if (!value)
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, SimplyPriority.getInstance().getConfig().getString("messages.poolIsFull"));
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        SimplyPriority.getPoolManager().removePlayerFromPool(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        event.setMaxPlayers(PoolManager.maxPlayer);
    }
}
