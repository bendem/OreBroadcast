package be.bendem.OreBroadcast;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Broadcast event for oraBroadcast.
 *
 * @author cnaude
 */
public class OreBroadcastEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList(); 
    
    private String message;
    private Player player;
    private boolean cancelled;

    /**
     *
     * @param message the formatted message
     * @param player the player that triggered the event
     */
    public OreBroadcastEvent(String message, Player player) {
        this.message = message;
        this.player = player;
    }

    /**
     * Get the formatted message.
     *
     * @return
     */
    public String getMessage() {
        return this.message;
    }
    
    /**
     * Set the message.
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }
       
    /**
     * Get the player.
     *
     * @return
     */
    public Player getPlayer() {
        return this.player;
    }
    
    /**
     * Set the player.
     *
     * @param player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     *
     * @return
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     *
     * @return
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
         return cancelled;
    }

    @Override
    public void setCancelled(boolean bln) {
         this.cancelled = bln;
    }
}
