package com.xyberviri.amchat.radio;

import java.io.Serializable;
import java.util.ArrayList;

public class Radio implements Serializable  {
	private static final long serialVersionUID = 1L;
	private State vMic,vRadio,vFilter,vClassicMode;
	private int vFrequency,vCode,vCutOff;
	private String vCryptKey;
	private ArrayList<String> vKeySet = new ArrayList<String>();

	public Radio(int frequency){
		vKeySet = new ArrayList<String>();
		this.setFrequency(frequency);
		this.setSquelchCode(0);
		this.setEncryptionKey();
		this.setRadio(State.On);
		this.setMic(State.On);
		this.setFilter(State.Off);
		this.setCutoff(0);
		this.setClassicMode(State.Off);
	}
	
	public Radio(int frequency,int squelchcode,int cutoff,String encryptionkey,State radio,State mic, State filter){
		vKeySet = new ArrayList<String>();
		this.setFrequency(frequency);
		this.setSquelchCode(squelchcode);
		this.setEncryptionKey(encryptionkey);
		this.setRadio(radio);
		this.setMic(mic);
		this.setFilter(filter);
		this.setCutoff(cutoff);
		this.setClassicMode(State.Off);
	}
	
	public Integer getFrequency() {return vFrequency;}
	public void setFrequency(int frequency) {this.vFrequency = frequency;}

	public Integer getSquelchCode() {return vCode;}
	public void setSquelchCode(int code) {this.vCode = code;}	
	
	public String getEncryptionKey(){return vCryptKey;}
	public String setEncryptionKey(){return setEncryptionKey("");}
	public String setEncryptionKey(String vKey){
		if(vKey.equalsIgnoreCase("")){vKey = "none";}
		this.vCryptKey = vKey;
		return vCryptKey;
	}
	
	public ArrayList<String> getKeySet() {return vKeySet;}
	public void setKeySet(ArrayList<String> vKeySet) {this.vKeySet = vKeySet;}
	public boolean hasKey(String checkKey){return vKeySet.contains(checkKey);}
	public boolean delKey(String oldKey){return vKeySet.remove(oldKey);}
	public boolean addKey(String newKey){
		if(vKeySet.contains(newKey)){return false;} 
		else{return vKeySet.add(newKey);}
	}

	public State getRadio() {return vRadio;}
	public void  setRadio(State radioState) {this.vRadio = radioState;}
	
	public State getMic() {return vMic;}
	public void  setMic(State transmitterState){this.vMic = transmitterState;}
	
	public Integer 	getCutoff() {return vCutOff;}
	public void 	setCutoff(int cutoff) {this.vCutOff = cutoff;}
	
	public State getFilter() {return vFilter;}
	public void  setFilter(State vFilter) {this.vFilter = vFilter;}
	
	public void tuneUp(){this.vFrequency++;}
	public void tuneUp(int amount){this.vFrequency +=amount;}
	
	public void tuneDn(){this.vFrequency--;}
	public void tuneDn(int amount){this.vFrequency -=amount;}
	
	public void mute(){this.setMic(State.Off);}
	public void unmute(){this.setMic(State.On); }
	
	public void turnOn(){this.setRadio(State.On); }
	public void turnOff(){this.setRadio(State.Off); } 
	
	public void setClassicMode(State ClassicMode) {
		this.vClassicMode = ClassicMode;
	}
	
	//Information Methods
	//Does this radio use a squelch , code when transmitting.
	public boolean isSquelched(){
		return vCode != 0;
	}
	//Does this radio use an encryption key.
	public boolean isSecure(){
		return !vCryptKey.equalsIgnoreCase("none");
		}
	//Is this radio on (and therefore able to send/receive transmissions).
	public boolean isRadioOn(){
		return vRadio.equals(State.On);}
	//Can this radio send transmissions.
	public boolean canSend(){
		return vRadio.equals(State.On) && vMic.equals(State.On);}	
	 
	//Can this radio receive a transmission from another radio.	
	public boolean canRecieve(Radio transmitter){	
		if(!this.isRadioOn()){
			return false;}//This radio is off, hence we can't receive anything.
		if(!transmitter.canSend()){ 
			return false;}//The other radio can't send us messages.		
		if(transmitter.getFrequency() > (this.getFrequency() + this.getCutoff())){
			return false; //The transmitting frequency is above our cutoff
		}		
		if (transmitter.getFrequency() < (this.getFrequency() - this.getCutoff())){
			return false; //The transmitting frequency is below our cutoff
		}		
		//Squelch mode
		//When classic mode is enabled you will hear everyone using the same squelch code or 0
		//Otherwise only people on the same squelch code, or if your using 0 you will hear everyone. 
		if(vClassicMode.equals(State.On)){
			if(transmitter.isSquelched() && !st(transmitter)){
				 return false;							 
			}
		} else {
			if(this.isSquelched() && !st(transmitter)){
					 return false;							 			
			}		
		}
		//Secure Transmission, Filter: on, Can Decode message: no
		if(transmitter.isSecure() && this.getFilter().equals(State.On) && !this.canDecode(transmitter)){
			return false; 
		} 
		
		return true;
	}
	private boolean st(Radio transmitter){
		if(this.getFrequency().equals(transmitter.getFrequency())&&this.getSquelchCode().equals(transmitter.getSquelchCode())){
			return true;
			}
		else {
			return false;
		}
	}
	
	public boolean canDecode(Radio transmitter){
		return vKeySet.contains(transmitter.getEncryptionKey());
	}


}