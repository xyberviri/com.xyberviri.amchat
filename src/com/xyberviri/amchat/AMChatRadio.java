package com.xyberviri.amchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class AMChatRadio {
	AMChatRadioManager amcRadMan;		//	Handle to Radio Manager
	
	private String 		varRadioName;		//	What is this Radio's Handle KXAN for example
	private String 		varRadioOwner;		//	Who owns me
	private Location	varRadioLoc;		//	Where do i exist in 4d space. 
	private int 		varRadioChannel;	//	What Channel are we transmitting on
	private int			varRadioCode;		//	What Code are we using to encrypt chat, 0 is disabled.
	private String 		varRadioLinkPass;	//	What password does some one need to enter so they can join

	//we don't need to save any of these values
	//private double	varRadioRange;		//	How far do i reach.
	//private int		varRadioAntHt;		//	How Tall is my antenna.
	private boolean	varRadioIsValid;	//	Is this radio valid? Can it Transmit?
	private boolean	varRadioIsAdmin;	//	Is this a admin radio?
	private int		varRadioABlocks;	//	Antenna blocks
	private int		varRadioIBlocks;	//	Iron blocks
	private int		varRadioGBlocks;	//	Gold blocks
	private int		varRadioDBlocks;	//	Diamond
	private int		varRadioOBlocks;	//	Obsidian
	
	//TODO: Admin Radio
	//TODO: Player Radio
	//TODO: Plugin Radio
	// Server Radios are like player radios but don't Require Range checks
	// They also don't have a physical tower and the location is set buy the admins
	// Encryption, Passwords and Range Checks are Disabled.
	// Block Validation is Disabled.
	// Admin List returns Server Operator List 
	// Name Checks are Disabled
	
	private ArrayList<String> radioMembers;			//List of people who are authorized to use me
	private ArrayList<String> radioAdmins;			//List of Admins for this radio
	private ArrayList<AMChatRadio> radioNetwork; 	//These are other radios that are linked to me
	
	AMChatRadio(AMChatRadioManager amChatRadioManager){
		this.amcRadMan = amChatRadioManager;
		this.radioMembers = new ArrayList<String>();
		this.radioAdmins = new ArrayList<String>();
		this.radioNetwork = new ArrayList<AMChatRadio>();

		this.varRadioIsValid=false;	//	Is this radio valid? Can it Transmit?
		this.varRadioIsAdmin=false;	//	Is this a admin radio?
		this.varRadioABlocks=0;		
		this.varRadioIBlocks=0;		//	Iron blocks
		this.varRadioGBlocks=0;		//	Gold blocks
		this.varRadioDBlocks=0;		//	Diamond
		this.varRadioOBlocks=0;		//	Obsidian
		}
	
	

	
//	//This is the handle that another Radio is using to send us a relay message
//	public void rRelay(AMChatRelayPacket amcRelayPacket){
//		
//	}	
//	
//	//Relay Chat Message, this has to be called from some where else.
//	//It will check the packet to ensure this is not spam. 
//	private void sendRelay(AMChatRelayPacket amcRelayPacket){
//		
//	}
	public void chkValid(){
		if (varRadioIsAdmin){
			setValid(true);
		} else {
			update();
		}
	}
	
	private void update(){
		setValid(false);
		this.varRadioABlocks=0;		//  Antenna blocks
		this.varRadioIBlocks=0;		//	Iron blocks
		this.varRadioGBlocks=0;		//	Gold blocks
		this.varRadioDBlocks=0;		//	Diamond
		this.varRadioOBlocks=0;		//	Obsidian
		
		int x = varRadioLoc.getBlockX();
		int z = varRadioLoc.getBlockZ();
		World world = varRadioLoc.getWorld();
		
		for(int y = varRadioLoc.getBlockY(); y > varRadioLoc.getWorld().getMaxHeight();){
			Material blockType = world.getBlockAt(x, y, z).getType();
		if(blockType.equals(Material.JUKEBOX)){
			//TODO:SignSearchRouting();
			setValid(true);
			} else if(blockType.equals(Material.IRON_FENCE)){
				this.varRadioABlocks++;
				} else if(blockType.equals(Material.IRON_BLOCK)){
					this.varRadioIBlocks++;
					} else if(blockType.equals(Material.GOLD_BLOCK)){
						this.varRadioGBlocks++;
						} else if(blockType.equals(Material.DIAMOND_BLOCK)){
							this.varRadioDBlocks++;
							} else if(blockType.equals(Material.OBSIDIAN)){
								this.varRadioOBlocks++;
								} else{
									break;
									}
			y++;
		}	
	}

	//Is this a valid Radio tower should we talk to it, does it work.
	public boolean isValid() {if (varRadioIsAdmin){return true;} else {return varRadioIsValid;}}
	public void setValid(boolean b){this.varRadioIsValid=b;}
	
	//Getter/Setter:Admin Flag
	public boolean isAdmin(){return varRadioIsAdmin;}
	public void setAdmin(boolean b){this.varRadioIsAdmin=b;}

	//Getter/Setter:Owner
	public String getOwner() {return varRadioOwner;}
	public void setOwner(String varRadioOwner) {this.varRadioOwner = varRadioOwner;}

	//Getter/Setter:Frequency
	public int getChan() {return varRadioChannel;}
	public void setChan(int varRadioChannel) {this.varRadioChannel = varRadioChannel;}

	//Getter/Setter:Code
	public int getCode() {return varRadioCode;}
	public void setCode(int varRadioCode) {this.varRadioCode = varRadioCode;}
	
	//Getter/Setter:Password
	public String getPass() {return varRadioLinkPass;}
	public void setPass(String varRadioLinkPass) {this.varRadioLinkPass = varRadioLinkPass;}
	public boolean chkPass(String varInputPass){return varRadioLinkPass.equals(varInputPass);}
	
	//Getter/Setter:Name
	public String getName() {return varRadioName;}
	public void setName(String varRadioName) {this.varRadioName = varRadioName;}

	//Getter/Setter:Location
	public Location getLoc() {return varRadioLoc;}
	public void setLoc(Location varRadioLoc) {
		this.varRadioLoc=varRadioLoc;
		}	
	public void setLoc(World world,Double locX,Double locY,Double locZ) {this.varRadioLoc = new Location(world, locX, locY, locZ);	}		
	public void setLoc(String world,Double locX,Double locY,Double locZ) {this.varRadioLoc = new Location(this.amcRadMan.amcMain.getServer().getWorld(world), locX, locY, locZ);}
	public void setLoc(String world,String locX,String locY,String locZ) {
		this.varRadioLoc = new Location(this.amcRadMan.amcMain.getServer().getWorld(world),Double.valueOf(locX), Double.valueOf(locY), Double.valueOf(locZ));
		}
	
	
	//Get/Set/Add/Del Members
	public ArrayList<String> getMembers() {return radioMembers;}
	public void setMembers(ArrayList<String>radioMembers){
		this.radioMembers=radioMembers;
		}
	public boolean addMember(String radioMember) {
		if(!this.radioMembers.contains(radioMember)){
			this.radioMembers.add(radioMember);
			return true;
			} 
		return false;		
	}
	public boolean delMember(String radioMember) {
		if (this.radioMembers.contains(radioMember)){
			this.radioMembers.remove(radioMember);
			return true;
			}
		return false;
	}
	
	//Get/Set/Add/Del Admins
	public ArrayList<String> getAdmins(){return radioAdmins;}
	public void setAdmins(ArrayList<String>radioAdmins){
		this.radioAdmins=radioAdmins;
		}
	
	public boolean addAdmin(String radioMember) {
		if(!this.radioAdmins.contains(radioMember)){
			this.radioAdmins.add(radioMember);
			return true;
			} 
		return false;		
	}
	public boolean delAdmin(String radioMember) {
		if (this.radioAdmins.contains(radioMember)){
			this.radioAdmins.remove(radioMember);
			return true;
			}
		return false;
	}
	
	//Get/Add/Del Network Partners.
	public boolean isRadioNeworkPartner(AMChatRadio otherRadio) {
		return radioNetwork.contains(otherRadio);
		}
	
	public boolean setRadioNetworkPartner(AMChatRadio otherRadio) {
		if (!radioNetwork.contains(otherRadio)){
			this.radioNetwork.add(otherRadio);
			return true;
		}
		return false;
	}
	
	public boolean unsetRadioNetworkPartner(AMChatRadio otherRadio) {
		if (radioNetwork.contains(otherRadio)){
			this.radioNetwork.remove(otherRadio);
			return true;
		}
		return false;
	}




	public Map<String, Object> getSettings() {
		Map<String, Object> radioSetting = new HashMap<String,Object>();
		radioSetting.put("radio-id", varRadioName);
		radioSetting.put("owner", varRadioOwner);
		radioSetting.put("freq",varRadioChannel);
		radioSetting.put("code",this.varRadioCode);
		radioSetting.put("pass",this.varRadioLinkPass);
		radioSetting.put("locw",this.varRadioLoc.getWorld().getName());
		radioSetting.put("locx",varRadioLoc.getX());
		radioSetting.put("locy",varRadioLoc.getY());
		radioSetting.put("locz",varRadioLoc.getZ());
		radioSetting.put("admins",this.radioAdmins);
		radioSetting.put("members",this.radioMembers);
		radioSetting.put("radio-isadmin",varRadioIsAdmin);
		return radioSetting;	
	}


	
	
}//EOF
