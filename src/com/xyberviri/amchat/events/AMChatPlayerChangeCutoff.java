package com.xyberviri.amchat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class AMChatPlayerChangeCutoff extends AMChatPlayerChangeCode {
	public AMChatPlayerChangeCutoff(Player player,int varValue) {
		super(player, varValue);
	}
}
