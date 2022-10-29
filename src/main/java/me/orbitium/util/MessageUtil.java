package me.orbitium.util;

import me.orbitium.SimplyPriority;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtil {
    public static void sendMessage(CommandSender sender, String name, String messagePath) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                SimplyPriority.getInstance().getConfig().getString(messagePath).replace("[player_name]", name)));
    }

    public static void sendMessageToSender(CommandSender sender, String arg, boolean success, String truePath, String falsePath) {
        if (success)
            MessageUtil.sendMessage(sender, arg, truePath);
        else
            MessageUtil.sendMessage(sender, arg, falsePath);
    }
}
