package be.bendem.orebroadcast;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {

    public OreBroadcast plugin;

    CommandHandler(OreBroadcast plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("ob") && args.length == 1
                && args[0].equalsIgnoreCase("reload")
                && sender.hasPermission("ob.reload")) {
            plugin.reloadConfig();
            plugin.loadConfig();
            sender.sendMessage("Config reloaded...");
            return true;
        }

        return false;
    }

}
