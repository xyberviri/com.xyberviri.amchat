package com.xyberviri.amchat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.xyberviri.amchat.events.AMChatEvent;
import com.xyberviri.amchat.events.AMEventCenter;


public class AMChatListener implements Listener {
	//this is a listener server, all of the listeners
	AMChat amcMain;
	
	public AMChatListener (AMChat amchat){
		this.amcMain = amchat;
	}
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
    	if(event.isCancelled()){return;}
        Block block = event.getBlock();
//        amcMain.logMessage("BY:"+block.getY());
//        amcMain.logMessage("SL:"+block.getWorld().getSeaLevel());
//        amcMain.logMessage("MH:"+block.getWorld().getMaxHeight());
        
        //amcMain.logMessage(event.getPlayer().getDisplayName()+" placed "+block.getType());
        if (block.getType() == Material.JUKEBOX && (event.getPlayer().isSneaking()) && event.getPlayer().hasPermission("amchat.radio.fixed.create")){
        	if(block.getY() >= block.getWorld().getSeaLevel()-1 && (block.getY() <= amcMain.varRadioMaxHeight)){
        		amcMain.amcRadMan.createNewRadio(event.getPlayer(), block.getLocation());
        	} else {
        		event.setCancelled(true);
        		String varErrorMessage="";
        		if (block.getY() >= block.getWorld().getSeaLevel()-1){
        			varErrorMessage="IN RESTRICTED AIR SPACE";
        		} else {
        			varErrorMessage="NOT ABOVE SEA LEVEL";
        		}
        		amcMain.amcTools.msgToPlayer(event.getPlayer(),"Unable to build here, ",varErrorMessage);
        	}
        	     	
        }        		
    }    		
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(PlayerChatEvent event){
		if(event.isCancelled()) return;	
		Player sender = event.getPlayer();
		boolean isRadio, isOurs = false; //is this a XCast, Does this even belong to AMChat.
		//Decide if this is a our chat event.		
		if(amcMain.isRadioOn(sender)&&amcMain.getPlayerMic(sender)){
			//Radio on and Mic open, send via radio
			isOurs = true;
			isRadio = true;	
			if(amcMain.varHeldItemReq){
				//Additional checks if we "need to have a radio"
				if(sender.getItemInHand().getTypeId() == amcMain.varHeldItemID){
					//I'm either holding a radio or have one in my inventory.
				} else if (amcMain.isLocalManaged()){
					//This is local chat and this plugin is managing local chat also.
					isRadio = false;
				} else {
					//Local chat and this plugin doesn't handle local chat.
					isRadio = false;
					isOurs = false;
				}				
			}//End Checks for Held requirement. 
			
		} else if (amcMain.isLocalManaged()){
			//No active open mic or active radio, Its ours if AMChat takes care of local
			isOurs = true;
			isRadio = false;			
		} else {
			//Local chat and this plugin doesn't handle local chat.
			isRadio = false;
			isOurs = false;
		}
		
		//If this is our event so cancel the original chat event 
		if (isOurs){
			//This is the actual hand off to AMChat.
			AMEventCenter.callAmChatEvent(sender, event.getMessage(),isRadio,amcMain.getPlayerRadioChannel(sender),amcMain.getPlayerRadioCode(sender));
			event.setCancelled(true);
			} else {
				//This isn't an AMChat event
			}	
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onAMChat(AMChatEvent event) {
		if(event.isCancelled()) return;
    		this.amcMain.amcRouter.AMChatEvent(event);
    }
    
	@EventHandler
    public void onPlayerLogin(PlayerJoinEvent event){  
    		Player player = event.getPlayer();
    		this.amcMain.loadPlayerRadioSettings(player);
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event){
    		Player player = event.getPlayer();
    		amcMain.savePlayerRadioSettings(player);
    		amcMain.amcRadMan.unlinkPlayerFromRadio(player, amcMain.getPlayerLinkID(player),false); 
    }

	public boolean isLoaded(AMChat amcMainPlugin) {
		if (this.amcMain.equals(amcMainPlugin)){return true;}
		return false;
	}
	
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
    	if(event.getPlayer().getItemInHand().getTypeId() == amcMain.varHeldItemID){
    		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
    			amcMain.scanPlayerRadioChannel(event.getPlayer(), true);
    		} else {
    			amcMain.scanPlayerRadioChannel(event.getPlayer(), false);
    		}
    	}
    	
    }
}
