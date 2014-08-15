package be.bendem.bukkit.orebroadcast;

import be.bendem.bukkit.orebroadcast.updater.OreBroadcastUpdater;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;
import org.mcstats.Metrics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/* package */ class Config {

    private static final String PLAYER_FILE = "players.dat";
    private final OreBroadcast plugin;
    private final Set<SafeBlock> broadcastBlacklist   = new HashSet<>();
    private final Set<Material>  blocksToBroadcast    = new HashSet<>();
    private final Set<String>    worldWhitelist       = new HashSet<>();
    private final Set<UUID>      optOutPlayers        = new HashSet<>();
    private       boolean        worldWhitelistActive = false;
    private       boolean        metricsActive        = true;
    private OreBroadcastUpdater updater;
    private Metrics             metrics;

    /* package */ Config(OreBroadcast plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
    }

    /* package */ void loadConfig() {
        plugin.reloadConfig();
        // Create the list of materials to broadcast from the file
        List<String> configList = plugin.getConfig().getStringList("ores");
        blocksToBroadcast.clear();

        for(String item : configList) {
            Material material = Material.getMaterial(item.toUpperCase() + "_ORE");
            blocksToBroadcast.add(material);
            // Handle glowing redstone ore (id 74) and redstone ore (id 73)
            if(material.equals(Material.REDSTONE_ORE)) {
                blocksToBroadcast.add(Material.GLOWING_REDSTONE_ORE);
            }
        }

        // Load world whitelist
        worldWhitelist.clear();
        worldWhitelistActive = plugin.getConfig().getBoolean("active-per-worlds", true);
        if(worldWhitelistActive) {
            worldWhitelist.addAll(plugin.getConfig().getStringList("active-worlds"));
        }

        // Handling metrics changes
        boolean prev = metricsActive;
        metricsActive = plugin.getConfig().getBoolean("metrics", true);
        if(prev != metricsActive) {
            if(metricsActive) {
                startMetrics();
            } else {
                stopMetrics();
            }
        }

        // Updater thingy
        updater = new OreBroadcastUpdater(plugin, plugin.getJar());
        if(plugin.getConfig().getBoolean("updater.startup-check", true)) {
            updater.checkUpdate(null, false);
        }
        if(plugin.getConfig().getBoolean("updater.warn-ops", true)) {
            plugin.getServer().getPluginManager().registerEvents(new PlayerLoginListener(plugin), plugin);
        }

        // Load opt out players
        optOutPlayers.clear();
        try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(new File(plugin.getDataFolder(), PLAYER_FILE)))) {
            Object o = stream.readObject();
            if(o instanceof Set<?>) {
                optOutPlayers.addAll((Set<UUID>) o);
            }
        } catch(IOException | ClassNotFoundException e) {
            plugin.getLogger().severe("Failed to read opt out players from file");
            e.printStackTrace(System.err);
        }
    }

    /* package */ boolean isOptOut(UUID uuid) {
        return optOutPlayers.contains(uuid);
    }

    /* package */ void optOutPlayer(UUID uuid) {
        optOutPlayers.add(uuid);
        saveOptOutPlayers();
    }

    /* package */ void unOptOutPlayer(UUID uuid) {
        optOutPlayers.remove(uuid);
        saveOptOutPlayers();
    }

    private void saveOptOutPlayers() {
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(new File(plugin.getDataFolder(), PLAYER_FILE)))) {
            stream.writeObject(optOutPlayers);
        } catch(IOException e) {
            plugin.getLogger().severe("Failed to write opt out players to file");
            e.printStackTrace(System.err);
        }
    }

    /* package */ void startMetrics() {
        if(metrics == null) {
            try {
                metrics = new Metrics(plugin);
            } catch(IOException e) {
                plugin.getLogger().warning("Couldn't activate metrics :(");
                return;
            }
        }
        metrics.start();
    }

    /* package */ void stopMetrics() {
        if(metrics == null) {
            return;
        }
        // This is temporary while waiting for https://github.com/Hidendra/Plugin-Metrics/pull/43
        try {
            Field taskField = metrics.getClass().getDeclaredField("task");
            taskField.setAccessible(true);
            BukkitTask task = (BukkitTask) taskField.get(metrics);
            if(task != null) {
                task.cancel();
            }
        } catch(NoSuchFieldException | IllegalAccessException e) {
            plugin.getLogger().log(Level.WARNING, "Error while stopping metrics, please report this to the plugin author", e);
        }
    }

    /* package */ Set<SafeBlock> getBroadcastBlacklist() {
        return broadcastBlacklist;
    }

    /* package */ Set<Material> getBlocksToBroadcast() {
        return blocksToBroadcast;
    }

    /* package */ Set<String> getWorldWhitelist() {
        return worldWhitelist;
    }

    /* package */ boolean isWorldWhitelistActive() {
        return worldWhitelistActive;
    }

    /* package */ OreBroadcastUpdater getUpdater() {
        return updater;
    }

}
