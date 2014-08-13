package be.bendem.bukkit.orebroadcast.commands;

import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author bendem
 */
public abstract class Command {

    private final String name;
    private final String permission;

    protected Command(String name) {
        this(name, null);
    }

    protected Command(String name, String permission) {
        Validate.notNull(name);
        this.name = name;
        this.permission = permission;
    }

    public abstract void execute(CommandSender sender, List<String> args);

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

}
