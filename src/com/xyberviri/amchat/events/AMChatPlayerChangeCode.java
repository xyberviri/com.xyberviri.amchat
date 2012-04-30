package com.xyberviri.amchat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
 

public class AMChatPlayerChangeCode extends PlayerEvent  {
	private static final HandlerList handlers = new HandlerList();
	private int varValue;
	
	public AMChatPlayerChangeCode(Player player,int varValue) {
		super(player);
		this.varValue = varValue;
	}
	
	public int getValue (){return this.varValue;}
	//Event handler lists
	public HandlerList getHandlers(){return handlers;}
	public static HandlerList getHandlerList(){return handlers;}
}
