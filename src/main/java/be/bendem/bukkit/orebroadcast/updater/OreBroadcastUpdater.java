package be.bendem.bukkit.orebroadcast.updater;

import be.bendem.bukkit.orebroadcast.OreBroadcast;
import net.gravitydevelopment.updater.Updater;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

/**
 * @author bendem
 */
public class OreBroadcastUpdater {

    private final OreBroadcast plugin;
    private final File pluginFile;
    private Updater updater;
    private boolean isUpdateAvailable = false;
    private boolean isUpdated         = false;

    public OreBroadcastUpdater(OreBroadcast plugin, File pluginFile) {
        this.plugin = plugin;
        this.pluginFile = pluginFile;
    }

    public void checkUpdate(final CommandSender sender, final boolean download) {
        if(!isUpdateAvailable) {
            new UpdateCheck(plugin, sender, download).runTaskAsynchronously(plugin);
        }
    }

    public void downloadUpdate() {
        updater = new GravityUpdater(plugin, 72299, pluginFile, Updater.UpdateType.NO_VERSION_CHECK, true);
        isUpdated = true;
    }

    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    private class UpdateCheck extends BukkitRunnable {
        private final OreBroadcast  plugin;
        private final CommandSender sender;
        private final boolean download;

        public UpdateCheck(OreBroadcast plugin, CommandSender sender, boolean download) {
            this.plugin = plugin;
            this.sender = sender;
            this.download = download;
        }

        @Override
        public void run() {
            updater = new GravityUpdater(plugin, 72299, pluginFile, Updater.UpdateType.NO_DOWNLOAD, true);
            isUpdateAvailable = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
            if(isUpdateAvailable) {
                new UpdateNotifier(sender, download).runTask(plugin);
            }
        }
    }

    private class UpdateNotifier extends BukkitRunnable {
        private final CommandSender sender;
        private final boolean download;

        public UpdateNotifier(CommandSender sender, boolean download) {
            this.sender = sender;
            this.download = download;
        }

        @Override
        public void run() {
            if(sender == null) {
                if(download) {
                    plugin.getLogger().warning(updater.getLatestName() + " is available, downloading it...");
                    downloadUpdate();
                } else {
                    plugin.getLogger().warning(updater.getLatestName() + " is available, type '/ob update download' to download it");
                }
            } else {
                if(download) {
                    sender.sendMessage("Downloading update...");
                    downloadUpdate();
                } else {
                    sender.sendMessage("Update available");
                }
            }
        }
    }

}
