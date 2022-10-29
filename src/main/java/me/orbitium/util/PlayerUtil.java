package me.orbitium.util;

import me.orbitium.SimplyPriority;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class PlayerUtil {
    public static String getUUIDOrName(String arg) {
        if (arg.length() == 36)
            return arg;

        Player onlinePlayer = Bukkit.getPlayer(arg);
        if (onlinePlayer != null)
            return onlinePlayer.getUniqueId().toString();
        else {
            SimplyPriority.getInstance().getLogger().log(Level.WARNING, "There is no player or UUID in that text: " + arg);
            throw new NullPointerException();
        }
    }
}
