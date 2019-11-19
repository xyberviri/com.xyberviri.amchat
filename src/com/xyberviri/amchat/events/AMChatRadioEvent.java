package com.xyberviri.amchat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.xyberviri.amchat.AMChatRadio;

public class AMChatRadioEvent extends AMChatEvent {
	//private static final HandlerList handlers = new HandlerList();
	private AMChatRadio amcRadio;

	public AMChatRadioEvent(Player player, String message, boolean isRadio,int freq, int code,AMChatRadio amcRadio) {
		super(player, message, isRadio, freq, code);
		this.amcRadio = amcRadio;
	}
	
	public AMChatRadio getRadio(){return this.amcRadio;}
	//Event handler lists
	//public HandlerList getHandlers(){return handlers;}
	//public static HandlerList getHandlerList(){return handlers;}
}
