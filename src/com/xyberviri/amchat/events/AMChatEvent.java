package com.xyberviri.amchat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerChatEvent;

public class AMChatEvent extends PlayerChatEvent {
	private static final HandlerList handlers = new HandlerList();
	private int freq;
	private int code;
	private boolean isRadio;
	
	public AMChatEvent(Player player, String message, boolean isRadio, int freq, int code) {
		super(player, message);
		// TODO Auto-generated constructor stub
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
}
