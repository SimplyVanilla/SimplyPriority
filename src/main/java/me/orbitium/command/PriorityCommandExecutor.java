package me.orbitium.command;

import me.clip.placeholderapi.PlaceholderAPI;
import me.orbitium.SimplyPriority;
import me.orbitium.util.MessageUtil;
import me.orbitium.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.logging.Level;

public class PriorityCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp() ||
                sender.hasPermission(SimplyPriority.getInstance().getConfig().getString("permissions.managePriorities"))) {

            Bukkit.getScheduler().runTaskAsynchronously(SimplyPriority.getInstance(), () -> {
                if (args.length == 2) {
                    try {
                        switch (args[0]) {
                            case "add":
                                boolean success = SimplyPriority.getDatabase().addPlayerToDatabase(PlayerUtil.getUUIDOrName(args[1]));
                                MessageUtil.sendMessageToSender(sender, args[1], success
                                        , "messages.addedPriorityToPlayer", "messages.error.playerAlreadyHavePriority");
                                break;
                            case "remove":
                                boolean success1 = SimplyPriority.getDatabase().removePlayerFromDatabase(PlayerUtil.getUUIDOrName(args[1]));
                                MessageUtil.sendMessageToSender(sender, args[1], success1
                                        , "messages.priorityRemovedFromPlayer", "messages.error.playerDontHavePriority");
                                break;
                            case "check":
                                boolean success2 = SimplyPriority.getDatabase().playerHasPriority(PlayerUtil.getUUIDOrName(args[1]));
                                MessageUtil.sendMessageToSender(sender, args[1], success2
                                        , "messages.playerHavePriority", "messages.playerDoesntHavePriority");
                                break;
                        }
                    } catch (Exception ex) {
                        MessageUtil.sendMessage(sender, args[1], "messages.error.anyPlayerCannotFound");
                    }
                }
            });
        }
        return true;
    }

}
