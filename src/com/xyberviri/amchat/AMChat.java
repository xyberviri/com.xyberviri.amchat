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

import com.xyberviri.amchat.radio.Radio;

public class AMChat extends JavaPlugin {
	private static final Logger amcLogger = Logger.getLogger("Minecraft");
	private static AMChat amcChatMain;				//handle for this plugin
	private static AMCTools amcTools;				//toolkit for AMChat
	private static AMChatSettingsManager amcSetMan;	//Settings Manager
	private AMChatListener amcListener;				//Listener
	private AMChatCmd amcCmd;						//AMC Command control

	
	PluginDescriptionFile amcPdf;			//Plugin Description File
	FileConfiguration amcConfig;			//config.yml file

	@Override
	public void onEnable() {
		amcChatMain = this;
		amcTools = new AMCTools();
		amcSetMan = new AMChatSettingsManager();
		
		this.amcPdf = this.getDescription();
		this.amcConfig = this.getConfig();

		this.amcCmd = new AMChatCmd();
		this.amcListener = new AMChatListener();
		
		this.getCommand("am").setExecutor(amcCmd);
		this.getServer().getPluginManager().registerEvents(amcListener, this);
		
		logMessage("Enabled");
	}
	
	@Override
	public void onDisable() {
		amcSetMan.saveConfigPlayerRadioSettings();
		logMessage("Disabled.");
	}
	
	public static AMChat get(){return amcChatMain;}
	public static AMCTools tools(){return amcTools;}
	public static AMChatSettingsManager settings(){return amcSetMan;}
	public static Radio player(Player player){return amcSetMan.player(player);}
	// Log info message to console
	protected static void logMessage(String message){
		amcLogger.info("["+amcChatMain.amcPdf.getName()+"] "+message);}	
	// Log error message to console, disable plugin. These really never should occur, if they do we need to shut down the plugin and let the sever manager know.  
	protected static void logError(String message){
		amcLogger.severe("["+amcChatMain.amcPdf.getName()+"] WARNING "+message);
		amcLogger.severe("["+amcChatMain.amcPdf.getName()+"] WARNING Plugin auto disable triggered.");
		if (amcChatMain.isEnabled()){amcChatMain.getServer().getPluginManager().disablePlugin(amcChatMain);
		amcLogger.severe("["+amcChatMain.amcPdf.getName()+"] WARNING "+amcChatMain.amcPdf.getName()+" has been disabled automatically as a safty measure.");
		amcLogger.severe("["+amcChatMain.amcPdf.getName()+"] WARNING Contact developer!!!");}}	
}//EOF
