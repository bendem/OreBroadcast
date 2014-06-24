package be.bendem.orebroadcast;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

/**
 * Broadcast event for OreBroadcast messages.
 *
 * @author cnaude
 */
public class OreBroadcastEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private String message;
    private Player source;
    private boolean cancelled = false;
    private Set<Player> recipients;

    /**
     * Broadcast event for OreBroadcast messages.
     *
     * @param message the formatted message
     * @param source the player that mined the block
     * @param recipients the players that will receive this message
     */
    public OreBroadcastEvent(String message, Player source, Set<Player> recipients) {
        this.message = message;
        this.source = source;
        this.recipients = recipients;
    }

    /**
     * Gets the recipients that this message will be sent to.
     *
     * @return the players that will receive this message
     */
    public Set<Player> getRecipients() {
        return recipients;
    }

    /**
     * Sets the recipients that this message will be sent to.
     *
     * @param recipients the players that will receive this message
     */
    public void setRecipients(Set<Player> recipients) {
        this.recipients = recipients;
    }

    /**
     * Gets the formatted message which will be sent.
     *
     * @return the formatted message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Sets the message which will be sent.
     *
     * @param message the new message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the player.
     *
     * @return
     * @deprecated Renamed, see {@link OreBroadcastEvent#getSource()}
     */
    @Deprecated
    public Player getPlayer() {
        return this.source;
    }

    /**
     * Sets the player.
     *
     * @param player
     * @deprecated Removed, this had no effects anyway
     */
    @Deprecated
    public void setPlayer(Player player) {
        throw new UnsupportedOperationException("This method is deprecated and does nothing!");
    }

    /**
     * Gets the player who mined the block.
     *
     * @return the player who mined the block
     */
    public Player getSource() {
        return this.source;
    }

    /**
     * Gets the cancellation state of the event.
     *
     * @return true if the event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of the event
     *
     * @param cancel the new cancellation state
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
