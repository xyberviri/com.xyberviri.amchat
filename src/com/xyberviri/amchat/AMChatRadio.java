package com.xyberviri.amchat;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.World;

public class AMChatRadio {
	AMChatRadioManager amcRadMan;		//	Handle to Radio Manager
	
	private String 		varRadioName;		//	What is this Radio's Handle KXAN for example
	private String 		varRadioOwner;		//	Who owns me
	private Location	varRadioLoc;		//	Where do i exist in 4d space. 
	private int 		varRadioChannel;	//	What Channel are we transmitting on
	private int			varRadioCode;		//	What Code are we using to encrypt chat, 0 is disabled.
	private String 		varRadioLinkPass;	//	What password does some one need to enter so they can join

	private double	varRadioRange;		//	How far do i reach.
	private int		varRadioAntHt;		//	How Tall is my antenna.
	private boolean	varRadioIsValid;	//	Is this radio valid? Can it Transmit?
	private boolean	varRadioIsAdmin;	//	Is this a admin radio?
	private int		varRadioIBlocks;	//	Iron blocks
	private int		varRadioGBlocks;	//	Gold blocks
	private int		varRadioDBlocks;	//	Diamond
	private int		varRadioOBlocks;	//	Obsidian
	
	//TODO: Admin Radio
	//TODO: Player Radio
	// Server Radios are like player radios but don't Require Range checks
	// They also don't have a physical tower and the location is set buy the admins
	// Encryption, Passwords and Range Checks are Disabled.
	// Block Validation is Disabled.
	// Admin List returns Server Operator List 
	// Name Checks are Disabled
	
	private ArrayList<String> radioMembers;
	private ArrayList<String> radioAdmins;
	private ArrayList<AMChatRadio> radioNetwork; //These are radios that are linked to me
	
	AMChatRadio(AMChatRadioManager amChatRadioManager){
		this.amcRadMan = amChatRadioManager;
		this.radioMembers = new ArrayList<String>();
		this.radioAdmins = new ArrayList<String>();
		this.radioNetwork = new ArrayList<AMChatRadio>();
		
		setVarRadioRange(0.0);		//	How far do i reach.
		varRadioAntHt=0;		//	How Tall is my antenna.
		varRadioIsValid=false;	//	Is this radio valid? Can it Transmit?
		varRadioIsAdmin=false;	//	Is this a admin radio?
		varRadioIBlocks=0;		//	Iron blocks
		varRadioGBlocks=0;		//	Gold blocks
		varRadioDBlocks=0;		//	Diamond
		varRadioOBlocks=0;		//	Obsidian
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
	public void setLoc(Location varRadioLoc) {this.varRadioLoc=varRadioLoc;}	
	public void setLoc(World world,Double locX,Double locY,Double locZ) {this.varRadioLoc = new Location(world, locX, locY, locZ);	}		
	public void setLoc(String world,Double locX,Double locY,Double locZ) {this.varRadioLoc = new Location(this.amcRadMan.amcMain.getServer().getWorld(world), locX, locY, locZ);}
	

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

	public double getRange() {
		return varRadioRange;
	}

	public void setVarRadioRange(double varRadioRange) {
		this.varRadioRange = varRadioRange;
	}	
	
	
}
