package com.xyberviri.amchat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.xyberviri.amchat.radio.Radio;

public class AMChatSettingsManager {
	String varMsgFormat = ChatColor.DARK_GREEN+"["+ChatColor.GOLD+"%FREQ.%CODE"+ChatColor.GRAY+"%SUFFIX"+ChatColor.DARK_GREEN+"]"+ChatColor.YELLOW+"%SENDER"+ChatColor.WHITE+": %MESSAGE";
	String varRadioFreqSuffix = "rHz"; 		// frequency string name
	double varPlayerMaxChatDist = 32;		// The Maximum Distance local chat will reach.
	double varRadioMaxChatDist = 96;		// The Maximum Distance Radio chat will reach.
	boolean varManagePlayerChat = true;		// is our plugin responsible for dealing with non radio chat?
	boolean varLimitPlayerChat = true;		// Should we limit the distance that non radio chat can reach?
	boolean varLimitRadioChat = true;		// Should we limit the distance that a personal communicator can reach?
	boolean varUseRPMessages = true;		// Should we use the Role playing responses?
	
	double varRadioSkyWaveMod = 2;			// This distance to modify the chat distance for radios at night.
	boolean varSkyWaveEnabled = false;		// Is SkyWave Effect enabled?
	
	boolean varRadioAutoOn = true;			// if we should automatically turn a players radio on
	int varRadioDefFreq = 64;				// The first time a invalid frequency is returned, return this instead. this is also the /am home channel 
	int varRadioMinFreq = 32;				// this is the lowest frequency we can set for transmitting
	int varRadioMaxFreq = 512;				// this is the highest frequency we can set for transmitting
	int varRadioMaxCuttoff = 15;
	int varRadioMinCode = 0;				// this is the minimum valid code key, 0 = disabled; This really shouldn't be changed.
	int varRadioMaxCode = 999;				// max value encryption key we will use for transmission.
	int varHeldItemID = 345;				// the held item that is our radio
	boolean varHeldItemReq = false;			// is the held item needed so we can use our radio.

	private FileConfiguration playerRadioConfig=null;
	private File playerRadioConfigFile = null;
	private FileConfiguration amcConfig;			//config.yml file
	private Map <Player, Radio> 	playerRadios;	//Player Radio

	AMChatSettingsManager(){
		this.playerRadios = new HashMap<Player, Radio>();
		
		this.amcConfig = AMChat.get().getConfig();
		this.playerRadioConfig = getConfigPlayerRadioSettings();
		
		initSettings();
		
	}

	//Initialize Settings.
	private void initSettings(){
		loadSettings();	//Load any settings from config.yml to memory.
		saveSettings();	//config.yml, if there isn't one this makes one in the plugin folder.  
	}
	
	public Radio player(Player vPlayer){
		if(playerRadios.containsKey(vPlayer)){
			return playerRadios.get(vPlayer);
		} else {
			return createNewRadio(vPlayer);
		}
	}
	
