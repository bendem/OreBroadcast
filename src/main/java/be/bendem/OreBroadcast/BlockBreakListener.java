package be.bendem.OreBroadcast;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.GameMode;
import org.bukkit.Material;

import java.util.ArrayList;

public class BlockBreakListener implements Listener {

    public OreBroadcast plugin;

    BlockBreakListener(OreBroadcast plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Reject creative users and users without ob.broadcast permission
        if(event.getPlayer().getGameMode() != GameMode.SURVIVAL
           || !event.getPlayer().hasPermission("ob.broadcast")) {
            return;
        }

        // Create the list of blocks to broadcast from the file
        ArrayList<String> blocksToBroadcast = new ArrayList<String>(plugin.getConfig().getStringList("Ores"));
        for (int i = 0; i < blocksToBroadcast.size(); ++i) {
            blocksToBroadcast.set(i, blocksToBroadcast.get(i).toUpperCase() + "_ORE");
            // Handle glowing redstone ore (id 74) and redstone ore (id 73)
            if(blocksToBroadcast.get(i).equals("REDSTONE_ORE")) {
                plugin.logger.info(blocksToBroadcast.get(i));
                blocksToBroadcast.add("GLOWING_REDSTONE");
            }
        }

        if(blocksToBroadcast.contains(event.getBlock().getType().name())) {
            // TODO broadcast message to all players with ob.receive permission
            plugin.logger.info("----------------------------------------------------------");
            plugin.logger.info(getVeinSize(event.getBlock()) + " block of the same type near this block");
            plugin.logger.info("A block to broadcast broke : " + event.getBlock().getType().name());
            plugin.logger.info("----------------------------------------------------------");
        }
    }

    public final int getVeinSize(Block block) {
        ArrayList<Block> vein = new ArrayList<Block>();
        vein.add(block);
        return getVein(block, vein).size();
    }

    public final ArrayList<Block> getVein(Block block, ArrayList<Block> vein) {
        int i, j, k;
        for (i = -1; i < 2; ++i) {
            for (j = -1; j < 2; ++j) {
                for (k = -1; k < 2; ++k) {
                    if(vein.contains(block.getRelative(i, j, k))       // block already found
                       || !compare(block, block.getRelative(i, j, k))  // block has not the same type
                       || (i == 0 && j == 0 && k == 0)) {              // comparing block to itself
                        // Recursion end!
                        continue;
                    }
                    vein.add(block.getRelative(i, j, k));
                    vein = getVein(vein.get(vein.size() - 1), vein);
                }
            }
        }

        return vein;
    }

    public boolean compare(Block block1, Block block2) {
        return block1.getType().equals(block2.getType())
            || block1.getType() == Material.GLOWING_REDSTONE_ORE && block2.getType() == Material.REDSTONE_ORE
            || block1.getType() == Material.REDSTONE_ORE && block2.getType() == Material.GLOWING_REDSTONE_ORE;
    }

}
