package com.xyberviri.amchat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class AMChatEvent extends PlayerChatEvent {
	public int freq;
	public int code;
	
	public AMChatEvent(Player player, String message, int freq, int code) {
		super(player, message);
		// TODO Auto-generated constructor stub
		this.freq = freq;
		this.code = code;
	}
	
	public int getFreq(){
		return this.freq;
	}
	public int getCode(){
		return this.code;
	}
	
	
}
