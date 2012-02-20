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
	private boolean amcListenerLoaded = false;
	
	public AMChatListener (AMChat amchat){
		this.amcMain = amchat;
		this.amcListenerLoaded = true;
	}
	
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
    	if(this.amcListenerLoaded){
    		event.setCancelled(true);
    		this.amcMain.amcRouter.AMChatEvent(event);
    		
    	} else {
    		this.amcMain.logError("chat event sent to unlinked listener!");
    	}
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event){
    	if(this.amcListenerLoaded){
    		Player player = event.getPlayer();
    		this.amcMain.initPlayerRadio(player);
    	} else {
    		this.amcMain.logError("login event sent to unlinked listener!");
    	}
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event){
    	
    	if(this.amcListenerLoaded){
    		//Player player = event.getPlayer();
    		//this.amcMain.initPlayerRadio(player);
    	} else {
    		this.amcMain.logError("logout event sent to unlinked listener!");
    	}
    }

	// return true if we were successfully loaded
    public boolean isLoaded(){return this.amcListenerLoaded;}
}
