package com.xyberviri.amchat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    public void onPlayerChat(PlayerChatEvent event) {
    		event.setCancelled(true);
    		this.amcMain.amcRouter.AMChatEvent(event);
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event){
    		Player player = event.getPlayer();
    		this.amcMain.initPlayerRadio(player);
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event){
    		//Player player = event.getPlayer();
    		//this.amcMain.initPlayerRadio(player);    	
    }

	public boolean isLoaded(AMChat amcMainPlugin) {
		if (this.amcMain.equals(amcMainPlugin)){return true;}
		return false;
	}
}
