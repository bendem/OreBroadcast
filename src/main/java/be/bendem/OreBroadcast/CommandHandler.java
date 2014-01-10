package be.bendem.OreBroadcast;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {

    public OreBroadcast plugin;

    CommandHandler(OreBroadcast plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("ob")) {
            return false;
        }

        plugin.logger.info("ob command issued...");
        if(args.length == 0) {
            // Default

            // ...............

            return true;
        }

        if(args[0].equalsIgnoreCase("reload") && args.length == 1) {
            if (sender.hasPermission("ob.reload")) {
                plugin.reloadConfig();
                sender.sendMessage("Config reloaded...");
                return true;
            }
        }

        return false;
    }

}
