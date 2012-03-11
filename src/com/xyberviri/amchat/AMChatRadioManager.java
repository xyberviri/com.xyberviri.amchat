package com.xyberviri.amchat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.xyberviri.util.XYCustomConfig;



public class AMChatRadioManager {
	AMChat amcMain;
	
	private ArrayList<AMChatRadio> 		amRadioList;		//AMChat Radio's
	private Map <String, AMChatRadio> 	amRadioHandles;		//Handles to Radios
	private	Map <String, ArrayList<String>>  ownerTowers = new HashMap<String,ArrayList<String>>(); // These are handled here since AMChat is more for portable radio. 
	private XYCustomConfig radioConfig;
	private long varLastSave;
	
	
	AMChatRadioManager(AMChat amchat){
		this.amcMain = amchat;
		this.amRadioList = new ArrayList<AMChatRadio>();
		this.amRadioHandles = new HashMap<String, AMChatRadio>();
		this.radioConfig = new XYCustomConfig(amcMain,"rm.settings.yml");
		this.varLastSave = System.currentTimeMillis();
		loadRadioSettings();
	}
	
	public void amcRadManComCheck(){
		if (!amRadioList.isEmpty()){
			List<AMChatRadio> badRadios= new ArrayList<AMChatRadio>();
			for(AMChatRadio amcRadio: amRadioList){
				if(!amcRadio.chkValid()){
					badRadios.add(amcRadio);
				}
			}
			if(!badRadios.isEmpty()){
				for(AMChatRadio amcRadio: badRadios){
					deleteRadio(amcRadio);
				}
			}
			autoSave();
		}
	}
	
	public void autoSave(){
		if (!amRadioList.isEmpty()){			
			long currentTime = System.currentTimeMillis();
			if (varLastSave+amcMain.varScheduleSaveRate<currentTime){
				amcMain.logMessage("Auto-Saving Fixed Radio information.");
				this.varLastSave = currentTime;
			}	
		}
	}
	
	//Save all the Radios to a file
	public void saveRadioSettings(){
		if(amRadioList.isEmpty()){
			amcMain.logMessage("note:no radios have been created, no save file will be created.");
		} else {
			amcMain.logMessage("Saving radio tower settings.");
		Map<String, Object> radManSettings = new HashMap<String,Object>();
		for(AMChatRadio varRadio :  amRadioList){		
			amcMain.logMessage(">"+varRadio.getName());
			radManSettings.put(varRadio.getName(), varRadio.getSettings());
		}
		this.radioConfig.get().createSection("radio-settings",radManSettings);
		this.radioConfig.saveToDisk();
		amcMain.logMessage(amRadioList.size()+" radio settings have been saved to file.");	
		}
	}	
	
	//Load all the Radios to the server from the settings file.
	public void loadRadioSettings(){
		if(radioConfig.get().isConfigurationSection("radio-settings")){
			ConfigurationSection radioSettingsFile = radioConfig.get().getConfigurationSection("radio-settings");
			
			if(radioSettingsFile.getKeys(false).isEmpty()){
				return;
			} else{
				int x=0;
				amcMain.logMessage("Loading radios.....");
				Set<String> varSetName = radioSettingsFile.getKeys(false);
				for(String radioSettings : varSetName){	
					
					Map<String, Object> radioSetting = new HashMap<String,Object>();
					radioSetting = radioSettingsFile.getConfigurationSection(radioSettings).getValues(true);
					String varStatus="SUCCESS";
					
					if(createRadio(radioSetting)){						
						x++;
					} else {varStatus="FAILED";}
					amcMain.logMessage("Loading: "+radioSettings+" "+varStatus);
					
				}
				amcMain.logMessage("Complete "+x+" radios loaded");
			}
		} else {
			amcMain.logMessage("No rm.settings.yml file detected, has anyone built any towers?");
		}
	}
	
