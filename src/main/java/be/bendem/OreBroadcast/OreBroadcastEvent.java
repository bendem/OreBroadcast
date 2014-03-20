/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package be.bendem.OreBroadcast;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author cnaude
 */
public class OreBroadcastEvent extends Event {

    private static final HandlerList handlers = new HandlerList(); 
    
    private final String message;
    private final Player player;

    /**
     *
     * @param message
     * @param player
     */
    public OreBroadcastEvent(String message, Player player) {
        this.message = message;
        this.player = player;
    }

    /**
     *
     * @return
     */
    public String getDeathMessage() {
        return this.message;
    }
       
    /**
     *
     * @return
     */
    public Player getPlayer() {
        return this.player;
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
}
