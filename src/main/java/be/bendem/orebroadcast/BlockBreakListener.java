package be.bendem.orebroadcast;

import org.bukkit.block.Block;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.GameMode;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

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

        Block block = event.getBlock();
        // Don't broadcast the blocks which has already been broadcasted
        // or which have been placed by a player
        if(plugin.broadcastBlacklist.contains(block)) {
            plugin.broadcastBlacklist.remove(block);
            plugin.logger.finer("Block in blackList : " + plugin.broadcastBlacklist.size());
            return;
        }

        // Measuring event time
        long timer = System.currentTimeMillis();

        String blockName = (block.getType() == Material.GLOWING_REDSTONE_ORE ? "redstone" :
            block.getType().name().toLowerCase().replace("_ore", ""));

        if(plugin.blocksToBroadcast.contains(block.getType().name())) {
            int veinSize = getVeinSize(block);
            String color = plugin.getConfig().getString("colors." + blockName, "white").toUpperCase();
            String formattedMessage = format(
                plugin.getConfig().getString("message", "{player} just found {count} block{plural} of {ore}"),
                event.getPlayer(),
                Integer.toString(veinSize),
                blockName,
                color,
                veinSize > 1
            );
            broadcast(event.getPlayer(), formattedMessage);
        }

        plugin.logger.finer("Block in blackList : " + plugin.broadcastBlacklist.size());
        plugin.logger.finer("Event duration : " + (System.currentTimeMillis() - timer) + "ms");
    }

    private final int getVeinSize(Block block) {
        HashSet<Block> vein = new HashSet<>();
        vein.add(block);
        vein = getVein(block, vein);
        plugin.broadcastBlacklist.addAll(vein);
        plugin.broadcastBlacklist.remove(block);

        return vein.size();
    }

    private final HashSet<Block> getVein(Block block, HashSet<Block> vein) {
        int i, j, k;
        for (i = -1; i < 2; ++i) {
            for (j = -1; j < 2; ++j) {
                for (k = -1; k < 2; ++k) {
                    if(vein.contains(block.getRelative(i, j, k))           // block already found
                           || !compare(block, block.getRelative(i, j, k))  // block has not the same type
                           || (i == 0 && j == 0 && k == 0)) {              // comparing block to itself
                        // Recursion end!
                        continue;
                    }
                    vein.add(block.getRelative(i, j, k));
                    vein = getVein(block.getRelative(i, j, k), vein);
                }
            }
        }

        return vein;
    }

    private boolean compare(Block block1, Block block2) {
        return block1.getType().equals(block2.getType())
            || block1.getType() == Material.GLOWING_REDSTONE_ORE && block2.getType() == Material.REDSTONE_ORE
            || block1.getType() == Material.REDSTONE_ORE && block2.getType() == Material.GLOWING_REDSTONE_ORE;
    }

    private void broadcast(Player player, String message) {
        Set<Player> recipients = new HashSet<>();
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if(player.hasPermission("ob.receive")) {
                recipients.add(onlinePlayer);
            }
        }
        OreBroadcastEvent event = new OreBroadcastEvent(message, player, recipients);
        plugin.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            for (Player recipient : event.getRecipients()) {
                recipient.sendMessage(event.getMessage());
            }
        }
    }

    private final String format(String msg, Player player, String count, String ore, String color, boolean plural) {
        return colorize(msg
            // DEPRECATED Use player_name instead
            .replace("{player}", player.getDisplayName())
            .replace("{player_name}", player.getDisplayName())
            .replace("{real_player_name}", player.getName())
            .replace("{count}", count)
            .replace("{ore}", translateOre(ore, color))
            .replace("{ore_color}", "&" + ChatColor.valueOf(color).getChar())
            .replace("{plural}", plural ? plugin.getConfig().getString("plural", "s") : ""));
    }

    private final String translateOre(String ore, String color) {
        return "&" + ChatColor.valueOf(color).getChar()
            + plugin.getConfig().getString("ore-translations." + ore, ore);
    }

    private final String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
