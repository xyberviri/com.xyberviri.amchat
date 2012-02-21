package com.xyberviri.amchat;

import java.util.ArrayList;

public class AMChatRelayPacket {
	private AMChat amcMain;
	private String message;
	private String encmessage;
	private Boolean isEncrypted;
	private ArrayList<String> received;

	AMChatRelayPacket(AMChat amcMain, String message){
		this.amcMain = amcMain;
		this.message = message;
		this.encmessage="";
		this.isEncrypted=false;
		this.received = new ArrayList<String>();
	}
	
	public String getMsg(){
		return message;
	}
	
	public String getCrypt(){
		if(isEncrypted){
			return encmessage;
		} else {
			this.encmessage = amcMain.amcTools.createBadMessage(message);
			return encmessage;
		}
	}	
	
	public boolean hasRecived(String chkName){
		if (received.contains(chkName)){
			return true;
		} else {
			received.add(chkName);
			return false;
		}
	}
}
