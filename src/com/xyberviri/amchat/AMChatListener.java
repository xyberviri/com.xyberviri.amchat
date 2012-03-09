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
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AMChatListener implements Listener {
	//this is a listener server, all of the listeners
	AMChat amcMain;
	
	public AMChatListener (AMChat amchat){
		this.amcMain = amchat;
	}
	
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.JUKEBOX && (event.getPlayer().isSneaking()) && event.getPlayer().hasPermission("amchat.radio.fixed.create")){
        	if(block.getY() >= block.getWorld().getSeaLevel()){
        		amcMain.amcRadMan.createNewRadio(event.getPlayer(), block.getLocation());
        	} else {
        		amcMain.amcTools.errorToPlayer(event.getPlayer(), "Unable to place a radio this low, you have to be at least at sea level.");
        	}
        	     	
        }        		
    }    		
	
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
    		event.setCancelled(true);
    		this.amcMain.amcRouter.AMChatEvent(event);
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event){
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
