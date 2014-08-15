package be.bendem.bukkit.orebroadcast;

import be.bendem.bukkit.orebroadcast.commands.Command;
import be.bendem.bukkit.orebroadcast.commands.CommandHandler;
import be.bendem.bukkit.orebroadcast.handlers.BlockBreakListener;
import be.bendem.bukkit.orebroadcast.handlers.BlockPlaceListener;
import be.bendem.bukkit.orebroadcast.handlers.PistonListener;
import be.bendem.bukkit.orebroadcast.updater.OreBroadcastUpdater;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * OreBroadcast for Bukkit
 *
 * @author bendem
 */
public class OreBroadcast extends JavaPlugin {

    private Config config;

    @Override
    public void onEnable() {
        config = new Config(this);

        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new PistonListener(this), this);

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
                config.loadConfig();
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
                if(config.getUpdater().isUpdated()) {
                    sender.sendMessage("An update has already been downloaded, restart the server to apply it");
                    return;
                }

                if(args.get(0).equalsIgnoreCase("check")) {
                    config.getUpdater().checkUpdate(sender, false);
                    if(config.getUpdater().isUpdateAvailable()) {
                        sender.sendMessage("Update available");
                    } else {
                        sender.sendMessage("No update available");
                    }
                    return;
                }

                if(args.get(0).equalsIgnoreCase("download")) {
                    config.getUpdater().checkUpdate(sender, true);
                }
            }
        });

        commandHandler.register(new Command("optout", "ob.commands.optout") {
            @Override
            public void execute(CommandSender sender, List<String> args) {
                if(!(sender instanceof Player)) {
                    return;
                }
                UUID uuid = ((Player) sender).getUniqueId();
                if(config.isOptOut(uuid)) {
                    config.unOptOutPlayer(uuid);
                    sender.sendMessage("You will now receive the ore broadcasts");
                } else {
                    config.optOutPlayer(uuid);
                    sender.sendMessage("You won't receive ore broadcasts anymore");
                }
            }
        });
    }

    @Override
    public void onDisable() {
        config.stopMetrics();
    }

    /* package */ File getJar() {
        return getFile();
    }

    public OreBroadcastUpdater getUpdater() {
        return config.getUpdater();
    }

    public boolean isOptOut(Player player) {
        return config.isOptOut(player.getUniqueId());
    }

    /**
     * Blacklists a block. Blocks blacklisted won't get broadcasted when
     * broken.
     *
     * @param block the block to blacklist
     */
    public void blackList(Block block) {
        config.getBroadcastBlacklist().add(new SafeBlock(block));
    }

    /**
     * Blacklists multiple blocks. Blocks blacklisted won't get broadcasted
     * when broken.
     *
     * @param blocks the blocks to blacklist
     */
    public void blackList(Collection<Block> blocks) {
        for(Block block : blocks) {
            blackList(block);
        }
    }

    /**
     * Unblacklist a block.
     *
     * @param block the block to unblacklist
     */
    public void unBlackList(Block block) {
        config.getBroadcastBlacklist().remove(new SafeBlock(block));
    }

    /**
     * Unblacklist multiple blocks.
     *
     * @param blocks the blocks to unblacklist
     */
    public void unBlackList(Collection<Block> blocks) {
        for(Block block : blocks) {
            unBlackList(block);
        }
    }

    /**
     * Clear the blacklist.
     *
     * @return Count of blocks removed from the blacklist
     */
    public int clearBlackList() {
        int size = config.getBroadcastBlacklist().size();
        config.getBroadcastBlacklist().clear();
        return size;
    }

    /**
     * Checks wether a block is blacklisted or not.
     *
     * @param block the block to check
     * @return true if the block is blacklisted
     */
    public boolean isBlackListed(Block block) {
        return config.getBroadcastBlacklist().contains(new SafeBlock(block));
    }

    /**
     * Checks wether a material should be broadcasted when broken
     *
     * @param material the material to check
     * @return true if breaking a block of that material will trigger a
     *         broadcast
     */
    public boolean isWhitelisted(Material material) {
        return config.getBlocksToBroadcast().contains(material);
    }

    /**
     * Check if OreBroadcast is active in a world
     *
     * @param world the world to check if OreBroadcast is active in
     * @return true if OreBroadcast is active in the world
     */
    public boolean isWorldWhitelisted(World world) {
        return isWorldWhitelisted(world.getName());
    }

    /**
     * Check if OreBroadcast is active in a world
     *
     * @param world the name of the world
     * @return true if OreBroadcast is active in the world
     */
    public boolean isWorldWhitelisted(String world) {
        return !config.isWorldWhitelistActive() || config.getWorldWhitelist().contains(world);
    }

}
