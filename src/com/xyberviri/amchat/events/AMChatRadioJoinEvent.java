package com.xyberviri.amchat.events;

import org.bukkit.entity.Player;

import com.xyberviri.amchat.AMChatRadio;

public class AMChatRadioJoinEvent extends AMChatRadioCreateEvent{

	public AMChatRadioJoinEvent(Player who, AMChatRadio amcRadio) {
		super(who, amcRadio);
	}

}
