package be.bendem.bukkit.orebroadcast.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final Map<String, Command> commands;
    private final String               cmdName;

    public CommandHandler(JavaPlugin plugin, String command) {
        commands = new HashMap<>();
        cmdName = command.toLowerCase();

        plugin.getCommand(cmdName).setExecutor(this);
        plugin.getCommand(cmdName).setTabCompleter(this);

        register(new HelpCommand(this));
    }

    public void register(Command cmd) {
        commands.put(cmd.getName(), cmd);
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if(!cmd.getName().equals(cmdName) || args.length < 1) {
            return false;
        }
        Command command = commands.get(args[0]);
        if(command == null) {
            return false;
        }
        if(command.hasPermission(sender)) {
            List<String> argList = Arrays.asList(args).subList(1, args.length);
            commands.get(args[0]).execute(sender, argList);
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have the permission to use that command.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if(!cmd.getName().equals(cmdName) || args.length > 1) {
            return null;
        }
        List<String> results = new LinkedList<>();
        boolean ignoreArg = false;
        if(args.length == 0) {
            ignoreArg = true;
        }
        for(Command command : commands.values()) {
            if(command.hasPermission(sender) && (ignoreArg || command.getName().startsWith(args[0]))) {
                results.add(command.getName());
            }
        }
        return results;
    }

    /* package */ Map<String, Command> getCommands() {
        return commands;
    }

}