	//This creates a radio from a Object Map
	//The Suppression warring is for the conversions of the arrays.
	@SuppressWarnings("unchecked")
	private boolean createRadio(Map<String,Object> radioSettings){
		boolean b=true;
		if(radioSettings.containsKey("radio-id")){
			AMChatRadio newRadio = new AMChatRadio(this);
			newRadio.setName((String) radioSettings.get("radio-id"));
			if(radioSettings.containsKey("owner")){
				newRadio.setOwner((String) radioSettings.get("owner"));	
			}
			if(radioSettings.containsKey("freq")){
				newRadio.setChan((Integer) radioSettings.get("freq"));}
			if(radioSettings.containsKey("code")){
				newRadio.setCode((Integer) radioSettings.get("code"));}
			if(radioSettings.containsKey("pass")){
				newRadio.setPass((String) radioSettings.get("pass"));}
			if(radioSettings.containsKey("locw")
			 &&radioSettings.containsKey("locx")
			 &&radioSettings.containsKey("locy")
			 &&radioSettings.containsKey("locz")){
				double x,y,z;
				x=(Double) radioSettings.get("locx");
				y=(Double) radioSettings.get("locy");
				z=(Double) radioSettings.get("locz");
				Location varLoc = new Location(Bukkit.getServer().getWorld((String) radioSettings.get("locw")), x, y,z);
				newRadio.setLoc(varLoc);
			}
			
			//Arrays freak eclipse out when trying to read these back. 
			if(radioSettings.containsKey("admins")){
				Object objAdmin=radioSettings.get("admins");
				if (objAdmin instanceof ArrayList<?>){
				newRadio.setAdmins((ArrayList<String>) objAdmin);	
				}				
			}
			if(radioSettings.containsKey("members")){
				Object objMembers=radioSettings.get("members");
				if (objMembers instanceof ArrayList<?>){
				newRadio.setMembers((ArrayList<String>) objMembers);	
				}	
			}
			if(radioSettings.containsKey("radio-isadmin")){
				newRadio.setAdmin((Boolean) radioSettings.get("radio-isadmin"));}	
			//If ever thing was loaded correctly then we add it to the list of radios
			//Also note by not putting it in this list it wont be saved. 
			if(b){
				//newRadio.chkValid();
			this.amRadioList.add(newRadio);
			this.amRadioHandles.put((String) radioSettings.get("radio-id"), newRadio);
			addOwnerRadio((String) radioSettings.get("owner"),(String) radioSettings.get("radio-id"));
			}
		} 
		else{b=false;}
	
		return b;		
	}
	


	//Creates a new radio, this is from a player perspective. 
	public void createNewRadio(Player player,Location radioLocation){
		AMChatRadio newRadio = new AMChatRadio(this);
		String varNewRadioHandle = genRadioID(false);
		String varNewPass="";
		newRadio.setName(varNewRadioHandle);
		newRadio.setOwner(player.getDisplayName());
		newRadio.setLoc(radioLocation);
		newRadio.setChan(amcMain.getPlayerRadioChannel(player));
		newRadio.setCode(amcMain.getPlayerRadioCode(player));		
		newRadio.addMember(player.getDisplayName());
		newRadio.addAdmin(player.getDisplayName());
		varNewPass=""+varNewRadioHandle.hashCode();
		newRadio.setPass(varNewPass);
		this.amRadioList.add(newRadio);
		this.amRadioHandles.put(varNewRadioHandle, newRadio);	
		addOwnerRadio(player.getDisplayName(),varNewRadioHandle);
		//newRadio.linkPlayer(player);
		//amcMain.setPlayerLinkID(player, varNewRadioHandle);
		//player.setCompassTarget(radioLocation);		
		
		linkPlayerToRadio(player, varNewRadioHandle);	
		amcMain.amcTools.msgToPlayer(player, "You have created a new Radio.");
		amcMain.amcTools.msgToPlayer(player, "MCC-ID: ",varNewRadioHandle);
		amcMain.amcTools.msgToPlayer(player, "PASSWD: ",varNewPass);
		amcMain.logMessage(player.getDisplayName()+" created a new radio");
		amcMain.logMessage("id:"+varNewRadioHandle+" default passwd:"+varNewPass);
	}
	
