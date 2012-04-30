package com.xyberviri.amchat.events;

import org.bukkit.entity.Player;

import com.xyberviri.amchat.AMChatRadio;

public class AMChatPlayerChangeRadioFrequency extends AMChatPlayerChangeRadioCode {

	public AMChatPlayerChangeRadioFrequency(Player who, AMChatRadio amcRadio, int varValue) {
		super(who, amcRadio, varValue);
	}
}
