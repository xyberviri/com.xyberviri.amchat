package com.xyberviri.amchat;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AMCTools {

	public AMCTools(){
	}
	
	public double getDistance(Location location1,Location location2){
		// This only accepts location objects. 
		double distanceBetween;

		// if (!(location1.getWorld() == location2.getWorld())){return -1.0f;}
		
		double x1 = location1.getX() + 0.01;
		double x2 = location2.getX() + 0.01;
		double y1 = location1.getY() + 0.01;
		double y2 = location2.getY() + 0.01;
		double z1 = location1.getZ() + 0.01;
		double z2 = location2.getZ() + 0.01;
		distanceBetween = Math.sqrt(((x2-x1)*(x2-x1))+((y2-y1)*(y2-y1))+((z2-z1)*(z2-z1)));
		return distanceBetween;
	}
	
	
	//Converts double to float and performs inverse square calculation. 
	//Divide the return by 1 to get the distance.
	double invSqrt(double y){
		float x = (float) y;	//this shouldn't be a problem unless there are very large maps
	    float xhalf = 0.5f*x;	
	    int i = Float.floatToIntBits(x); // get bits for floating value
	    i = 0x5f375a86- (i>>1); // gives initial guess y0
	    x = Float.intBitsToFloat(i); // convert bits back to float
	    x = x*(1.5f-xhalf*x*x); // Newton step, repeating increases accuracy
	    double z = (double) x;
	    return z;
	}
	  
	//This is a simple status message to the player were working on.
	public void msgToPlayer(Player player,String message){
		if (player instanceof Player){
			player.sendMessage(ChatColor.YELLOW + message);
		} else {
			AMChat.logMessage(message);
		}
	}
	public void msgToPlayer(Player player,String message,String message2){
		if (player instanceof Player){
			player.sendMessage(ChatColor.YELLOW + message+ChatColor.WHITE+message2);
		} else {
			AMChat.logMessage(message+message2);
		}
	}
		
	//This is a simple status message to the player were working on.
	public void errorToPlayer(Player player,String message){
		if (player instanceof Player){
			player.sendMessage(ChatColor.RED + message);
		} else {
			AMChat.logMessage(message);
		}
	}	

	
	//Formatter for Radio chat by broadcasting player
	public String createMessage(Player player,String message){
		String msgToReturn = AMChat.settings().getMsgFormat();		
		msgToReturn = msgToReturn.replace("%FREQ", AMChat.player(player).getFrequency().toString());
		msgToReturn = msgToReturn.replace("%CODE",String.format("%03d", AMChat.player(player).getSquelchCode().toString()));
		msgToReturn = msgToReturn.replace("%SENDER", player.getDisplayName());
		msgToReturn = msgToReturn.replace("%SUFFIX", AMChat.settings().getRadioSuffix());
		msgToReturn = msgToReturn.replace("%MESSAGE", message);
		return msgToReturn;
	}


	
	public String scrambleString(String inputword) {
		inputword = inputword.toLowerCase();
		//remove all non alpha and alpha eaton rishd should eliminate 65% of all English.
		String word = inputword.replaceAll("[\\d\\s\\Weatonrishd]", "");
		if(word.length() == 0){return "";}
		
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
}
