package com.xyberviri.amchat.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.xyberviri.amchat.AMChatRadio;

public class AMChatRadioLoadEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private AMChatRadio amcRadio;
	
	public AMChatRadioLoadEvent(AMChatRadio amcRadio){
		this.amcRadio= amcRadio;
	}
	
	public AMChatRadio getRadio(){
		return this.amcRadio;
	}

	public HandlerList getHandlers(){return handlers;}
	public static HandlerList getHandlerList(){return handlers;}

}
