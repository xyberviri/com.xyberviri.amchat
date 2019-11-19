package com.xyberviri.amchat;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AMCTools {
	AMChat amcMain;
	


	
	public AMCTools(AMChat amChat){
		this.amcMain = amChat;
	}
	

	
	
	public double getDistance(Location location1,Location location2){
		// This only accepts location objects. 
		double distanceBetween;
		double x1 = location1.getX() + 0.01;
		double x2 = location2.getX() + 0.01;
		double z1 = location1.getZ() + 0.01;
		double z2 = location2.getZ() + 0.01;
		
		if(AMChat.amcChatMain.varUse3dDistances) {
			double y1 = location1.getY() + 0.01;
			double y2 = location2.getY() + 0.01;
			distanceBetween = Math.sqrt(((x2-x1)*(x2-x1))+((y2-y1)*(y2-y1))+((z2-z1)*(z2-z1)));	
		} else {
			distanceBetween = Math.sqrt(((x2-x1)*(x2-x1))+((z2-z1)*(z2-z1)));
		}
		
		return distanceBetween;
	}
	  
	//This is a simple status message to the player were working on.
	public void msgToPlayer(Player player,String message){
		if (player instanceof Player){
			player.sendMessage(ChatColor.YELLOW + message);
		} else {
			amcMain.amcLogger.info(message);
		}
	}
	public void msgToPlayer(Player player,String message,String message2){
		if (player instanceof Player){
			player.sendMessage(ChatColor.YELLOW + message+ChatColor.WHITE+message2);
		} else {
			amcMain.amcLogger.info(message+message2);
		}
	}
		
	//This is a simple status message to the player were working on.
	public void errorToPlayer(Player player,String message){
		if (player instanceof Player){
			player.sendMessage(ChatColor.RED + message);
		} else {
			amcMain.amcLogger.info(message);
		}
	}	

	
	//Formatter for Radio chat by broadcasting player
	public String createMessage(Player player,String message){
		String msgToReturn = amcMain.varMsgFormat;		
		msgToReturn = msgToReturn.replace("%FREQ", Integer.toString(amcMain.getPlayerRadioChannel(player)));
		msgToReturn = msgToReturn.replace("%CODE",String.format("%03d", amcMain.getPlayerRadioCode(player)));
		msgToReturn = msgToReturn.replace("%SENDER", player.getDisplayName());
		msgToReturn = msgToReturn.replace("%SUFFIX", amcMain.varRadioFreqSuffix);
		msgToReturn = msgToReturn.replace("%MESSAGE", message);
		return msgToReturn;
	}

	//encrypted Formatter for Radio chat by broadcasting player
	public String createBadMessage(String message){
		String msgToReturn = amcMain.varMsgFormat;		
		msgToReturn = msgToReturn.replace("%FREQ","??");
		msgToReturn = msgToReturn.replace("%CODE","???");
		msgToReturn = msgToReturn.replace("%SENDER", "??");
		msgToReturn = msgToReturn.replace("%SUFFIX", amcMain.varRadioFreqSuffix);
		msgToReturn = msgToReturn.replace("%MESSAGE",scrambleString(message));
		return msgToReturn;
	}
	
	public String scrambleString(String inputword) {
		inputword = inputword.toLowerCase();
		//remove all non alpha and alpha eaton rishd should eliminate 65% of all English.
		//the \W will remove all should remove all non English alpha.
		String word = inputword.replaceAll("[\\d\\s\\Weatonrishd]", "");
	    StringBuilder builder = new StringBuilder(word.length());
	    boolean[] used = new boolean[word.length()];
	    for (int i = 0; i < word.length(); i++) {
	        int rndIndex;
	        do {
	            rndIndex = new Random().nextInt(word.length());
	        } while (used[rndIndex]);
	        used[rndIndex] = true;
	        builder.append(word.charAt(rndIndex));
	    }
	    	   builder.append("...");
	    return builder.toString();
	}	
	
	public String formatSaveFix(String string){
		string = string.replaceAll("\u00A7([0-9a-f])", "&$1");
		return string;
	}
	public String formatLoadFix(String string){
		string = string.replaceAll("&([0-9a-f])", "\u00a7$1");
		return string;
	}
	

	
	
	//returns true if we are loaded from the main plugin. 
	public boolean isLoaded(AMChat amcMainPlugin) {
		if (this.amcMain.equals(amcMainPlugin)){
			return true;
			}
		return false;
	}
}
