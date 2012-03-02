package com.xyberviri.amchat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

import com.xyberviri.util.XYCustomConfig;



public class AMChatRadioManager {
	AMChat amcMain;
	
	private ArrayList<AMChatRadio> 		amRadioList;		//AMChat Radio's
	private Map <String, AMChatRadio> 	amRadioHandles;		//Handles to Radios
	private Map <Player, Boolean> 		amIsBuilding;		//Is the player building a radio?
	//private Map <Player, Integer> 		amRadioStep;		//What step is the player at in construction of a tower. 
	//this.amRadioStep = new HashMap<Player,Integer>();
	private XYCustomConfig radioConfig;
	
	
	
	AMChatRadioManager(AMChat amchat){
		this.amcMain = amchat;
		this.amRadioList = new ArrayList<AMChatRadio>();
		this.amRadioHandles = new HashMap<String, AMChatRadio>();
		this.amIsBuilding = new HashMap<Player,Boolean>();
		this.radioConfig = new XYCustomConfig(amcMain,"rm.settings.yml");
		
		//if(radioConfig.get().isConfigurationSection("KX05")){
//			Map<String, Object> radioSettings = radioConfig.get().getValues(true);
//			for (Map.Entry<String, Object> entry: radioSettings.entrySet()) {
//				amcMain.logMessage(entry.getKey()+": "+entry.getValue());
//			}
//			amcMain.logMessage("SUCCESS!!");
		//}

		
//		amcRadManSerializationTest("KX05");
//		amcRadManSerializationTest("PD13");
//		amcRadManSerializationTest("PX12");
//		amcRadManSerializationTest("CD01");
//		amcRadManSerializationTest("ZX02");
//
//		amcMain.logMessage("Total Radio Objects: "+amRadioList.size());
		//radioConfig.saveToDisk();
		
	}
	

	 
	 
	public void amcRadManSerializationTest(String varString){
		AMChatRadio newRadio = new AMChatRadio(this);
		String varNewRadioHandle = genRadioID(false);

		newRadio.setName(varNewRadioHandle);
		newRadio.setOwner(varString+" owner");
		//newRadio.setLoc("world",0.0,0.0,0.0);
		newRadio.setChan(1);
		newRadio.setCode(0);
		newRadio.setPass("pass");		
		newRadio.addMember("Member1");
		newRadio.addMember("Member2");
		newRadio.addAdmin("Member1");
		

		amRadioList.add(newRadio);
		amRadioHandles.put(varNewRadioHandle, newRadio);
	}
	
//	public void amcRadManFileTest(String varString){
//		amcMain.logMessage("AMCRadManFileTest: "+varString);
//		Map<String, Object> radioSettings = new HashMap<String,Object>();
//		radioSettings.put("name", varString);
//		radioSettings.put("owner", "Xyberviri");
//		radioSettings.put("freq", "100");
//		radioSettings.put("code", "0");
//		radioSettings.put("pass", "none");
//		radioSettings.put("locw", "nether");
//		radioSettings.put("locx", "0");
//		radioSettings.put("locy", "64");
//		radioSettings.put("locz", "0");
//		ArrayList<String> radioMembers = new ArrayList<String>();
//		ArrayList<String> radioAdmins = new ArrayList<String>();
//		radioAdmins.add("Xyberviri");
//		radioMembers.add("Xyberviri");
//		radioMembers.add("rumblegoat");
//		radioSettings.put("admins", radioAdmins);
//		radioSettings.put("members", radioMembers);
//		radioConfig.get().createSection(varString, radioSettings);
//		radioConfig.saveToDisk();
//
//	}

	public void loadRadioSettings(){
		
	}
	//This creates a radio from a Object Map
	private void createRadio(Map<String,Object> radioSettings){
		
	}
	
	public void saveRadioSettings(){
		
	}
	//This creates the Object Map from a Radio
	private Map<String, Object> createRadioSettings(AMChatRadio amcRadio){
		Map<String, Object> radioSettings = new HashMap<String,Object>();
//		Map<String, Object> playerSetting = new HashMap<String,Object>();
//		playerSetting.put("radio",isRadioOn(player));
//		playerSetting.put("freq",getPlayerRadioChannel(player));	
//		playerSetting.put("code",getPlayerRadioCode(player));
//		playerSetting.put("mic",getPlayerMic(player));
//		playerSetting.put("filter",getPlayerFilter(player));
//		playerSetting.put("cutoff",getPlayerCutoff(player));
//		playerSetting.put("link",getPlayerLinkID(player));
//		//playerSettings.put(player.getDisplayName(), playerSetting);
//		//playerRadioConfig.createSection("radio-settings",playerSettings);
//		playerRadioConfig.createSection(player.getDisplayName(), playerSetting);
//		this.saveConfigPlayerRadioSettings();
		return radioSettings;
	}
	

	
	
	
	
	public void createNewRadio(Player player){
		AMChatRadio newRadio = new AMChatRadio(this);
		String varNewRadioHandle = genRadioID(false);

		newRadio.setName(varNewRadioHandle);
		newRadio.setOwner(player.getDisplayName());
		newRadio.setLoc(player.getLocation());
		newRadio.setChan(amcMain.getPlayerRadioChannel(player));
		newRadio.setCode(amcMain.getPlayerRadioCode(player));
		newRadio.setPass(""+varNewRadioHandle.hashCode());
		
		newRadio.addMember(player.getDisplayName());
		newRadio.addAdmin(player.getDisplayName());
		

		amRadioList.add(newRadio);
		amRadioHandles.put(varNewRadioHandle, newRadio);
		amcMain.logMessage(player.getDisplayName()+" created a new radio with id:"+varNewRadioHandle);
	}
	

	
	public boolean isPlayerBuilding(Player thisPlayer){
		if(amIsBuilding.containsKey(thisPlayer)){
			return amIsBuilding.get(thisPlayer);
		} else {
			this.amIsBuilding.put(thisPlayer, false);
			return false;
		}
	}
	
	


	
	public boolean isLoaded(AMChat amcMainPlugin) {
		if (this.amcMain.equals(amcMainPlugin)){			
			return true;
			}
		return false;
	}

	
	public String genRadioID(boolean useAlt){
		//Serial numbers are <HOUR><DAY><#> with the number being the number of radios with that id already.
		String newRadioIDPrefix;
		String newRadioIDString;
		int varOffset=1;
		
		if(useAlt){			
			newRadioIDPrefix="ZX";
			} 
		else{
			String[] varHour = {"Z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y"};
			String[] varDay = {"X","S","M","T","W","H","F","A","Z"};			
			Calendar this_moment = Calendar.getInstance(); 
			newRadioIDPrefix = varHour[this_moment.get(Calendar.HOUR_OF_DAY)]+varDay[this_moment.get(Calendar.DAY_OF_WEEK)];
		}

		while(true){			
			if(varOffset>9){ 	//If the id for this prefix is 10+ use that to construct the string.
				newRadioIDString=newRadioIDPrefix+varOffset;} 
			else{ 				//pad 0-9 with leading zero so we have at least 4 digits for id's.
				newRadioIDString=newRadioIDPrefix+String.format("%02d",varOffset);
				}
			
			if (!isLinkValid(newRadioIDString)){ //if not in use break.
				break;
				}
			varOffset++;
		}
		return newRadioIDString;
	}	
	
	public boolean isLinkValid(String radioID) {
	 return amRadioHandles.containsKey(radioID);
	}

	public void linkMessage(String playerLinkID, String message) {
		// TODO Auto-generated method stub
		
	}
}
