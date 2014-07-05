package be.bendem.bukkit.orebroadcast;

import be.bendem.bukkit.orebroadcast.commands.Command;
import be.bendem.bukkit.orebroadcast.commands.CommandHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
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
        saveDefaultConfig();
        loadConfig();

        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);

        CommandHandler commandHandler = new CommandHandler(this, "ob");
        commandHandler.register(new Command("clear", "ob.commands.clear") {
            @Override
            public void execute(CommandSender sender, List<String> args) {
                int size = clearBlackList();
                sender.sendMessage(size + " block" + (size > 1 ? "s" : "")  + " cleared...");
            }
        });

        commandHandler.register(new Command("reload", "ob.commands.reload") {
            @Override
            public void execute(CommandSender sender, List<String> args) {
                reloadConfig();
                loadConfig();
                sender.sendMessage("Config reloaded...");
            }
        });
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

    public int clearBlackList() {
        int size = broadcastBlacklist.size();
        broadcastBlacklist.clear();
        return size;
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

        for(String item : configList) {
            Material material = Material.getMaterial(item.toUpperCase() + "_ORE");
            blocksToBroadcast.add(material);
            // Handle glowing redstone ore (id 74) and redstone ore (id 73)
            if(material.equals(Material.REDSTONE_ORE)) {
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
