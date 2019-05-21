package com.xyberviri.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class XYCustomConfig {
	private FileConfiguration customConfig=null; 	//Configuration handle
	private File customConfigFile = null;		//File where we store data.
	private String dataFile;			//Internal handles for folder and file.
	Plugin p;
	
	public XYCustomConfig(Plugin p,String dataFile){
		this.p = p;
		this.dataFile = dataFile;
		this.customConfig = get();
	}	
	
	private void reloadcustomConfig(){
		if(customConfigFile==null){
			customConfigFile=new File(p.getDataFolder(),dataFile);
		}
		
		customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
		
		InputStream defConfigStream = p.getResource(dataFile);
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			customConfig.setDefaults(defConfig);
	}}
	
	public FileConfiguration get(){
		if (customConfig==null){
			reloadcustomConfig();
			}
		return customConfig;			
	}
	
	public void saveToDisk(){
		if (customConfigFile==null||customConfig==null){			
			return;}
		try{
			customConfig.save(customConfigFile);			
		} catch (IOException ex){
		}
	}
}
