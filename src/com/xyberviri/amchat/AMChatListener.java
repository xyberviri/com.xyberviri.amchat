package com.xyberviri.amchat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AMChatListener implements Listener {
	//this is a listener server, all of the listener
	AMChat amcMain;
	
	public AMChatListener (AMChat amchat){
		this.amcMain = amchat;
	}
	
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.JUKEBOX){        	
        	amcMain.amcRadMan.createNewRadio(event.getPlayer(), block.getLocation());     	
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
    }

	public boolean isLoaded(AMChat amcMainPlugin) {
		if (this.amcMain.equals(amcMainPlugin)){return true;}
		return false;
	}
	
}
