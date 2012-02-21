package com.xyberviri.amchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class AMChat extends JavaPlugin {
	
	AMCTools amcTools;						//tools for AMChat
	AMChatRouter amcRouter;					//Chat router is responsible for deciding what to do with player chat events.
	AMChatListener amcListener;				//Chat Listener
	AMChatCmd amcCmd;						//AMC Command control
	PluginDescriptionFile amcPdf;
	FileConfiguration amcConfig;
	
	final Logger amcLogger = Logger.getLogger("Minecraft");
	
	// Settings
	String varMsgFormat = ChatColor.DARK_GREEN+"["+ChatColor.GOLD+"%FREQ.%CODE"+ChatColor.GRAY+"%SUFFIX"+ChatColor.DARK_GREEN+"]"+ChatColor.YELLOW+"%SENDER"+ChatColor.WHITE+": %MESSAGE";
	String varRadioFreqSuffix = "rHz"; 		// frequency string name
	
	//these are double because we calculate the distance versus this, and that variable is a double
	double varPlayerMaxChatDist = 64;		// The Maximum Distance local chat will reach.
	double varRadioMaxChatDist = 160;		// The Maximum Distance Radio chat will reach.
	
	boolean varLimitPlayerChat = true;	// Should we limit the distance that non radio chat can reach?
	boolean varLimitRadioChat = false;	// Should we limit the distance that a personal communicator can reach?
	
	double varRadioSkyWaveMod = 2;			// This distance to modify the chat distance for radios at night.
	boolean varSkyWaveEnabled = false;	// Is SkyWave Effect enabled?
	
	boolean varRadioAutoOn = true;		// if we should automatically turn a players radio on
	int varRadioDefFreq = 64;			// The first time a invalid frequency is returned, return this instead. this is also the /am home channel 
	int varRadioMinFreq = 32;			// this is the lowest frequency we can set for transmitting
	int varRadioMaxFreq = 512;			// this is the highest frequency we can set for transmitting
	int varRadioMaxCuttoff = 15;
	int varRadioMinCode = 0;			// this is the minimum valid code key, 0 = disabled; This really shouldn't be changed.
	int varRadioMaxCode = 999;			// max value encryption key we will use for transmission.
	
	ArrayList<String>    playerRadioOn = new ArrayList<String>();				// List of players with Radios On
	Map<Player, Integer> playerRadioChannel = new HashMap<Player, Integer>(); 	// player radio channel
	Map<Player, Integer> playerRadioCode = new HashMap<Player, Integer>(); 		// Player radio encoding key
	Map<Player, Boolean> playerRadioMic = new HashMap<Player, Boolean>();		// Player radio broadcasting enabled	
	Map<Player, Boolean> playerRadioFilter = new HashMap<Player, Boolean>();	// Player radio filter blocks encrypted chat that is otherwise unreadable
	Map<Player, Integer> playerRadioCutoff = new HashMap<Player, Integer>();	// Player radio cutoff blocks other channels, i.e. cross talk, we might make this a integer later
	Map<Player, String>  playerRadioPing = new HashMap<Player,String>();		// This is the last player that sent a  ping
	
	public void loadSettings(){
		this.varMsgFormat = amcConfig.getString("radio-format", varMsgFormat);
		this.varRadioFreqSuffix = amcConfig.getString("radio-suffix",varRadioFreqSuffix);
		this.varPlayerMaxChatDist = amcConfig.getDouble("chat-distance",varPlayerMaxChatDist);
		this.varRadioMaxChatDist = amcConfig.getDouble("radio-distance", varRadioMaxChatDist);
		this.varLimitPlayerChat = amcConfig.getBoolean("chat-limited", varLimitPlayerChat);
		this.varLimitRadioChat = amcConfig.getBoolean("radio-limited", varLimitRadioChat);
		this.varSkyWaveEnabled = amcConfig.getBoolean("enable-skywave", varSkyWaveEnabled);
		this.varRadioSkyWaveMod = amcConfig.getDouble("skywave-mod", varRadioSkyWaveMod);
		this.varRadioMinFreq = amcConfig.getInt("radio-min", varRadioMinFreq);
		this.varRadioMaxFreq = amcConfig.getInt("radio-max", varRadioMaxFreq);
		this.varRadioMaxCuttoff = amcConfig.getInt("radio-cutoff-max", varRadioMaxCuttoff);
		this.varRadioMaxCode = amcConfig.getInt("radio-code-max", varRadioMaxCode);
		this.varRadioDefFreq = amcConfig.getInt("radio-default-channel", varRadioDefFreq);
		this.varRadioAutoOn = amcConfig.getBoolean("radio-auto-on", varRadioAutoOn);
	}
	
	public void saveSettings(){
		amcConfig.set("radio-format", varMsgFormat);
		amcConfig.set("radio-suffix",varRadioFreqSuffix);
		amcConfig.set("chat-distance",varPlayerMaxChatDist);
		amcConfig.set("radio-distance", varRadioMaxChatDist);
		amcConfig.set("chat-limited", varLimitPlayerChat);
		amcConfig.set("radio-limited", varLimitRadioChat);
		amcConfig.set("enable-skywave", varSkyWaveEnabled);
		amcConfig.set("skywave-mod", varRadioSkyWaveMod);
		amcConfig.set("radio-min", varRadioMinFreq);
		amcConfig.set("radio-max", varRadioMaxFreq);
		amcConfig.set("radio-cutoff-max", varRadioMaxCuttoff);
		amcConfig.set("radio-code-max", varRadioMaxCode);
		amcConfig.set("radio-default-channel", varRadioDefFreq);
		amcConfig.set("radio-auto-on", varRadioAutoOn);	
		this.saveConfig();
	}
	
	@Override
	public void onDisable() {
		saveSettings();
		amcLogger.info(" disabled.");
	}

	@Override
	public void onEnable() {
		// Load services, order is important!//
		this.amcPdf = this.getDescription();
		this.amcConfig = this.getConfig();
		this.amcTools = new AMCTools(this);
		this.amcRouter = new AMChatRouter(this);
		this.amcCmd = new AMChatCmd(this);
		this.amcListener = new AMChatListener(this);
				
		// Plugin Health Checks
		// Tool package
		if(amcTools.isLoaded()){logMessage("Tools Package Linked");}
		else{logError("Could not link to Tools Package!");}
		
		// Chat router initialized
		if (amcRouter.isLoaded()){logMessage("Chat Router Linked");} 
		else {logError("Could not link to Chat Router!");}
		
		// "AMChat" Listener, not "chat listener", loaded and event registered with plugin manager. 
		if(amcListener.isLoaded()){
			logMessage("Chat listener loaded");
			this.getServer().getPluginManager().registerEvents(amcListener, this);
		}
		else {logError("Could not load AMChat listener");}
		
		if(amcCmd.isLoaded()){
			logMessage("Command control loaded");
			this.getCommand("am").setExecutor(amcCmd);
		}
		else {logError("Could not load command control module");}
		
		loadSettings();	
		saveSettings();
		logMessage("enabled");
	}
	

	// Log info message to console
	public void logMessage(String message){amcLogger.info("["+amcPdf.getName()+"] "+message);}
	
	// Log error message to console, disable plugin. These really never should occur, if they do we need to shut down the plugin and let the sever manager know.  
	public void logError(String message){
		amcLogger.severe("["+amcPdf.getName()+"] WARNING "+message);
		if (this.isEnabled()){this.setEnabled(false);
		amcLogger.severe("["+amcPdf.getName()+"] WARNING Plugin auto disable triggered.");}}
		
	//Initialize player, check variables are set, if not set set default or load from save file
	// TODO create load system in this function
	public boolean initPlayerRadio(Player player){
		if (!playerRadioOn.contains(player.getDisplayName()) && varRadioAutoOn){
			logMessage("Player's radio has been set to auto on per config.yml");
			togglePlayerRadio(player);
		}
		logMessage(player.getDisplayName());
		logMessage("Freq: " + getPlayerRadioChannel(player));
		logMessage("Code: " + getPlayerRadioCode(player));
		logMessage("Mic Open: " + getPlayerMic(player));
		logMessage("Filter Enabled: " + getPlayerFilter(player));
		logMessage("Cutoff: "+ getPlayerCutoff(player));		
		return true;
	}
	
	//Return the Max Chat Distance
	public double getMaxChat(){
		
		return this.varPlayerMaxChatDist;
	}
	//Should the Chat distance be limited?
	public boolean limitChat(){
		return this.varLimitPlayerChat;
	}
	//Return the Max Radio distance
	public double getMaxRadio(){
		return this.varRadioMaxChatDist;
	}	
	//Should we limit radio distance?
	public boolean limitRadio(){
		return this.varLimitRadioChat;
	}
	//Get the SkyWaveRange Modifier
	public double getSkyWaveMod(){
		return this.varRadioSkyWaveMod;
	}	
	//is SkyWave effect enabled?
	public boolean isSkyWaveEnabled(){
		return this.varSkyWaveEnabled;
	}	



	// Returns Array of all players with "On" Radio
	public ArrayList<String> getRadioPlayers (){
		return playerRadioOn;
	}	
	
	// Return True if players radio is on
	public boolean isRadioOn(Player player){
		return playerRadioOn.contains(player.getDisplayName());
	}
	
	public void togglePlayerRadio (Player player){
		if(playerRadioOn.contains(player.getDisplayName())){
			playerRadioOn.remove(player.getDisplayName());
			amcTools.msgToPlayer(player, "[Radio]:"," OFF");
		} else {
			playerRadioOn.add(player.getDisplayName());
			amcTools.msgToPlayer(player, "[Radio]:"," ON");
		}
	}
	
	public void togglePlayerMic(Player player) {
		if(playerRadioMic.get(player)){
			playerRadioMic.put(player, false);
			amcTools.msgToPlayer(player, "[Mic]:"," OFF");
		} else {
			playerRadioMic.put(player, true);
			amcTools.msgToPlayer(player, "[Mic]:"," ON");
		}
		
	}	

	public void togglePlayerFilter (Player player){
		if(playerRadioFilter.get(player)){
			playerRadioFilter.put(player, false);
			amcTools.msgToPlayer(player, "[Filter]:"," OFF");
		} else {
			playerRadioFilter.put(player, true);
			amcTools.msgToPlayer(player, "[Filter]:"," ON");
		}

	}	

	public void tunePlayerRadioChannel(Player player,Integer value){
		playerRadioChannel.put(player, value);
		amcTools.msgToPlayer(player, "[Freq]:"," "+value);
}
	
	public void setPlayerRadioCode(Player player,Integer value){
		if (value < 0){value=0;}
		playerRadioCode.put(player, value);
		amcTools.msgToPlayer(player, "[Code]:"," "+value);
}
	
	public void setPlayerRadioCutoff(Player player,Integer value){
		if (value < 0){value=0;}
		playerRadioCutoff.put(player, value);
		amcTools.msgToPlayer(player, "[Cutoff]:"," "+value);;
}		
	// Return the integer of the channel the player is on
	// if the value is null, set to default channel
	// if the value is below acceptable value, set to minimum channel
	// if the value is above acceptable value set to max value.
	public int getPlayerRadioChannel(Player player){
		int playerOnThisChan = varRadioMinFreq;
		if(playerRadioChannel.containsKey(player)){
			playerOnThisChan = playerRadioChannel.get(player); //get players radio channel
			//check if the returned value is outside acceptable values
			if (playerOnThisChan > varRadioMaxFreq){
				playerOnThisChan = varRadioMaxFreq;
			} else if (playerOnThisChan < varRadioMinFreq){
				playerOnThisChan = varRadioMinFreq;
			}
		} else {
			playerRadioChannel.put(player,varRadioDefFreq);
			playerOnThisChan = varRadioDefFreq;
		}		
		return playerOnThisChan;	
	}
	
	// Return the integer of the radio code the player is using
	// if the value is null, set it to the minimum value, 0=disabled;
	// if value is outside of limits set to minimum value, 0=disabled;
	public int getPlayerRadioCode(Player player){
		int playerUsingThisCode = varRadioMinCode;
		if(playerRadioCode.containsKey(player)){
			playerUsingThisCode = playerRadioCode.get(player);
			if(playerUsingThisCode < varRadioMinCode || playerUsingThisCode > varRadioMaxCode){
				playerUsingThisCode = varRadioMinCode;
			}			
		} else {
			playerRadioCode.put(player, playerUsingThisCode);
		}
		return playerUsingThisCode;
	}
	
	// Return a boolean for the players mic state
	// if the mic is on will return true
	// if the mic is off will return false
	// if the value is null, will set the mic to true and return true
	public boolean getPlayerMic(Player player){
		if(playerRadioMic.containsKey(player)){
			return playerRadioMic.get(player);			
		} else {
			playerRadioMic.put(player, true);
			return true;
		}
	}
	
	// Return a boolean for the players filter state
	// if the filter is on will return true
	// if the filter is off will return false
	// if the value is null will set the filter to false and return that
	public boolean getPlayerFilter(Player player){
		if(playerRadioFilter.containsKey(player)){
			return playerRadioFilter.get(player);			
		} else {
			playerRadioFilter.put(player, false);
			return false;
		}
	}

	// Return boolean for player cutoff filter state
	// if the filter is enabled will return true
	// if the filter is disabled will return false
	// if the value is null, will set the value to false and return that
	public int getPlayerCutoff(Player player){
		if(playerRadioCutoff.containsKey(player)){
			return playerRadioCutoff.get(player);			
		} else {
			playerRadioCutoff.put(player, this.varRadioMaxCuttoff);
			return this.varRadioMaxCuttoff;
		}
	}

	public boolean canReceive(Player sender, Player player) {
//		logMessage(sender.getDisplayName()+" > "+player.getDisplayName());
//		logMessage(playerRadioChannel.get(sender)+" > "+playerRadioChannel.get(player));
//		logMessage(playerRadioCode.get(sender)+" > "+playerRadioCode.get(player));
//		logMessage(playerRadioCutoff.get(sender)+" > "+playerRadioCutoff.get(player));
//		logMessage("receiving filter > "+playerRadioFilter.get(player));
		
		if (sender.equals(player)){
//			logMessage("TRUE:Sender is Reciever");
			return true;}
		if(!isRadioOn(player)){
//			logMessage("FALSE:Radio Off");
			return false;} 
		if (playerRadioChannel.get(sender) < (playerRadioChannel.get(player) - playerRadioCutoff.get(player)) ){ 
//			logMessage("FALSE:"+playerRadioChannel.get(sender)+" < "+playerRadioChannel.get(player)+" - "+playerRadioCutoff.get(player));
			return false;
		} else if (playerRadioChannel.get(sender) > (playerRadioChannel.get(player) + playerRadioCutoff.get(player))  ) {
//			logMessage("FALSE"+playerRadioChannel.get(sender)+" > "+playerRadioChannel.get(player)+" + "+playerRadioCutoff.get(player));
			return false;
		}
		if((playerRadioChannel.get(sender)!=playerRadioChannel.get(player))&&(playerRadioCode.get(sender)!=playerRadioCode.get(player))&&(playerRadioFilter.get(player))){
//			logMessage("FALSE:Filter Enabled");
			return false;
			}
		if(varLimitRadioChat){
//			logMessage("INFO:Radio Chat Distance being checked");
			if(amcTools.getDistance(sender.getLocation(), player.getLocation()) > varRadioMaxChatDist){
//				logMessage("FALSE:Radio Distance too Far");
				return false;
				}
			}
		
//		logMessage("TRUE:Player can receive message");
		return true;
	}	
	
	public boolean canRead(Player sender, Player player) {		
		if (sender.equals(player)){return true;}
		if (playerRadioCode.get(sender)==0){return true;}
		if((playerRadioCode.get(sender) == playerRadioCode.get(player))&&(playerRadioChannel.get(sender) == playerRadioChannel.get(player))){return true;} 
		return false;
	}


	
	
//	public void togglePlayerMic(Player player){
//		if(playerRadioMic.containsKey(player)){
//		if(playerRadioMic.get(player)){
//			playerRadioMic.put(player, false);
//			player.sendMessage(ChatColor.YELLOW + "Mic Off");
//		} else {
//			playerRadioMic.put(player, true);
//			player.sendMessage(ChatColor.YELLOW + "Mic On");			
//		}
//		} else {
//			playerRadioMic.put(player, true);
//			player.sendMessage(ChatColor.YELLOW + "Mic On");
//		}			
//		
//	}	
	
	

}
