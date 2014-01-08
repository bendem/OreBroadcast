package be.bendem.OreBroadcast;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlockBreakListener implements Listener {

    public OreBroadcast plugin;

    BlockBreakListener(OreBroadcast plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        plugin.logger.info("A block just broke...");
    }

}