	public void deleteRadio(AMChatRadio delThisRadio){
		String varOwner = delThisRadio.getOwner();
		String varRadio = delThisRadio.getName();
		amcMain.logMessage("Destroying: "+varRadio);
		delThisRadio.lcast("radio has been destroyed");
		delOwnerRadio(varOwner, varRadio);
		amRadioList.remove(delThisRadio);
		amRadioHandles.remove(varRadio);	
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
	
	public boolean isLinkValid(String linkID) {
		if(linkID.equalsIgnoreCase("none")){return false;}
	 return amRadioHandles.containsKey(linkID);
	}
	
	public boolean isPassValid(String linkID,String linkPass){
		if(amRadioHandles.containsKey(linkID)&&amRadioHandles.get(linkID).chkPass(linkPass)){
			return true;
		} 
		return false;
	}
	
	public boolean isPlayerRadioAdmin(Player player,String linkID){
		boolean b = false;
		if(player.hasPermission("amchat.radio.override.admin")||player.isOp()){
			b=true;
		}
		else if(amRadioHandles.containsKey(linkID)){
			b=amRadioHandles.get(linkID).isPlayerAdmin(player.getDisplayName());
		}
		return b;		
	}
	
	public boolean playerNeedsPass(Player player,String linkID){
		boolean b=true;
		if(amRadioHandles.containsKey(linkID)){
			if(player.hasPermission("amchat.radio.override.admin")||player.isOp()||amRadioHandles.get(linkID).chkPass("")||amRadioHandles.get(linkID).isPlayerMember(player.getDisplayName())||amRadioHandles.get(linkID).isPlayerAdmin(player.getDisplayName())||amRadioHandles.get(linkID).isPlayerOwner(player.getDisplayName())){
				b=false;
			}
		}
		return b;
	}

	public void linkMessage(String linkID, String message) {
		   amRadioHandles.get(linkID).bcast(message);
	}
	
	public AMChatRadio getRadio(String varHandle){
		return amRadioHandles.get(varHandle);
	}
	
	public ArrayList<AMChatRadio> getRadioList(){
		return amRadioList;
	}
	
	public boolean linkPlayerToRadio(Player player,String linkID){
		//canLink
		if(amRadioHandles.containsKey(linkID)){
			AMChatRadio targetRadio=amRadioHandles.get(linkID);
			if(amcMain.canLink(targetRadio, player)){
				if(targetRadio.roomToJoin()){
					String curLinkID = amcMain.getPlayerLinkID(player);
					if(isLinkValid(curLinkID)){amRadioHandles.get(curLinkID).delUser(player);}
			
					targetRadio.linkPlayer(player);	
					amcMain.setPlayerLinkID(player, targetRadio.getName());
					amcMain.tunePlayerRadioChannel(player,targetRadio.getChan());
					amcMain.setPlayerRadioCode(player, targetRadio.getCode());
					player.setCompassTarget(targetRadio.getLoc());
					
					amcMain.amcTools.msgToPlayer(player, "A link has successfully been established to ",linkID);
					return true;
				} else {amcMain.amcTools.errorToPlayer(player, "Sorry, you are unable to link to that radio because it is at max capacity.");}
			} else {
				amcMain.amcTools.errorToPlayer(player, "Sorry, you are outside of the linkable range.");
			}
		} else {
			amcMain.amcTools.errorToPlayer(player, "Sorry, that link id was invalid.");
		}
		amcMain.setPlayerLinkID(player,"none");
		return false;
	}
	
	public void unlinkPlayerFromRadio(Player player,String linkID,boolean unsetLink){
		if(amRadioHandles.containsKey(linkID)&& amRadioHandles.get(linkID).isPlayerUser(player)){
			amRadioHandles.get(linkID).delUser(player);
			amcMain.amcTools.msgToPlayer(player, "you have been disconnected from ", linkID);	
		}
		if(unsetLink){
		amcMain.setPlayerLinkID(player,"none");
		}
	}
		
	//Add/Del/Get Owners Radio list
	//These functions are to get a list of radios by owner
	//Add radio to Owner's list, checks if the key already exists if so dont change anything, if there is no key in the parent hash, add a empty array.
	public void addOwnerRadio(String varName,String varRadioID){
		if(ownerTowers.containsKey(varName)){
			ArrayList<String> exTowers = ownerTowers.get(varName);
			if(!exTowers.contains(varRadioID)){
				exTowers.add(varRadioID);
				ownerTowers.put(varName, exTowers);
			}
		} else {
			ArrayList<String> exTowers = new ArrayList<String>();
			exTowers.add(varRadioID);
			ownerTowers.put(varName, exTowers);
		}
	}

	//Del radio from Owner's list, if the key exists its removed from the array and readded to the parent hash,  if there is no key in the parent hash, add a empty array.
	public void delOwnerRadio(String varName,String varRadioID){
		if(ownerTowers.containsKey(varName)){
			ArrayList<String> exTowers = ownerTowers.get(varName);
			if(exTowers.contains(varRadioID)){
				exTowers.remove(varRadioID);
				ownerTowers.put(varName, exTowers);
			}
		} else {
			ArrayList<String> exTowers = new ArrayList<String>();
			ownerTowers.put(varName, exTowers);
		}
	}
	
	//Gets a ArrayList of Radio id's, if there are no owned radios a empty list is returned. 
	public ArrayList<String> getOwnerRadios(String varName){
		ArrayList<String> exTowers = new ArrayList<String>();
		if(ownerTowers.containsKey(varName)){
		exTowers = ownerTowers.get(varName);
		}
		return exTowers;
	}
	

}//EOF
