package com.xyberviri.amchat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.xyberviri.amchat.events.AMChatPlayerChat;

public class EventCenter {	
	
	public static AMChatPlayerChat callPlayerChatEvent(Player sender,String message,boolean isRadio,int varFreq,int varCode){
	AMChatPlayerChat event = new AMChatPlayerChat (sender, message,isRadio,varFreq,varCode);
	Bukkit.getServer().getPluginManager().callEvent(event);
	return event;
	}
	

		

}