	public Radio createNewRadio(Player vPlayer){
		Radio vTemp = new Radio(varRadioDefFreq);
		
		return vTemp;
	}
	
	
	
	
	
//	@SuppressWarnings("unchecked")
//	public void loadPlayerRadioSettings(Player player){
//		boolean playerHasSettings;		
//		Map<String, Object> playerSetting = new HashMap<String,Object>();
//		if (playerRadioConfig.isConfigurationSection(player.getDisplayName())){
//			playerSetting = playerRadioConfig.getConfigurationSection(player.getDisplayName()).getValues(true);
//			playerHasSettings=true;
//		} 
//		else{
//			AMChat.logMessage("No Saved settings for player, loading defaults");
//			playerHasSettings=false;
//		}
//		
//		
////		if (playerHasSettings && playerSetting.containsKey("radio")){			
////			if(!isRadioOn(player)&&((Boolean) playerSetting.get("radio"))){togglePlayerRadio(player);}
////		} else if (!isRadioOn(player) && varRadioAutoOn){
////			togglePlayerRadio(player);
////		}
////		
////		if(playerHasSettings && playerSetting.containsKey("freq")){
////			tunePlayerRadioChannel(player, (Integer) playerSetting.get("freq"));
////		} else {
////			tunePlayerRadioChannel(player,varRadioDefFreq);
////		}
////		
////		if(playerHasSettings && playerSetting.containsKey("code")){
////			setPlayerRadioCode(player,(Integer) playerSetting.get("code"));
////		} else {
////			setPlayerRadioCode(player, 0);
////		}
////		
////		if(playerHasSettings && playerSetting.containsKey("mic")){
////			setPlayerMic(player, (Boolean) playerSetting.get("mic"));
////		} else {
////			setPlayerMic(player, true);
////		}
////		
////		if(playerHasSettings && playerSetting.containsKey("filter")){
////			setPlayerFilter(player, (Boolean) playerSetting.get("filter"));
////		} else {
////			setPlayerFilter(player, false);
////		}
////			
////		if(playerHasSettings && playerSetting.containsKey("cutoff")){
////			setPlayerRadioCutoff(player, (Integer) playerSetting.get("cutoff"));
////		} else {
////			setPlayerRadioCutoff(player, varRadioMaxCuttoff);
////		}
////		//String last link we had on logout. 
////		if (playerHasSettings && playerSetting.containsKey("link") && amcRadMan.isLinkValid((String) playerSetting.get("link"))){
////			amcRadMan.linkPlayerToRadio(player, (String) playerSetting.get("link"));
////		} else {
////			setPlayerLinkID(player, "none");
////		}
////		//ArrayList<String> Players favorite radios
////		if(playerHasSettings && playerSetting.containsKey("favorites")){
////			Object objAdmin=playerSetting.get("favorites");
////			if (objAdmin instanceof ArrayList<?>){
////				this.setFavRadios(player, (ArrayList<String>) objAdmin);	
////			}				
////		}
//		
//		if(!playerHasSettings){
//			savePlayerRadioSettings(player);
//		}
//	}
//	
//	public void savePlayerRadioSettings(Player player){
//	//Map<String, Object> playerSettings = new HashMap<String,Object>();
//	Map<String, Object> playerSetting = new HashMap<String,Object>();
//	playerSetting.put("radio",isRadioOn(player));
//	playerSetting.put("freq",getPlayerRadioChannel(player));	
//	playerSetting.put("code",getPlayerRadioCode(player));
//	playerSetting.put("mic",getPlayerMic(player));
//	playerSetting.put("filter",getPlayerFilter(player));
//	playerSetting.put("cutoff",getPlayerCutoff(player));
//	playerSetting.put("link",getPlayerLinkID(player));
//	playerSetting.put("favorites", getFavRadios(player));
//	//playerSettings.put(player.getDisplayName(), playerSetting);
//	//playerRadioConfig.createSection("radio-settings",playerSettings);
//	playerRadioConfig.createSection(player.getDisplayName(), playerSetting);
//	saveConfigPlayerRadioSettings();
//	}	
	

	//Load/Save Plugin Settings
	private void loadSettings(){
		this.varMsgFormat = AMChat.tools().formatLoadFix(amcConfig.getString("radio-format", varMsgFormat));
		this.varRadioFreqSuffix = amcConfig.getString("radio-suffix",varRadioFreqSuffix);
		this.varPlayerMaxChatDist = amcConfig.getDouble("chat-distance",varPlayerMaxChatDist);
		this.varRadioMaxChatDist = amcConfig.getDouble("radio-distance", varRadioMaxChatDist);
		this.varManagePlayerChat = amcConfig.getBoolean("manage-local", varManagePlayerChat);
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
		this.varHeldItemID = amcConfig.getInt("radio-item-id",varHeldItemID);
		this.varHeldItemReq = amcConfig.getBoolean("radio-item-required", varHeldItemReq);
	}
	
	private void saveSettings(){
		amcConfig.set("radio-format", AMChat.tools().formatSaveFix(varMsgFormat));
		amcConfig.set("radio-suffix",varRadioFreqSuffix);
		amcConfig.set("chat-distance",varPlayerMaxChatDist);
		amcConfig.set("radio-distance", varRadioMaxChatDist);
		amcConfig.set("manage-local", varManagePlayerChat);
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
		amcConfig.set("radio-item-id",varHeldItemID);
		amcConfig.set("radio-item-required", varHeldItemReq);
		AMChat.get().saveConfig();
	}
	
	//Player Settings File Get/Save//
	private void reloadConfigPlayerRadioSettings(){
		if(playerRadioConfigFile==null){
			playerRadioConfigFile=new File(AMChat.get().getDataFolder(),"pl.settings.yml");
		}		
		playerRadioConfig = YamlConfiguration.loadConfiguration(playerRadioConfigFile);		
		}
	
	public FileConfiguration getConfigPlayerRadioSettings(){
		if (playerRadioConfig==null){
			reloadConfigPlayerRadioSettings();
			}
		return playerRadioConfig;			
	}
	
	public void saveConfigPlayerRadioSettings(){
		if (playerRadioConfigFile==null||playerRadioConfig==null){
			AMChat.logMessage("Unable to save, player radio config pointers are null");
			return;}
		try{
			playerRadioConfig.save(playerRadioConfigFile);
			AMChat.logMessage("Saving player settings file");
		} catch (IOException ex){
			AMChat.logMessage("Could not save player setting file "+playerRadioConfigFile+" IOexception " + ex.toString());
		}
		
	}	
}
