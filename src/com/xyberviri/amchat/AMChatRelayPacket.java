package com.xyberviri.amchat;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

//This is designed to be a anti spam packet, its also designed to hold
//the encrypted messages so it only has to be jumbled up only once.

public class AMChatRelayPacket {
	private String message;					//this is the message itself. 
	private ArrayList<Player> amcRelayPlayers;	//list of players that were online at the time of message creation. 
	private ArrayList<String> recRelayID;		//When a relay receive the message it has to add it's id to the message.

	AMChatRelayPacket(String origin, String originMessage){
		this.amcRelayPlayers = new ArrayList<Player>();
		this.recRelayID = new ArrayList<String>();
		this.recRelayID.add(origin);
		setPlayers();
		this.message = originMessage;
		
	}
	//Get all the online players instead of doing it a bunch of times
	private void setPlayers(){
		for(Player player : Bukkit.getOnlinePlayers()){
			this.amcRelayPlayers.add(player);
			}
		}
	
	public ArrayList<Player> getPlayers(){
		return amcRelayPlayers;
	}
	
	//We confirm delivery of the message to the player by removing them from the internal list
	public void confirmDelivery(Player player){
		if(amcRelayPlayers.contains(player)){
			amcRelayPlayers.remove(player);
		}		
	}
	//the relay provides the id to get the message,
	//we then add the id to the list of relays. 
	public String getMsg(String relayID){
		if (!this.recRelayID.contains(relayID)){
			this.recRelayID.add(relayID);
			}
		return message;
	}

	public boolean hasRecived(String relayID){
		if(this.recRelayID.contains(relayID)){
		return true;
		} else {
			return false;
		}
	}
	
	public void setRecived(String relayID){
		if(!this.recRelayID.contains(relayID)){
			this.recRelayID.add(relayID);
		}
	}

}
