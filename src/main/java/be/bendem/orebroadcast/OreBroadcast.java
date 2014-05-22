package be.bendem.orebroadcast;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * OreBroadcast for Bukkit
 *
 * @author bendem
 */
public class OreBroadcast extends JavaPlugin {

    // As it's currently stored, blocks which have already been broadcasted
    // will be again after a server restart / reload.
    private final Set<Block>    broadcastBlacklist = new HashSet<>();
    private final Set<Material> blocksToBroadcast  = new HashSet<>();
    private final Set<String>   worldWhitelist     = new HashSet<>();
    private boolean worldWhitelistActive = false;

    @Override
    public void onEnable() {
        PluginDescriptionFile description = getDescription();

        saveDefaultConfig();
        loadConfig();
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getCommand("ob").setExecutor(new CommandHandler(this));
        getLogger().fine(description.getName() + " version " + description.getVersion() + " is enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().fine(getDescription().getName() + " want you to have a nice day ;-)");
    }

    public void blackList(Block block) {
        broadcastBlacklist.add(block);
    }

    public void blackList(Collection<Block> blocks) {
        broadcastBlacklist.addAll(blocks);
    }

    public void unBlackList(Block block) {
        broadcastBlacklist.remove(block);
    }

    public boolean isBlackListed(Block block) {
        return broadcastBlacklist.contains(block);
    }

    public boolean isWhitelisted(Material material) {
        return blocksToBroadcast.contains(material);
    }

    public boolean isWorldWhitelisted(String world) {
        return !worldWhitelistActive || worldWhitelist.contains(world);
    }

    public void loadConfig() {
        // Create the list of materials to broadcast from the file
        List<String> configList = getConfig().getStringList("ores");
        blocksToBroadcast.clear();

        for (String item : configList) {
            Material material = Material.getMaterial(item.toUpperCase() + "_ORE");
            blocksToBroadcast.add(material);
            // Handle glowing redstone ore (id 74) and redstone ore (id 73)
            if (material.equals(Material.REDSTONE_ORE)) {
                blocksToBroadcast.add(Material.GLOWING_REDSTONE_ORE);
            }
        }

        // Load worlds
        worldWhitelist.clear();
        worldWhitelistActive = getConfig().getBoolean("active-per-worlds", true);
        if(worldWhitelistActive) {
            worldWhitelist.addAll(getConfig().getStringList("active-worlds"));
        }
    }

}
