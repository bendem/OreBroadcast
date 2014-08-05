package be.bendem.bukkit.orebroadcast.handlers;

import be.bendem.bukkit.orebroadcast.OreBroadcast;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private final OreBroadcast plugin;

    public BlockPlaceListener(OreBroadcast plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        if(plugin.isWhitelisted(block.getType()) && plugin.isWorldWhitelisted(block.getWorld().getName()) && !plugin.isBlackListed(block)
                && (e.getPlayer().getGameMode() != GameMode.CREATIVE
                    || !plugin.getConfig().getBoolean("broadcast-creative-placed-blocks", true))) {
            plugin.blackList(block);
        }
    }

}
