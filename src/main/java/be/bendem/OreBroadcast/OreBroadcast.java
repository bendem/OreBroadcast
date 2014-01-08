package be.bendem.OreBroadcast;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * OreBroadcast for Bukkit
 *
 * @author bendem
 */
public class OreBroadcast extends JavaPlugin {

    public PluginDescriptionFile pdfFile;
    public Logger logger;

    @Override
    public void onEnable() {
        logger = getLogger();

        pdfFile = getDescription();
        logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");

        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getCommand("ob").setExecutor(new CommandHandler(this));
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        logger.info(pdfFile.getName() + " want you to have a nice day ;-)");
    }

}
