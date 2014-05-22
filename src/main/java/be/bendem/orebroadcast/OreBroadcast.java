package be.bendem.orebroadcast;

import org.bukkit.block.Block;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public Set<Block> broadcastBlacklist = new HashSet<>();
    public List<String> blocksToBroadcast;

    @Override
    public void onEnable() {
        logger = getLogger();
        pdfFile = getDescription();

        saveDefaultConfig();
        loadBlocksToBroadcastList();
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getCommand("ob").setExecutor(new CommandHandler(this));
        logger.fine(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    @Override
    public void onDisable() {
        logger.fine(pdfFile.getName() + " want you to have a nice day ;-)");
    }

    public void loadBlocksToBroadcastList() {
        // Create the list of blocks to broadcast from the file
        blocksToBroadcast = new ArrayList<>(getConfig().getStringList("ores"));
        for (int i = 0; i < blocksToBroadcast.size(); ++i) {
            blocksToBroadcast.set(i, blocksToBroadcast.get(i).toUpperCase() + "_ORE");
            // Handle glowing redstone ore (id 74) and redstone ore (id 73)
            if(blocksToBroadcast.get(i).equals("REDSTONE_ORE")) {
                blocksToBroadcast.add("GLOWING_REDSTONE");
            }
        }
    }

}
