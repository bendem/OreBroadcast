package be.bendem.bukkit.orebroadcast;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

/**
 * SafeBlock contains informations about a block but doesn't prevents world
 * unloading (it doesn't contain informations about the server's worlds)
 * @author bendem
 */
public class SafeBlock {

    public final int    x;
    public final int    y;
    public final int    z;
    public final String world;

    public SafeBlock(Block block) {
        this(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
    }

    public SafeBlock(int x, int y, int z, String world) {
        Validate.notNull(world);
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public Block getBlock() {
        return Bukkit.getWorld(world).getBlockAt(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof SafeBlock)) {
            return false;
        }
        SafeBlock safeBlock = (SafeBlock) o;
        return x == safeBlock.x
                && y == safeBlock.y
                && z == safeBlock.z
                && world.equals(safeBlock.world);
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        result = 31 * result + world.hashCode();
        return result;
    }

}
