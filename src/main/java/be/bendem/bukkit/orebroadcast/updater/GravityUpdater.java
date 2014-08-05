package be.bendem.bukkit.orebroadcast.updater;

import net.gravitydevelopment.updater.Updater;
import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * @author bendem
 */
public class GravityUpdater extends Updater {

    public GravityUpdater(Plugin plugin, int id, File file, UpdateType type, boolean announce) {
        super(plugin, id, file, type, announce);
    }

    @Override
    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        return normalize(localVersion).compareTo(normalize(remoteVersion)) < 0;
    }

    private String normalize(String version) {
        int dots = StringUtils.countMatches(version, ".");
        while(dots < 2) {
            version += ".0";
            ++dots;
        }
        return version;
    }

}
