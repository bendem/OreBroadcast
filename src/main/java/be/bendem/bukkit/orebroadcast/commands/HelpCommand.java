package be.bendem.bukkit.orebroadcast.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author bendem
 */
public class HelpCommand extends Command {

    private final CommandHandler handler;

    protected HelpCommand(CommandHandler handler) {
        super("help", "Displays the commands you can use", null);
        this.handler = handler;
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        StringBuilder builder = new StringBuilder("OreBroadcast commands: \n");
        for(Command command : handler.getCommands().values()) {
            if(command.getPermission() == null || sender.hasPermission(command.getPermission())) {
                builder.append("- ").append(ChatColor.BLUE).append(command.getName()).append(ChatColor.RESET);
                if(command.getDescription() != null) {
                    builder.append(": ").append(command.getDescription());
                }
                builder.append('\n');
            }
        }
        sender.sendMessage(builder.deleteCharAt(builder.length() - 1).toString());
    }

}
