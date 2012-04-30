package com.xyberviri.amchat.events;

import org.bukkit.entity.Player;

import com.xyberviri.amchat.AMChatRadio;

public class AMChatPlayerChangeRadioPassword extends AMChatRadioCreateEvent {
	private String varValue;

	public AMChatPlayerChangeRadioPassword(Player who, AMChatRadio amcRadio, String varValue) {
		super(who, amcRadio);
		this.varValue = varValue;
	}	
	public String getValue(){return this.varValue;}
}
