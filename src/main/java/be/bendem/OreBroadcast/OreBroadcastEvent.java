package be.bendem.OreBroadcast;

import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Broadcast event for OreBroadcast messages.
 *
 * @author cnaude
 */
public class OreBroadcastEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private String message;
    private Player player;
    private boolean cancel = false;
    private Set<Player> recipients;

    /**
     *
     * @param message the formatted message
     * @param player the player that triggered the event
     * @param recipients
     */
    public OreBroadcastEvent(String message, Player player, Set<Player> recipients) {
        this.message = message;
        this.player = player;
        this.recipients = recipients;
    }

    /**
     * Gets a set of recipients that this message will be displayed to
     *     
     * @return the players that receive this message
     */
    public Set<Player> getRecipients() {
        return recipients;
    }
    
    /**
     * Sets of recipients that this message will be displayed to
     *     
     * @param recipients the players that receive this message
     */
    public void setRecipients(Set<Player> recipients) {
        this.recipients = recipients;
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
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
