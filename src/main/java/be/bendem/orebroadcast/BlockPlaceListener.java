package be.bendem.orebroadcast;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlockPlaceListener implements Listener {

    public OreBroadcast plugin;

    BlockPlaceListener(OreBroadcast plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if(plugin.blocksToBroadcast.contains(block.getType().name())
                && !plugin.broadcastBlacklist.contains(block)) {
            plugin.broadcastBlacklist.add(block);
        }
    }

}
