package be.bendem.bukkit.orebroadcast;

import be.bendem.bukkit.orebroadcast.commands.Command;
import be.bendem.bukkit.orebroadcast.commands.CommandHandler;
import be.bendem.bukkit.orebroadcast.handlers.BlockBreakListener;
import be.bendem.bukkit.orebroadcast.handlers.BlockPlaceListener;
import net.gravitydevelopment.updater.Updater;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.mcstats.Metrics;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

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
    private boolean metricsActive = true;
    private Metrics metrics;
    private Updater updater;
    private boolean isUpdateAvailable = false;
    private boolean isUpdated = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        if(metricsActive) {
            startMetrics();
        }

        if(getConfig().getBoolean("updater.startup-check", true)) {
            checkUpdate();
            if(isUpdateAvailable) {
                getLogger().warning(updater.getLatestName() + " is available, type '/ob update download' to download it");
            }
        }

        if(getConfig().getBoolean("updater.warn-ops", true)) {
            getServer().getPluginManager().registerEvents(new PlayerLoginListener(this), this);
        }

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

        commandHandler.register(new Command("update", "ob.commands.update") {
            @Override
            public void execute(CommandSender sender, List<String> args) {
                if(args.size() < 1) {
                    sender.sendMessage("Not enough arguments");
                    return;
                }
                if(isUpdated) {
                    sender.sendMessage("An update has already been downloaded, restart the server to apply it");
                    return;
                }

                if(args.get(0).equalsIgnoreCase("check")) {
                    checkUpdate();
                    if(isUpdateAvailable) {
                        sender.sendMessage("Update available");
                    } else {
                        sender.sendMessage("No update available");
                    }
                    return;
                }

                if(args.get(0).equalsIgnoreCase("download")) {
                    checkUpdate();
                    if(isUpdateAvailable) {
                        sender.sendMessage("Downloading update...");
                        downloadUpdate();
                    } else {
                        sender.sendMessage("No update available");
                    }
                }
            }
        });
    }

    @Override
    public void onDisable() {
        stopMetrics();
    }

    /**
     * Blacklists a block. Blocks blacklisted won't get broadcasted when
     * broken.
     *
     * @param block the block to blacklist
     */
    public void blackList(Block block) {
        broadcastBlacklist.add(block);
    }

    /**
     * Blacklists multiple blocks. Blocks blacklisted won't get broadcasted
     * when broken.
     *
     * @param blocks the blocks to blacklist
     */
    public void blackList(Collection<Block> blocks) {
        broadcastBlacklist.addAll(blocks);
    }

    /**
     * Unblacklist a block.
     *
     * @param block the block to unblacklist
     */
    public void unBlackList(Block block) {
        broadcastBlacklist.remove(block);
    }

    /**
     * Clear the blacklist.
     *
     * @return Count of blocks removed from the blacklist
     */
    public int clearBlackList() {
        int size = broadcastBlacklist.size();
        broadcastBlacklist.clear();
        return size;
    }

    /**
     * Checks wether a block is blacklisted or not.
     *
     * @param block the block to check
     * @return true if the block is blacklisted
     */
    public boolean isBlackListed(Block block) {
        return broadcastBlacklist.contains(block);
    }

    /**
     * Checks wether a material should be broadcasted when broken
     *
     * @param material the material to check
     * @return true if breaking a block of that material will trigger a
     *         broadcast
     */
    public boolean isWhitelisted(Material material) {
        return blocksToBroadcast.contains(material);
    }

    /**
     * Check if OreBroadcast is active in a world
     *
     * @param world the name of the world
     * @return true if OreBroadcast is active in the world
     */
    public boolean isWorldWhitelisted(String world) {
        return !worldWhitelistActive || worldWhitelist.contains(world);
    }

    private void loadConfig() {
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

        // Handling metrics changes
        boolean prev = metricsActive;
        metricsActive = getConfig().getBoolean("metrics", true);
        if(prev != metricsActive) {
            if(metricsActive) {
                startMetrics();
            } else {
                stopMetrics();
            }
        }
    }

    private void startMetrics() {
        if(metrics == null) {
            try {
                metrics = new Metrics(this);
            } catch(IOException e) {
                getLogger().warning("Couldn't activate metrics :(");
                return;
            }
        }
        metrics.start();
    }

    private void stopMetrics() {
        if(metrics == null) {
            return;
        }
        // This is temporary while waiting for https://github.com/Hidendra/Plugin-Metrics/pull/43
        try {
            Field taskField = metrics.getClass().getDeclaredField("task");
            taskField.setAccessible(true);
            BukkitTask task = (BukkitTask) taskField.get(metrics);
            if(task != null) {
                task.cancel();
            }
        } catch(NoSuchFieldException | IllegalAccessException e) {
            getLogger().log(Level.WARNING, "Error while stopping metrics, please report this to the plugin author", e);
        }
    }

    private void checkUpdate() {
        if(!isUpdateAvailable) {
            updater = new OBUpdater(this, 72299, getFile(), Updater.UpdateType.NO_DOWNLOAD, true);
            isUpdateAvailable = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
        }
    }

    private void downloadUpdate() {
        updater = new OBUpdater(this, 72299, getFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
        isUpdated = true;
    }

    boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    boolean isUpdated() {
        return isUpdated;
    }

}
