package com.xyberviri.amchat;



public class AMChatRadioManager {
	AMChat amcMain;
	private boolean isLoaded = false;
	
	
	
	AMChatRadioManager(AMChat amchat){
		this.amcMain = amchat;
		this.isLoaded = true;
	}
	
	
	
	
	
	
	public boolean isLoaded(){
		return this.isLoaded;
	}
}
