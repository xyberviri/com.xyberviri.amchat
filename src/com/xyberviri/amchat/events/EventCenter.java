package com.xyberviri.amchat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.xyberviri.amchat.AMChatRadio;

public class EventCenter {
	
	public static AMChatPlayerChat callAmChatEvent(Player sender,String message,boolean isRadio,int varFreq,int varCode){
	AMChatPlayerChat event = new AMChatPlayerChat (sender, message,isRadio,varFreq,varCode);
	Bukkit.getServer().getPluginManager().callEvent(event);
	return call(event);
	}
	
	public static AMChatRadioCreateEvent callAMChatRadioCreateEvent(Player creator,AMChatRadio radio){
	AMChatRadioCreateEvent event = new AMChatRadioCreateEvent(creator,radio);
	Bukkit.getServer().getPluginManager().callEvent(event);
	return call(event);
	}
	
	public static AMChatRadioEvent callAmChatRadioEvent(Player sender,String message,boolean isRadio,int varFreq,int varCode,AMChatRadio amcRadio){
	AMChatRadioEvent event = new AMChatRadioEvent (sender, message,isRadio,varFreq,varCode,amcRadio);
	Bukkit.getServer().getPluginManager().callEvent(event);
	return call(event);
	}	
	
	public static AMChatRadioJoinEvent callAMChatRadioJoinEvent(Player player,AMChatRadio radio){
	AMChatRadioJoinEvent event = new AMChatRadioJoinEvent(player,radio);
	Bukkit.getServer().getPluginManager().callEvent(event);
	return call(event);
	}
	
	public static AMChatRadioLoadEvent callAMChatRadioLoadEvent(AMChatRadio radio){
	AMChatRadioLoadEvent event = new AMChatRadioLoadEvent(radio);
	return call(event);
	}	
		
	public static AMChatEvent call(AMChatEvent newEvent){
		Bukkit.getServer().getPluginManager().callEvent(newEvent);
		return newEvent;		
	}
}
