package com.xyberviri.amchat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class AMChat extends JavaPlugin {
	
	public static AMChat amcChatMain;		//handle for this plugin
	AMCTools amcTools;						//tools for AMChat
	AMChatRouter amcRouter;					//Chat router is responsible for deciding what to do with player chat events.
	AMChatListener amcListener;				//Chat Listener
	AMChatCmd amcCmd;						//AMC Command control
	AMChatRadioManager amcRadMan;			//
	PluginDescriptionFile amcPdf;			//Plugin Description File
	FileConfiguration amcConfig;			//config.yml file
	
	private FileConfiguration playerRadioConfig=null;
	private File playerRadioConfigFile = null;

	final Logger amcLogger = Logger.getLogger("Minecraft");
	
	// Settings
	String varMsgFormat = ChatColor.DARK_GREEN+"["+ChatColor.GOLD+"%FREQ.%CODE"+ChatColor.GRAY+"%SUFFIX"+ChatColor.DARK_GREEN+"]"+ChatColor.YELLOW+"%SENDER"+ChatColor.WHITE+": %MESSAGE";
	String varBCsatMsgFormat = ChatColor.DARK_GREEN+"["+ChatColor.GOLD+"%LINKID"+ChatColor.DARK_GREEN+"]"+ChatColor.YELLOW+"%SENDER"+ChatColor.WHITE+": %MESSAGE";
	
	String varRadioFreqSuffix = "rHz"; 		// frequency string name
	
	//these are double because we calculate the distance versus this, and that variable is a double
	double varPlayerMaxChatDist = 32;		// The Maximum Distance local chat will reach.
	double varRadioMaxChatDist = 96;		// The Maximum Distance Radio chat will reach.
	boolean varManagePlayerChat = true;	// is our plugin responsible for dealing with non radio chat?
	boolean varLimitPlayerChat = true;	// Should we limit the distance that non radio chat can reach?
	boolean varLimitRadioChat = true;	// Should we limit the distance that a personal communicator can reach?
	
	double varRadioSkyWaveMod = 2;		// This distance to modify the chat distance for radios at night.
	boolean varSkyWaveEnabled = false;	// Is SkyWave Effect enabled?
	
	boolean varRadioAutoOn = true;		// if we should automatically turn a players radio on
	int varRadioDefFreq = 64;			// The first time a invalid frequency is returned, return this instead. this is also the /am home channel 
	int varRadioMinFreq = 32;			// this is the lowest frequency we can set for transmitting
	int varRadioMaxFreq = 512;			// this is the highest frequency we can set for transmitting
	int varRadioMaxCuttoff = 15;
	int varRadioMinCode = 0;			// this is the minimum valid code key, 0 = disabled; This really shouldn't be changed.
	int varRadioMaxCode = 999;			// max value encryption key we will use for transmission.

	int varScheduleTickRate=20;			//This is the value for the tick rate used by the scheduler
	long varScheduleSaveRate=600000;	//this is how many milliseconds we should wait before we save the active radios.
	
	int varHeldItemID = 345;			// the held item that is our radio
	boolean varHeldItemReq = false;		// is the held item needed so we can use our radio.
	
	double varFixedRadioRangeMod=10;	// this is the distance each antenna block gives our fixed radios.	
	int varFixedRadioUserModI=1;		// this is the additional number of users that a radio can support with each additional iron block
	int varFixedRadioUserModG=3;		// "		"		"		"		"		"		gold block
	int varFixedRadioUserModD=7;		// "		"		"		"		"		"		diamond block
	
	ArrayList<String>    playerRadioOn = new ArrayList<String>();				// List of players with Radios On
	Map<Player, Integer> playerRadioChannel = new HashMap<Player, Integer>(); 	// player radio channel
	Map<Player, Integer> playerRadioCode = new HashMap<Player, Integer>(); 		// Player radio encoding key
	Map<Player, Boolean> playerRadioMic = new HashMap<Player, Boolean>();		// Player radio broadcasting enabled	
	Map<Player, Boolean> playerRadioFilter = new HashMap<Player, Boolean>();	// Player radio filter blocks encrypted chat that is otherwise unreadable
	Map<Player, Integer> playerRadioCutoff = new HashMap<Player, Integer>();	// Player radio cutoff blocks other channels, i.e. cross talk, we might make this a integer later
	Map<Player, String>  playerRadioLinkID = new HashMap<Player,String>();		// If a player is linked to a tower this will be set to something. 		
	Map<Player, ArrayList<String>>  playerFavRadios = new HashMap<Player,ArrayList<String>>();
	
	//Player Settings File Reload/Get/Save//
	public void reloadConfigPlayerRadioSettings(){
		if(playerRadioConfigFile==null){
			playerRadioConfigFile=new File(this.getDataFolder(),"pl.settings.yml");
		}
		
		playerRadioConfig = YamlConfiguration.loadConfiguration(playerRadioConfigFile);
		InputStream defConfigStream = getResource("pl.settings.yml");
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			playerRadioConfig.setDefaults(defConfig);
	}}
	
	public FileConfiguration getConfigPlayerRadioSettings(){
		if (playerRadioConfig==null){
			reloadConfigPlayerRadioSettings();
			}
		return playerRadioConfig;			
	}
	
	public void saveConfigPlayerRadioSettings(){
		if (playerRadioConfigFile==null||playerRadioConfig==null){
			logMessage("unable to save varible for Config or file is null");
			return;}
		try{
			playerRadioConfig.save(playerRadioConfigFile);
			logMessage("Saving player settings file");
		} catch (IOException ex){
			logMessage("Could not save player setting file "+playerRadioConfigFile+" IOexception " + ex.toString());
		}
		
	}
	
	public void loadSettings(){
		this.varMsgFormat = amcTools.formatLoadFix(amcConfig.getString("radio-format", varMsgFormat));
		this.varBCsatMsgFormat = amcTools.formatLoadFix(amcConfig.getString("broadcast-format", varBCsatMsgFormat));		
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
		this.varFixedRadioRangeMod=amcConfig.getDouble("antenna-range-mod", varFixedRadioRangeMod);		
		this.varFixedRadioUserModI = amcConfig.getInt("antenna-user-mod-iron",varFixedRadioUserModI);
		this.varFixedRadioUserModG = amcConfig.getInt("antenna-user-mod-gold",varFixedRadioUserModG);
		this.varFixedRadioUserModD = amcConfig.getInt("antenna-user-mod-diamond",varFixedRadioUserModD);
		this.varScheduleTickRate = amcConfig.getInt("antenna-tick-rate",varScheduleTickRate);
		this.varScheduleSaveRate = amcConfig.getLong("save-interval",varScheduleSaveRate);
	}
	
	public void saveSettings(){
		amcConfig.set("radio-format", amcTools.formatSaveFix(varMsgFormat));
		amcConfig.set("broadcast-format", amcTools.formatSaveFix(varBCsatMsgFormat));
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
		amcConfig.set("antenna-range-mod", varFixedRadioRangeMod);
		amcConfig.set("antenna-user-mod-iron",varFixedRadioUserModI);
		amcConfig.set("antenna-user-mod-gold",varFixedRadioUserModG);
		amcConfig.set("antenna-user-mod-diamond",varFixedRadioUserModD);
		amcConfig.set("antenna-tick-rate",varScheduleTickRate);
		amcConfig.set("save-interval",varScheduleSaveRate);
		this.saveConfig();
	}
	
	@Override
	public void onDisable() {
		saveSettings();
		saveConfigPlayerRadioSettings();
		amcRadMan.saveRadioSettings();
		logMessage("Disabled.");
	}

	@Override
	public void onEnable() {
		// Load services, order is important!//
		amcChatMain = this;
		this.amcPdf = this.getDescription();
		this.amcConfig = this.getConfig();
		this.playerRadioConfig = this.getConfigPlayerRadioSettings();
		this.amcTools = new AMCTools(this);
		this.amcRouter = new AMChatRouter(this);
		this.amcCmd = new AMChatCmd(this);
		this.amcListener = new AMChatListener(this);
		this.amcRadMan = new AMChatRadioManager(this);
		loadSettings();
		saveSettings();
		

		this.getServer().getPluginManager().registerEvents(amcListener, this);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new AMChatRadioCheck(this),20,varScheduleTickRate);
		
		this.getCommand("am").setExecutor(amcCmd);
		this.getCommand("xm").setExecutor(amcCmd);
		
	
		
		logMessage("Enabled");
	}
	

	// Log info message to console
	public void logMessage(String message){amcLogger.info("["+amcPdf.getName()+"] "+message);}
	
	// Log error message to console, disable plugin. These really never should occur, if they do we need to shut down the plugin and let the sever manager know.  
	public void logError(String message){
		amcLogger.severe("["+amcPdf.getName()+"] WARNING "+message);
		if (this.isEnabled()){this.getServer().getPluginManager().disablePlugin(this);
		amcLogger.severe("["+amcPdf.getName()+"] WARNING Plugin auto disable triggered.");}}
		
	@SuppressWarnings("unchecked")
	public void loadPlayerRadioSettings(Player player){
		boolean playerHasSettings;		
		Map<String, Object> playerSetting = new HashMap<String,Object>();
		if (playerRadioConfig.isConfigurationSection(player.getDisplayName())){
			playerSetting = playerRadioConfig.getConfigurationSection(player.getDisplayName()).getValues(true);
			playerHasSettings=true;
		} 
		else{
			logMessage("No Saved settings for player, loading defaults");
			playerHasSettings=false;
		}
		
		
		//if some developer reads this drop me a hint at a better way to do .yml settings
		//TODO: Figure out where to put Validation for channel, codes and cutoff settings.
		
		if (playerHasSettings && playerSetting.containsKey("radio")){			
			if(!isRadioOn(player)&&((Boolean) playerSetting.get("radio"))){togglePlayerRadio(player);}
		} else if (!isRadioOn(player) && varRadioAutoOn){
			togglePlayerRadio(player);
		}
		
		if(playerHasSettings && playerSetting.containsKey("freq")){
			tunePlayerRadioChannel(player, (Integer) playerSetting.get("freq"));
		} else {
			tunePlayerRadioChannel(player,varRadioDefFreq);
		}
		
		if(playerHasSettings && playerSetting.containsKey("code")){
			setPlayerRadioCode(player,(Integer) playerSetting.get("code"));
		} else {
			setPlayerRadioCode(player, 0);
		}
		
		if(playerHasSettings && playerSetting.containsKey("mic")){
			setPlayerMic(player, (Boolean) playerSetting.get("mic"));
		} else {
			setPlayerMic(player, true);
		}
		
		if(playerHasSettings && playerSetting.containsKey("filter")){
			setPlayerFilter(player, (Boolean) playerSetting.get("filter"));
		} else {
			setPlayerFilter(player, false);
		}
			
		if(playerHasSettings && playerSetting.containsKey("cutoff")){
			setPlayerRadioCutoff(player, (Integer) playerSetting.get("cutoff"));
		} else {
			setPlayerRadioCutoff(player, varRadioMaxCuttoff);
		}
		//String last link we had on logout. 
		if (playerHasSettings && playerSetting.containsKey("link") && amcRadMan.isLinkValid((String) playerSetting.get("link"))){
			amcRadMan.linkPlayerToRadio(player, (String) playerSetting.get("link"));
		} else {
			setPlayerLinkID(player, "none");
		}
		//ArrayList<String> Players favorite radios
		if(playerHasSettings && playerSetting.containsKey("favorites")){
			Object objAdmin=playerSetting.get("favorites");
			if (objAdmin instanceof ArrayList<?>){
				this.setFavRadios(player, (ArrayList<String>) objAdmin);	
			}				
		}
		
		if(!playerHasSettings){
			savePlayerRadioSettings(player);
		}
	}
	
	public void savePlayerRadioSettings(Player player){
	//Map<String, Object> playerSettings = new HashMap<String,Object>();
	Map<String, Object> playerSetting = new HashMap<String,Object>();
	playerSetting.put("radio",isRadioOn(player));
	playerSetting.put("freq",getPlayerRadioChannel(player));	
	playerSetting.put("code",getPlayerRadioCode(player));
	playerSetting.put("mic",getPlayerMic(player));
	playerSetting.put("filter",getPlayerFilter(player));
	playerSetting.put("cutoff",getPlayerCutoff(player));
	playerSetting.put("link",getPlayerLinkID(player));
	playerSetting.put("favorites", getFavRadios(player));
	//playerSettings.put(player.getDisplayName(), playerSetting);
	//playerRadioConfig.createSection("radio-settings",playerSettings);
	playerRadioConfig.createSection(player.getDisplayName(), playerSetting);
	saveConfigPlayerRadioSettings();
	}
	
	public boolean isFavRadio(Player player,String VarRadioID){
		if(playerFavRadios.containsKey(player)){
			return playerFavRadios.get(player).contains(VarRadioID);
		}
	return false;
	}
	
	//Add This ID to a players bookmark list
	public void addFavRadio(Player player,String varRadioID){
		ArrayList<String> favRadio = new ArrayList<String>();
		if(playerFavRadios.containsKey(player)){
			favRadio = playerFavRadios.get(player);
			if(!favRadio.contains(varRadioID)){	// Don't add duplicates
				favRadio.add(varRadioID);
			}
		} else {			
			favRadio.add(varRadioID);
		}
		playerFavRadios.put(player, favRadio);
	}

	//Delete This ID from the players bookmark list.
	public void delFavRadio(Player player,String varRadioID){  
		ArrayList<String> favRadio = new ArrayList<String>();
		if(playerFavRadios.containsKey(player)){
			favRadio = playerFavRadios.get(player);
			if(favRadio.contains(varRadioID)){
				favRadio.remove(varRadioID);				
			}
		}
		this.playerFavRadios.put(player, favRadio);
	}
	
	public ArrayList<String> getFavRadios(Player player){
		ArrayList<String> favRadio = new ArrayList<String>();
		if(playerFavRadios.containsKey(player)){
			favRadio = playerFavRadios.get(player);
		}
		else {
			this.playerFavRadios.put(player, favRadio);
		}
		return favRadio;
	}
	
	//Set all of the players favorites, this is really just for use during loading.  
	private void setFavRadios(Player player,ArrayList<String> favList){
		this.playerFavRadios.put(player, favList);
	}
   	
	//return true if the player is linked to a radio transmitter. 
	public boolean isPlayerLinked(Player player){
		if(getPlayerLinkID(player).equalsIgnoreCase("none")){
			return false;
		}
		return true;		
	}
	//Get Players Radio link id, if the key is missing set it to none
	public String getPlayerLinkID(Player player) {
		if (!playerRadioLinkID.containsKey(player)){
			setPlayerLinkID(player,"none");
			}
		return playerRadioLinkID.get(player);
	}
	
	//Set Players Radio link id, if the give id is blank its set to none
	public void setPlayerLinkID(Player player,String linkID){
		if(linkID.isEmpty()){linkID="none";}
		amcTools.msgToPlayer(player, "[Link]: ",linkID);
		this.playerRadioLinkID.put(player, linkID);
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

	public boolean isLocalManaged(){
		return this.varManagePlayerChat;
	}
	//This is resource intensive don't use it allot. 
	public List<Player> getPlayersByLinkID(String varLinkID){
		List<Player> varList = new ArrayList<Player>();
		for(Player linkID: playerRadioLinkID.keySet()){
			if(playerRadioLinkID.get(linkID).equals(varLinkID)){
				varList.add(linkID);
			}
		}
		return varList;
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
	
	public void scanPlayerRadioChannel(Player player,boolean varDirection){
		if(isPlayerLinked(player)){
			amcTools.msgToPlayer(player, "TODO:Scanning for radio links direction up:"+varDirection);
		} else {
		int playerOnThisChan = varRadioMinFreq;
		if(playerRadioChannel.containsKey(player)){
			int currentChan = playerRadioChannel.get(player);
			int scanValue = playerRadioCutoff.get(player);
			//SCAN UP
			if(varDirection){
				playerOnThisChan=currentChan+scanValue;
			if(playerOnThisChan>varRadioMaxFreq){
				playerOnThisChan=varRadioMinFreq+(playerOnThisChan-varRadioMaxFreq);
			}
			//SCAN DOWN	
			}else {
				playerOnThisChan=currentChan-scanValue;	
				if(playerOnThisChan< varRadioMinFreq){
					playerOnThisChan=varRadioMaxFreq-(playerOnThisChan+varRadioMinFreq);
				}				
			}
		}else {
			playerRadioChannel.put(player,varRadioDefFreq);
			playerOnThisChan = varRadioDefFreq;
		}
		tunePlayerRadioChannel(player,playerOnThisChan);
		}
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
	public void setPlayerMic(Player player,boolean b){
		playerRadioMic.put(player, b);
	}
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
	public void setPlayerFilter(Player player, boolean b){
		playerRadioFilter.put(player, b);
	}
	
	public boolean getPlayerFilter(Player player){
		if(playerRadioFilter.containsKey(player)){
			return playerRadioFilter.get(player);			
		} else {
			playerRadioFilter.put(player, false);
			return false;
		}
	}

	// return the cutoff value for our radio
	// we hear our_channel+-cut_off
	public int getPlayerCutoff(Player player){
		if(playerRadioCutoff.containsKey(player)){
			return playerRadioCutoff.get(player);			
		} else {
			playerRadioCutoff.put(player, this.varRadioMaxCuttoff);
			return this.varRadioMaxCuttoff;
		}
	}

	//Instead of slamming the main object with compares we do all that stuff here
	public boolean canReceive(Player sender, Player player) {
		if (sender.equals(player)){ 
			return true;}		
			//If the player receiving the message is a op or has the below permission they can hear everything.
		if (player.hasPermission("amchat.radio.hearall")||player.isOp()){return true;}
			
		if(!isRadioOn(player)){//The player doesn't even have his radio on			
			return false;} 
		
		if(!sender.getWorld().equals(player.getWorld())){ //we can't talk to the other side.
			return false;
		}
		
		if (playerRadioChannel.get(sender) < (playerRadioChannel.get(player) - playerRadioCutoff.get(player)) ){
			//Radio chat is below cutoff limit
			return false;
		} else if (playerRadioChannel.get(sender) > (playerRadioChannel.get(player) + playerRadioCutoff.get(player))  ) {
			//Radio channel is above the cutoff limit
			return false;
		}
		//TODO:Change this comparison, This might cause issues on certain channel/code combinations
		if((playerRadioChannel.get(sender)!=playerRadioChannel.get(player))&&(playerRadioCode.get(sender)!=playerRadioCode.get(player))&&(playerRadioFilter.get(player))){
			//message is encrypted and we don't want to hear that.
			return false;
			}
		if(varLimitRadioChat){
			if(amcTools.getDistance(sender.getLocation(), player.getLocation()) > varRadioMaxChatDist){
				//Radio chat is limited and they are too far
				return false;
				}
			}
		return true;
	}	
	
	public boolean canReceive(AMChatRadio radio, Player player) {
		if (player.hasPermission("amchat.radio.hearall")||player.isOp()){
			return true;
			}			
		if(!isRadioOn(player)){//The player doesn't even have his radio on			
			return false;
			} 
		
		if(!radio.getLoc().getWorld().equals(player.getWorld())){ //we can't talk to the other side.
			return false;
			}
		
		if (radio.getChan() < (playerRadioChannel.get(player) - playerRadioCutoff.get(player)) ){
			//Radio chat is below cutoff limit
			return false;
		} else if (radio.getChan() > (playerRadioChannel.get(player) + playerRadioCutoff.get(player))  ) {
			//Radio channel is above the cutoff limit
			return false;
		}
		if((!playerRadioChannel.get(player).equals(radio.getChan()))&&(!playerRadioCode.get(player).equals(radio.getCode()))&&(playerRadioFilter.get(player))){
			//message is encrypted and we don't want to hear that.	
			return false;
			}
		if(varLimitRadioChat){
			if (!radio.isAdmin()&&(amcTools.getDistance(radio.getLoc(), player.getLocation()) > radio.getMaxDistance())){
				return false;
				}
			}
		return true;
	}	
	
	public boolean canLink(AMChatRadio radio, Player player) {
		if (player.hasPermission("amchat.radio.override.link")||player.isOp()){
			return true;
			}			
		if(!isRadioOn(player)){//The player doesn't even have his radio on			
			return false;
			} 
		
		if(!radio.getLoc().getWorld().equals(player.getWorld())){ //we can't talk to the other side.
			return false;
			}
		if(varLimitRadioChat){
			if (!radio.isAdmin()&&(amcTools.getDistance(radio.getLoc(), player.getLocation()) > radio.getMaxDistance())){
				return false;
				}
			}
		return true;
	}		
	//same logic for checking if players can receive is implied here.
	public boolean canRead(Player sender, Player player) {
		//Sender is Receiver
		if (sender.equals(player)){return true;}	
		//Sender is not encrypting chat
		if (playerRadioCode.get(sender)==0){return true;}
		//Receiver has read all permission
		if (player.hasPermission("amchat.radio.readall")||player.isOp()){return true;}
		//the player and receiver are on the same channel with the same key
		if((playerRadioCode.get(sender).equals(playerRadioCode.get(player)))&&(playerRadioChannel.get(sender).equals(playerRadioChannel.get(player)))){
			return true;
		} 
		return false;
	}
	
	public boolean canRead(AMChatRadio radio, Player player) {
		//Sender is not encrypting chat
		if (radio.getCode()==0){return true;}
		//Receiver has read all permission
		if (player.hasPermission("amchat.radio.readall")||player.isOp()){return true;}
		//the player and receiver are on the same channel with the same key
		if((playerRadioCode.get(player).equals(radio.getCode()))&&(playerRadioChannel.get(player).equals(radio.getChan()))){
			return true;
		} 
		return false;	
		
	}	
	
	public boolean canPing(Player sender, Player player) {
		if(varLimitRadioChat){
			if(amcTools.getDistance(sender.getLocation(), player.getLocation()) > varRadioMaxChatDist){
				return false;
				}
			}
		return true;
	}

	public void playerRadioPing(Player sender, Player player) {
		//This works pretty simple, we use the message format to show our information
		String senderInfo = "Frequency:"+ getPlayerRadioChannel(player) + this.varRadioFreqSuffix + " Code:"+getPlayerRadioCode(player);
		String pingMessage = "*PING*"+amcTools.createMessage(player, ChatColor.YELLOW +senderInfo+"*PING*");
		amcTools.msgToPlayer(player, pingMessage);
		if (canPing(sender,player)){
		amcTools.msgToPlayer(sender, pingMessage);
		}
		
	}
	
	public static AMChat getAMChatHandle(){
		return amcChatMain;
	}
	
}//EOF
