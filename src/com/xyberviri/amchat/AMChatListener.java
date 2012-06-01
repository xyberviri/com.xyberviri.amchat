package com.xyberviri.amchat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.xyberviri.amchat.events.AMChatPlayerChat;


public class AMChatListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(PlayerChatEvent event){
		if(event.isCancelled()) return;	
//		Player sender = event.getPlayer();
//		boolean isRadio = false; 
//		
//		//Check if the radio is on and the mic is open
//		if(amcMain.isRadioOn(sender)&&amcMain.getPlayerMic(sender)){
//			isRadio = true;
//			//If the server requires the item in hand and we don't have it, flag this as non radio chat.
//			if(amcMain.varHeldItemReq && sender.getItemInHand().getTypeId() != amcMain.varHeldItemID)
//			{isRadio = false;}
//		}
//		
//		
//		if((!isRadio) && (!amcMain.isLocalManaged())){
//			amcMain.logMessage("Ignoring chat event");
//			return;
//		}
//		
//		EventCenter.callAmChatEvent(sender, event.getMessage(),isRadio,amcMain.getPlayerRadioChannel(sender),amcMain.getPlayerRadioCode(sender));
//		event.setCancelled(true);
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onAMChat(AMChatPlayerChat event) {
		if(event.isCancelled()) return;
  
    }
    
	@EventHandler
    public void onPlayerLogin(PlayerJoinEvent event){  

    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event){
    		
    }

	
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
//    	if(event.getPlayer().getItemInHand().getTypeId() == amcMain.varHeldItemID){
//    		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
//    			//TODO:Add Options here for players to have shortcuts on left/right mouse buttons. 
//    			amcMain.scanPlayerRadioChannel(event.getPlayer(), true);
//    		} else {
//    			amcMain.scanPlayerRadioChannel(event.getPlayer(), false);
//    		}
//    	}
//    	
    }
}
