package com.xyberviri.amchat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class AMChatPlayerChangeFrequency extends AMChatPlayerChangeCode  {
	public AMChatPlayerChangeFrequency(Player player,int varValue) {
		super(player, varValue);
	}
}
