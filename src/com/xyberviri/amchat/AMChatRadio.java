package com.xyberviri.amchat;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;

public class AMChatRadio {
	AMChatRadioManager amcRadMan;		//	Handle to Radio Manager
	
	private String 		varRadioName;		//	What is this Radio's Handle KXAN for example
	private Location	varRadioLoc;		//	Where do i exist in 4d space. 
	private String		varRadioSerial;		//	This is my Serial number, its given to me by the Radio Manager.
	private String 		varRadioOwner;		//	Who owns me
	private int 		varRadioChannel;	//	What Channel are we transmitting on
	private int			varRadioCode;		//	What Code are we using to encrypt chat, 0 is disabled.
	private String 		varRadioLinkPass;	//	What password does some one need to enter so they can join
	private double		varRadioRange;		//	How far do i reach.
	private int			varRadioAntHt;		//	How Tall is my antenna.
	private boolean		varRadioIsValid;	//	Is this radio valid? Can it Transmit?
	private boolean		varRadioIsAdmin;	//	Is this a admin radio?
	
	//TODO: Admin Radio
	//TODO: Player Radio
	// Server Radios are like player radios but don't Require Range checks
	// They also don't have a physical tower and the location is set buy the admins
	// Encryption, Passwords and Range Checks are Disabled.
	// Block Validation is Disabled.
	// Admin List returns Server Operator List 
	// Name Checks are Disabled
	
	private ArrayList<String> radioMembers = new ArrayList<String>();
	private ArrayList<String> radioAdmins = new ArrayList<String>();
	private ArrayList<AMChatRadio> radioNetwork = new ArrayList<AMChatRadio>(); //These are radios that are linked to me
	
	AMChatRadio(AMChatRadioManager amChatRadioManager){
		this.amcRadMan = amChatRadioManager;
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
	
	//Getter/Setter:Name
	public String getName() {return varRadioName;}
	public void setName(String varRadioName) {this.varRadioName = varRadioName;}

	//Getter/Setter:Location
	public Location getLoc() {return varRadioLoc;}
	public void setLoc(Location varRadioLoc) {
		this.varRadioLoc = varRadioLoc;
		}	
	public void setLoc(World world,Double locX,Double locY,Double locZ) {
		this.varRadioLoc = new Location(world, locX, locY, locZ);
		}		
	public void setLoc(String world,Double locX,Double locY,Double locZ) {
		this.varRadioLoc = new Location(this.amcRadMan.amcMain.getServer().getWorld(world), locX, locY, locZ);
		}
	

	//Get Members
	public ArrayList<String> getMembers() {return radioMembers;}
	
	//Get/Add/Del Admins
	public ArrayList<String> getAdmins(){return radioAdmins;}
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
	
	
}
