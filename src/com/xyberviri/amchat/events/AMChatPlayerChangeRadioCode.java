package com.xyberviri.amchat.events;

import org.bukkit.entity.Player;

import com.xyberviri.amchat.AMChatRadio;

public class AMChatPlayerChangeRadioCode extends AMChatRadioCreateEvent {
	private int varValue;
	public AMChatPlayerChangeRadioCode(Player who, AMChatRadio amcRadio,int varValue) {
		super(who, amcRadio);
		this.varValue = varValue;
	}
	public int getValue(){
		return this.varValue;
	}
}
