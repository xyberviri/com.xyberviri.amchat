package com.xyberviri.amchat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.xyberviri.amchat.radio.Radio;

public class AMChatSettingsManager {
	
	public String getMsgFormat(){return varMsgFormat;}
	public String getRadioSuffix(){return varRadioFreqSuffix;}
	public double getMaxLocalDist(){return varPlayerMaxChatDist;}
	public double getMaxRadioDist(){return varRadioMaxChatDist;}
	public boolean isLocalManaged(){return varManagePlayerChat;}
	public boolean isLocalLimited(){return varLimitPlayerChat;}
	public boolean isRadioManaged(){return varLimitRadioChat;}
	public boolean isSkyWaveEnabled(){return varSkyWaveEnabled;}
	public double getSkyWaveMod(){return varSkyWaveMod;}
	public boolean firstTimeOn(){return varRadioAutoOn;}
	public int getDefaultFreq(){return varRadioDefFreq;}
	public int getMinFreq(){return varRadioMinFreq;}
	public int getMaxFreq(){return varRadioMaxFreq;}
	public int getMaxCutoff(){return varRadioMaxCutoff;}
	public int getMaxCode(){return varRadioMaxCode;}
	public int getItemID(){return varHeldItemID;}
	public boolean isItemRequired(){return varHeldItemReq;}
	
	private String varMsgFormat = ChatColor.DARK_GREEN+"["+ChatColor.GOLD+"%FREQ.%CODE"+ChatColor.GRAY+"%SUFFIX"+ChatColor.DARK_GREEN+"]"+ChatColor.YELLOW+"%SENDER"+ChatColor.WHITE+": %MESSAGE";
	private String varRadioFreqSuffix = "rHz"; 		// frequency string name
	private double varPlayerMaxChatDist = 32;		// The Maximum Distance local chat will reach.
	private double varRadioMaxChatDist = 96;		// The Maximum Distance Radio chat will reach.
	private boolean varManagePlayerChat = true;		// is our plugin responsible for dealing with non radio chat?
	private boolean varLimitPlayerChat = true;		// Should we limit the distance that non radio chat can reach?
	private boolean varLimitRadioChat = true;		// Should we limit the distance that a personal communicator can reach?

	private double varSkyWaveMod = 2;				// This distance to modify the chat distance for radios at night.
	private boolean varSkyWaveEnabled = false;		// Is SkyWave Effect enabled?
	
	private boolean varRadioAutoOn = true;			// if we should automatically turn a players radio on
	private int varRadioDefFreq = 64;				// The first time a invalid frequency is returned, return this instead. this is also the /am home channel 
	private int varRadioMinFreq = 32;				// this is the lowest frequency we can set for transmitting
	private int varRadioMaxFreq = 512;				// this is the highest frequency we can set for transmitting
	private int varRadioMaxCutoff = 15;
	private int varRadioMaxCode = 999;				// max value encryption key we will use for transmission.
	private int varHeldItemID = 345;				// the held item that is our radio
	private boolean varHeldItemReq = false;			// is the held item needed so we can use our radio.

	private FileConfiguration playerRadioConfig=null;
	private File playerRadioConfigFile = null;
	private FileConfiguration amcConfig;			//config.yml file
	private Map <Player, Radio> 	playerRadios;	//Player Radio

	AMChatSettingsManager(){
		this.playerRadios = new HashMap<Player, Radio>();		
		this.amcConfig = AMChat.get().getConfig();
		this.playerRadioConfig = getConfigPlayerRadioSettings();		
		loadSettings();
		saveSettings();		
	}
	
	public Radio player(Player player){
		if(playerRadios.containsKey(player)){
			return playerRadios.get(player);
		} else {
			return createNewRadio(player);
		}
	}
	
	private Radio createNewRadio(Player player){
		Radio temp = new Radio(varRadioDefFreq);
		playerRadios.put(player, temp);		
		return temp;
	}
	
	
	
	
	

	//Load/Save Plugin Settings from config.yml
	private void loadSettings(){
		this.varMsgFormat = AMChat.tools().formatLoadFix(amcConfig.getString("radio-format", varMsgFormat));
		this.varRadioFreqSuffix = amcConfig.getString("radio-suffix",varRadioFreqSuffix);
		this.varPlayerMaxChatDist = amcConfig.getDouble("chat-distance",varPlayerMaxChatDist);
		this.varRadioMaxChatDist = amcConfig.getDouble("radio-distance", varRadioMaxChatDist);
		this.varManagePlayerChat = amcConfig.getBoolean("manage-local", varManagePlayerChat);
		this.varLimitPlayerChat = amcConfig.getBoolean("chat-limited", varLimitPlayerChat);
		this.varLimitRadioChat = amcConfig.getBoolean("radio-limited", varLimitRadioChat);
		this.varSkyWaveEnabled = amcConfig.getBoolean("enable-skywave", varSkyWaveEnabled);
		this.varSkyWaveMod = amcConfig.getDouble("skywave-mod", varSkyWaveMod);
		this.varRadioMinFreq = amcConfig.getInt("radio-min", varRadioMinFreq);
		this.varRadioMaxFreq = amcConfig.getInt("radio-max", varRadioMaxFreq);
		this.varRadioMaxCutoff = amcConfig.getInt("radio-cutoff-max", varRadioMaxCutoff);
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
		amcConfig.set("skywave-mod", varSkyWaveMod);
		amcConfig.set("radio-min", varRadioMinFreq);
		amcConfig.set("radio-max", varRadioMaxFreq);
		amcConfig.set("radio-cutoff-max", varRadioMaxCutoff);
		amcConfig.set("radio-code-max", varRadioMaxCode);
		amcConfig.set("radio-default-channel", varRadioDefFreq);
		amcConfig.set("radio-auto-on", varRadioAutoOn);	
		amcConfig.set("radio-item-id",varHeldItemID);
		amcConfig.set("radio-item-required", varHeldItemReq);
		AMChat.get().saveConfig();
	}
	
	// pl.settings.yml saving/loading //
	private FileConfiguration getConfigPlayerRadioSettings(){
		if (playerRadioConfig==null){
			reloadConfigPlayerRadioSettings();
			}
		return playerRadioConfig;			
	}

	private void reloadConfigPlayerRadioSettings(){
		if(playerRadioConfigFile==null){
		   playerRadioConfigFile=new File(AMChat.get().getDataFolder(),"pl.settings.yml");
		}		
		playerRadioConfig = YamlConfiguration.loadConfiguration(playerRadioConfigFile);		
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
	// pl.settings.yml saving/loading to/from disk//
	
}//EOL