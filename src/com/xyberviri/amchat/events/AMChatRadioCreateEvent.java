package com.xyberviri.amchat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.xyberviri.amchat.AMChatRadio;

public class AMChatRadioCreateEvent extends PlayerEvent{
	private static final HandlerList handlers = new HandlerList();
	private AMChatRadio amcRadio;

	public AMChatRadioCreateEvent(Player who,AMChatRadio amcRadio) {
		super(who);
		this.amcRadio = amcRadio;
	}
	
	//Return the radio that was just created
	public AMChatRadio getRadio(){return this.amcRadio;}
		
	public HandlerList getHandlers(){return handlers;}
	public static HandlerList getHandlerList(){return handlers;}

}
