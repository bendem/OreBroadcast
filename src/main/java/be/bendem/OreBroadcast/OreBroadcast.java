package be.bendem.OreBroadcast;

import org.bukkit.block.Block;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * OreBroadcast for Bukkit
 *
 * @author bendem
 */
public class OreBroadcast extends JavaPlugin {

    public PluginDescriptionFile pdfFile;
    public Logger logger;
    // As it's currently stored, blocks which have already been broadcasted
    // will be again after a server restart / reload.
    //
    // Note to myself : maybe ArrayList isn't optimized... Should look at Hash____
    // (in BlockBreakListener as well...)
    public ArrayList<Block> alreadyBroadcastedBlocks = new ArrayList<Block>();

    @Override
    public void onEnable() {
        logger = getLogger();
        pdfFile = getDescription();

        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getCommand("ob").setExecutor(new CommandHandler(this));
        logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    @Override
    public void onDisable() {
        logger.info(pdfFile.getName() + " want you to have a nice day ;-)");
    }

}
