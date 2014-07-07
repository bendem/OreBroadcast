package be.bendem.bukkit.orebroadcast;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * @author bendem
 */
public class PlayerLoginListener implements Listener {

    private final OreBroadcast plugin;

    public PlayerLoginListener(OreBroadcast plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if(e.getPlayer().hasPermission("ob.update.notification")
                && plugin.isUpdateAvailable()
                && !plugin.isUpdated()) {
            e.getPlayer().sendMessage(
                "[" + ChatColor.BLUE + plugin.getName() + ChatColor.RESET +
                "] An update is available type /ob update download to download it"
            );
        }
    }

}
