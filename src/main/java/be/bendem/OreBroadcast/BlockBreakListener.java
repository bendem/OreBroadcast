package be.bendem.OreBroadcast;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.GameMode;

import java.util.List;

public class BlockBreakListener implements Listener {

    public OreBroadcast plugin;

    BlockBreakListener(OreBroadcast plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Reject creative users
        if(event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        plugin.logger.info(event.getBlock().getType().name());

        List<String> blocksToBroadcast = plugin.getConfig().getStringList("Ores");

        for (int i = 0; i < blocksToBroadcast.size(); ++i) {
            blocksToBroadcast.set(i, blocksToBroadcast.get(i).toUpperCase() + "_ORE");
            // If redstone is in the config, add glowing redstone as well
            if(blocksToBroadcast.get(i) == "REDSTONE_ORE") {
                blocksToBroadcast.add("GLOWING_REDSTONE_ORE");
            }
        }

        if(blocksToBroadcast.contains(event.getBlock().getType().name())) {
            plugin.logger.info("A block to broadcast broke...");
        }
    }

}
