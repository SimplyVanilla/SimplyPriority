package me.orbitium.database;

import me.orbitium.SimplyPriority;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PoolManager {

    Map<UUID, PriorityType> playersInPool = new HashMap<>();

    private static final int normalPlayerPoolSize = SimplyPriority.getInstance().getConfig().getInt("pool.normalPlayerPoolSize");
    private static final int priorityPlayerPoolSize = SimplyPriority.getInstance().getConfig().getInt("pool.priorityPlayerPoolSize");
    private static final int moderatorPlayerPoolSize = SimplyPriority.getInstance().getConfig().getInt("pool.moderatorPlayerPoolSize");
    private static final boolean overridePriorityPoolSize = SimplyPriority.getInstance().getConfig().getBoolean("pool.priorityPlayersCanUseNormalPlayerPool");

    private static int currentNormalPlayerCount = 0;
    private static int currentPriorityPlayerCount = 0;

    public static int maxPlayer;

    public PoolManager(SimplyPriority simplyPriority) {
        int maxPlayer = simplyPriority.getServer().getMaxPlayers();
        int totalPoolSize = normalPlayerPoolSize + priorityPlayerPoolSize + moderatorPlayerPoolSize;
        if (maxPlayer != totalPoolSize) {
            throw new IllegalStateException("Max player count and pool size must be same!" +
                    " Total server player limit: " + maxPlayer + " but total pool size is: " + totalPoolSize);
        }

        if (SimplyPriority.getInstance().getConfig().getBoolean("pool.hideNormalPlayerPoolSizeOnMaxPlayerList"))
            maxPlayer -= normalPlayerPoolSize;
        if (SimplyPriority.getInstance().getConfig().getBoolean("pool.hidePriorityPlayerPoolSizeOnMaxPlayerList"))
            maxPlayer -= normalPlayerPoolSize;
        if (SimplyPriority.getInstance().getConfig().getBoolean("pool.hideModeratorPlayerPoolSizeOnMaxPlayerList"))
            maxPlayer -= normalPlayerPoolSize;
        PoolManager.maxPlayer = maxPlayer;
    }

    public boolean addNewPlayerToPool(UUID playerUUID, PriorityType priorityType) {
        if (priorityType == PriorityType.NORMAL) { // Normal pool
            if ((currentNormalPlayerCount + 1) <= normalPlayerPoolSize) {
                playersInPool.put(playerUUID, PriorityType.NORMAL);
                currentNormalPlayerCount++;
                return true;
            }
        } else { // Priority pool
            if ((currentPriorityPlayerCount + 1) <= priorityPlayerPoolSize) {
                playersInPool.put(playerUUID, PriorityType.PRIORITY);
                currentPriorityPlayerCount++;
                return true;
            } else if (overridePriorityPoolSize && (currentNormalPlayerCount != normalPlayerPoolSize)) {
                playersInPool.put(playerUUID, PriorityType.NORMAL);
                currentNormalPlayerCount++;
                return true;
            }
        }
        return false;
    }

    public void removePlayerFromPool(UUID playerUUID) {
        PriorityType priorityType = playersInPool.get(playerUUID);
        if (priorityType == PriorityType.NORMAL)
            currentNormalPlayerCount--;
        else if (priorityType == PriorityType.PRIORITY)
            currentPriorityPlayerCount--;
        playersInPool.remove(playerUUID);
    }
}
