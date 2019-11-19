package com.xyberviri.amchat.events;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class AMChatEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private static final Set<Player> recipients = null;
	private int freq;
	private int code;
	private boolean isRadio;
	private boolean cancelled;
	private String message;
	
	public AMChatEvent(Player player, String message, boolean isRadio, int freq, int code) {
		super(player);
		this.isRadio = isRadio;
		this.freq = freq;
		this.code = code;
	}
	//is this Radio Chat?
	public boolean isRadioChat(){return this.isRadio;}
	//Frequency player transmitted this on
	public int getFreq(){return this.freq;}
	//Code this message is encrypted with
	public int getCode(){return this.code;}
	//Event handler lists
	public HandlerList getHandlers(){return handlers;}
	public static HandlerList getHandlerList(){return handlers;}
	
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
		
	}
	public String getMessage() {
		return this.message;
	}
	public void setMessage(String message) {
		this.message=message;
	}
}
