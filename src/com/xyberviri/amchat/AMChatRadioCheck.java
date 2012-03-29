package com.xyberviri.amchat;

public class AMChatRadioCheck implements Runnable {
	AMChat amcMain;
	
	AMChatRadioCheck(AMChat amcMain){
		this.amcMain=amcMain;
	}

	public void run() {
		amcMain.amcRadMan.amcRadManComCheck();         
    }
}