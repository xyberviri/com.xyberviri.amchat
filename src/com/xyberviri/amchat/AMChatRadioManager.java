package com.xyberviri.amchat;



public class AMChatRadioManager {
	AMChat amcMain;
	
	
	
	AMChatRadioManager(AMChat amchat){
		this.amcMain = amchat;
	}
	
	
	
	
	
	
	public boolean isLoaded(AMChat amcMainPlugin) {
		if (this.amcMain.equals(amcMainPlugin)){
			return true;
			}
		return false;
	}
}
