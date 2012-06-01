package com.xyberviri.amchat.events;

import org.bukkit.entity.Player;

public class AMChatPlayerChat extends AMChatEvent {
	private Player player;
	private String message;
	private int freq;
	private int code;
	private boolean isRadio;
	
	public AMChatPlayerChat(Player player, String message, boolean isRadio, int freq, int code) {
		this.setPlayer(player);
		this.setMessage(message);
		this.setRadio(isRadio);
		this.setFreq(freq);
		this.setCode(code);
	}
	public Player getPlayer() {return player;}
	public void setPlayer(Player player) {this.player = player;}
	public String getMessage() {return message;}
	public void setMessage(String message) {this.message = message;}
	public int getFreq() {return freq;}
	public void setFreq(int freq) {this.freq = freq;}
	public int getCode() {return code;}
	public void setCode(int code) {this.code = code;}
	public boolean isRadio() {return isRadio;}
	public void setRadio(boolean isRadio) {this.isRadio = isRadio;}
}
