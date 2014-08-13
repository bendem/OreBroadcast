package be.bendem.bukkit.orebroadcast.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHandler implements CommandExecutor {

    private final Map<String, Command> commands;

    public CommandHandler(JavaPlugin plugin, String command) {
        plugin.getCommand(command).setExecutor(this);
        commands = new HashMap<>();
    }

    public void register(Command cmd) {
        commands.put(cmd.getName(), cmd);
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if(!cmd.getName().equalsIgnoreCase("ob") || args.length < 1) {
            return false;
        }
        Command command = commands.get(args[0]);
        if(command == null) {
            return false;
        }
        if(command.getPermission() == null || sender.hasPermission(command.getPermission())) {
            List<String> argList = Arrays.asList(args).subList(1, args.length);
            commands.get(args[0]).execute(sender, argList);
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have the permission to use that command.");
        }
        return true;
    }

}
