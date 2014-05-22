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
        Player player = event.getPlayer();
        // Reject creative users, users without ob.broadcast permission
        // and user in a non whitelisted world
        if(player.getGameMode() != GameMode.SURVIVAL
                || !player.hasPermission("ob.broadcast")
                || !plugin.isWorldWhitelisted(player.getWorld().getName())) {
            return;
        }

        Block block = event.getBlock();
        // Don't broadcast the blocks which has already been broadcasted
        // or which have been placed by a player
        if(plugin.isBlackListed(block)) {
            plugin.unBlackList(block);
            return;
        }

        // Measuring event time
        long timer = System.currentTimeMillis();

        if(plugin.isWhitelisted(block.getType())) {
            String blockName;
            if(block.getType() == Material.GLOWING_REDSTONE_ORE) {
                blockName = "redstone";
            } else {
                blockName = block.getType().name().toLowerCase().replace("_ore", "");
            }

            int veinSize = getVeinSize(block);
            String color = plugin.getConfig().getString("colors." + blockName, "white").toUpperCase();
            String formattedMessage = format(
                plugin.getConfig().getString("message", "{player} just found {count} block{plural} of {ore}"),
                player,
                String.valueOf(veinSize),
                blockName,
                color,
                veinSize > 1
            );
            broadcast(player, formattedMessage);
        }

        plugin.getLogger().finer("Event duration : " + (System.currentTimeMillis() - timer) + "ms");
    }

    private int getVeinSize(Block block) {
        Set<Block> vein = new HashSet<>();
        vein.add(block);
        vein = getVein(block, vein);
        plugin.blackList(vein);
        plugin.unBlackList(block);

        return vein.size();
    }

    private Set<Block> getVein(Block block, Set<Block> vein) {
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

    private String format(String msg, Player player, String count, String ore, String color, boolean plural) {
        return colorize(msg
            .replace("{player_name}", player.getDisplayName())
            .replace("{real_player_name}", player.getName())
            .replace("{world}", player.getWorld().getName())
            .replace("{count}", count)
            .replace("{ore}", translateOre(ore, color))
            .replace("{ore_color}", "&" + ChatColor.valueOf(color).getChar())
            .replace("{plural}", plural ? plugin.getConfig().getString("plural", "s") : ""));
    }

    private String translateOre(String ore, String color) {
        return "&" + ChatColor.valueOf(color).getChar()
            + plugin.getConfig().getString("ore-translations." + ore, ore);
    }

    private String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
