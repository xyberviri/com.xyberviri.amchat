package com.xyberviri.amchat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class AMChatCmd implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length < 1){
				AMChat.logMessage("your mighty radio hears and is heard by all, there are no settings to change.");
				return true;
			}
			
		}
		
		
		return false;
	}

	
}
