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
        //amcMain.logMessage(event.getPlayer().getDisplayName()+" placed "+block.getType());
        if (block.getType() == Material.JUKEBOX && (event.getPlayer().isSneaking()) && event.getPlayer().hasPermission("amchat.radio.fixed.create")){
        	if(block.getY() >= block.getWorld().getSeaLevel()){
        		amcMain.amcRadMan.createNewRadio(event.getPlayer(), block.getLocation());
        	} else {
        		event.setCancelled(true);
        		amcMain.amcTools.errorToPlayer(event.getPlayer(), "Unable to place a radio this low, you have to be at least at sea level.");
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
				if(sender.getItemInHand().getTypeId() == amcMain.varHeldItemID ||sender.getInventory().contains(amcMain.varHeldItemID)){
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
		
		//If this is our event then cancel the original chat event 
		if (isOurs){
			AMChatEvent newAmChatEvent = new AMChatEvent (sender, event.getMessage(),isRadio,amcMain.getPlayerRadioChannel(sender),amcMain.getPlayerRadioCode(sender));
			amcMain.getServer().getPluginManager().callEvent(newAmChatEvent);
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
    
	@EventHandler(priority = EventPriority.HIGHEST)
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
	
    @EventHandler(priority = EventPriority.LOWEST)
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
