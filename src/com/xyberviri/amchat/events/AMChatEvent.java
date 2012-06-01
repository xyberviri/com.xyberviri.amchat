package com.xyberviri.amchat.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AMChatEvent extends Event implements Cancellable{
private static final HandlerList handlers = new HandlerList();
@Override
public HandlerList getHandlers(){return handlers;}
public static HandlerList getHandlerList(){return handlers;}
	
private boolean cancel = false;

@Override
public boolean isCancelled() {
	return this.cancel;
}
@Override
public void setCancelled(boolean cancel) {
	this.cancel = cancel;
	
}

}
