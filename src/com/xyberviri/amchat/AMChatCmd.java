package com.xyberviri.amchat;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class AMChatCmd implements CommandExecutor {
	AMChat amcMain;
	
	AMChatCmd(AMChat amcMain){
		this.amcMain = amcMain;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		if (!(sender instanceof Player)) {
			if (cmd.getName().equalsIgnoreCase("am") && args.length == 0){
				amcMain.logMessage("your mighty radio hears and is heard by all, there are no settings to change.");
				return true;
			}
			
			if (cmd.getName().equalsIgnoreCase("am") && args[0].equalsIgnoreCase("list")){
				if (amcMain.getRadioPlayers().size() > 0){
						amcMain.logMessage("There are "+amcMain.getRadioPlayers().size()+" players online with active radio");
						for (String playerName : amcMain.getRadioPlayers()){
							Player player = Bukkit.getServer().getPlayer(playerName);
					    	if (player != null){
					    	amcMain.logMessage("["+playerName+"] fq:"+amcMain.getPlayerRadioChannel(player)+", cd:"+amcMain.getPlayerRadioCode(player)+", mic open:"+amcMain.getPlayerMic(player)+", filter on:"+amcMain.getPlayerFilter(player)+", cutoff:"+amcMain.getPlayerCutoff(player));
					    	}	
						}
					} 
				else {amcMain.logMessage("There are no players online with active radios");
					}
				return true;
			}
			
			if (cmd.getName().equalsIgnoreCase("xm") && args[0].equalsIgnoreCase("list")){
				if(amcMain.amcRadMan.getRadioList().isEmpty()){
					amcMain.logMessage("There are no radios");
					return true;
				} else{
					amcMain.logMessage("There are "+amcMain.amcRadMan.getRadioList().size()+" radio(s) on the server.");
					for(AMChatRadio amcRadSet : amcMain.amcRadMan.getRadioList()){
						amcMain.logMessage("["+amcRadSet.getName()+"]f:"+amcRadSet.getChan()+" c:"+amcRadSet.getCode()+" loc:"+amcRadSet.getLoc().getWorld().getName()+" "+amcRadSet.getLoc().getBlockX()+", "+amcRadSet.getLoc().getBlockY()+", "+amcRadSet.getLoc().getBlockZ());
					}
					return true;
				}
				
			}
			
			if (cmd.getName().equalsIgnoreCase("xm") && args[0].equalsIgnoreCase("check")){
				amcMain.amcRadMan.amcRadManComCheck();
				return true;			
			}
			
			amcMain.logMessage("That is not avalible from the console");
			return true;
		}	
		
		Player player = (Player) sender;
		
		if(amcMain.varHeldItemReq && player.getItemInHand().getTypeId() != amcMain.varHeldItemID){
			amcMain.amcTools.msgToPlayer(player,"Sorry, you need to be holding a radio before you can change the settings.");
			return true;
		}

		//AM command branch
		if (cmd.getName().equalsIgnoreCase("am")){
			
			
		//am
		if (player.hasPermission("amchat.radio.personal.use") && args.length == 0){
			amcMain.amcTools.msgToPlayer(player,"[-Xmit-Freq-]:"," "+amcMain.getPlayerRadioChannel(player));
			amcMain.amcTools.msgToPlayer(player,"[-Xmit-Code-]:"," "+amcMain.getPlayerRadioCode(player));
			amcMain.amcTools.msgToPlayer(player,"[-Xmit-Link-]:"," "+amcMain.getPlayerLinkID(player));
			amcMain.amcTools.msgToPlayer(player,"[-Mic-Open--]:"," "+amcMain.getPlayerMic(player));
			amcMain.amcTools.msgToPlayer(player,"[-Cut-Off---]:"," "+amcMain.getPlayerCutoff(player));			
			amcMain.amcTools.msgToPlayer(player,"[-Filter----]:"," "+amcMain.getPlayerFilter(player));
			return true;
		}
		
		//am list
		if ((player.hasPermission("amchat.radio.list.personal")||player.isOp()) && args[0].equalsIgnoreCase("list")){
			if (amcMain.getRadioPlayers().isEmpty()){
				amcMain.amcTools.msgToPlayer(player,"There are no players online with active radios");
				} 
			else {
				amcMain.amcTools.msgToPlayer(player,"There are "+amcMain.getRadioPlayers().size()+" players online with active radio");
				for (String playerName : amcMain.getRadioPlayers()){
					Player activeRadios = Bukkit.getServer().getPlayer(playerName);
			    	if (activeRadios != null){
			    		amcMain.amcTools.msgToPlayer(player,"["+playerName+"] fq:"+amcMain.getPlayerRadioChannel(activeRadios)+", cd:"+amcMain.getPlayerRadioCode(activeRadios)+", mic open:"+amcMain.getPlayerMic(activeRadios)+", filter on:"+amcMain.getPlayerFilter(activeRadios)+", cutoff:"+amcMain.getPlayerCutoff(activeRadios));
			    	}	
				}
				
			}
			return true;
		}		
		
		
		
		//am radio
		if(player.hasPermission("amchat.radio.personal.radio") && args[0].equalsIgnoreCase("radio") && args.length == 1){
			amcMain.togglePlayerRadio(player);
			return true;
		}
		
		//am mic
		if(player.hasPermission("amchat.radio.personal.mic") && args[0].equalsIgnoreCase("mic") && args.length == 1){
			amcMain.togglePlayerMic(player);
			return true;
		}
		
		//am filter
		if(player.hasPermission("amchat.radio.personal.filter") && args[0].equalsIgnoreCase("filter") && args.length == 1){
			amcMain.togglePlayerFilter(player);
			return true;
		}
		
		//am tune <#>
		if(player.hasPermission("amchat.radio.personal.tune") && args[0].equalsIgnoreCase("tune") && args.length == 2){
			if(amcMain.isPlayerLinked(player)){
				amcMain.amcTools.errorToPlayer(player,"You are currently linked to "+amcMain.getPlayerLinkID(player)+" you need to /xm unlink before you can tune");
				return true;
			}
			try{ 
				Integer targetValue = Integer.parseInt(args[1]);
				
				if((targetValue<amcMain.varRadioMinFreq || targetValue>amcMain.varRadioMaxFreq)&&(!player.hasPermission("amchat.radio.override.tune"))&&(!player.isOp())){
					amcMain.amcTools.errorToPlayer(player,"Valid Frequencies are "+amcMain.varRadioMinFreq+"-"+amcMain.varRadioMaxFreq);
					return true;
				}				
				amcMain.tunePlayerRadioChannel(player, targetValue);
				return true;
			} catch (NumberFormatException e){
				amcMain.amcTools.errorToPlayer(player,args[1] + "is not a number!");
				return true;
				}
		}		
		
		//am code <#>
		if(player.hasPermission("amchat.radio.personal.code") && args[0].equalsIgnoreCase("code") && args.length == 2){
			if(amcMain.isPlayerLinked(player)){
				amcMain.amcTools.errorToPlayer(player,"You are currently linked to "+amcMain.getPlayerLinkID(player)+" you need to /xm unlink first to change your code");
				return true;
			}
			try{ 
				Integer targetValue = Integer.parseInt(args[1]);
				
				
				if ((targetValue<amcMain.varRadioMinCode || targetValue>amcMain.varRadioMaxCode)&&(!player.hasPermission("amchat.radio.override.code"))&&(!player.isOp())){
					amcMain.amcTools.errorToPlayer(player,"Valid Code range is "+amcMain.varRadioMinCode+"-"+amcMain.varRadioMaxCode);
					return true;					
				}
				
 
				amcMain.setPlayerRadioCode(player, targetValue);
				return true;
			} catch (NumberFormatException e){
				amcMain.amcTools.errorToPlayer(player,args[1] + "is not a number!");
				return true;
				}
		}		

		//am cutoff <#>
		if(player.hasPermission("amchat.radio.personal.cutoff") && args[0].equalsIgnoreCase("cutoff") && args.length == 2){
			try{ 
				Integer targetValue = Integer.parseInt(args[1]);
				if((targetValue>amcMain.varRadioMaxCuttoff)&&(!player.hasPermission("amchat.radio.override.cutoff"))&&(!player.isOp())){
					amcMain.amcTools.errorToPlayer(player,"Valid Cutoff is 0-"+amcMain.varRadioMaxCuttoff);
					return true;
				} else if (targetValue<0){
					amcMain.amcTools.errorToPlayer(player,"Valid Cutoff is 0-"+amcMain.varRadioMaxCuttoff+", setting to 0.");
					targetValue=0;
					}
				amcMain.setPlayerRadioCutoff(player, targetValue);
				return true;
			} catch (NumberFormatException e){
				amcMain.amcTools.errorToPlayer(player,args[1] + "is not a number!");
				return true;
				}
		}		
		
		//am home
		if(player.hasPermission("amchat.radio.personal.home") && args[0].equalsIgnoreCase("home") && args.length == 1){
			if(!amcMain.isRadioOn(player)){
				amcMain.togglePlayerRadio(player);
			}
			if(!amcMain.getPlayerMic(player)){
				amcMain.togglePlayerMic(player);	
			}
			if(amcMain.getPlayerFilter(player)){
				amcMain.togglePlayerFilter(player);
			}			
			if(amcMain.isPlayerLinked(player)){
				amcMain.setPlayerLinkID(player, "none");
			}
			amcMain.tunePlayerRadioChannel(player, amcMain.varRadioDefFreq);
			amcMain.setPlayerRadioCode(player, 0);
			amcMain.setPlayerRadioCutoff(player, amcMain.varRadioMaxCuttoff);
			return true;
		}			
		
		//am ping
		if(player.hasPermission("amchat.radio.personal.ping") && args[0].equalsIgnoreCase("ping") && args.length == 2){
			Player other = Bukkit.getServer().getPlayer(args[1]);
	        if (other == null) {
	        	amcMain.amcTools.errorToPlayer(player, args[1] + " is not online!");
	        	return true;
	        }
			amcMain.playerRadioPing(player,other);
			return true;
		}
		
		return false;
		}
		//Else if  were using XM commands 
		else if (cmd.getName().equalsIgnoreCase("xm")){
			
			//XM
			if ((player.hasPermission("amchat.radio.fixed.use")||player.isOp()) && args.length == 0){
				ArrayList<String> varRadios = amcMain.amcRadMan.getOwnerRadios(player.getDisplayName());
				if(varRadios.isEmpty()){
					amcMain.amcTools.msgToPlayer(player,"You dont currently own any radios.");
				} else {
					amcMain.amcTools.msgToPlayer(player,"Radio's Owned:",""+varRadios.size());
					String varHandleList="";
					for(String varHandle : varRadios){
						varHandleList="["+varHandle+"] "+varHandleList;
					}
					amcMain.amcTools.msgToPlayer(player,">",varHandleList);
				}	
				amcMain.amcTools.msgToPlayer(player,"Fav Radios:");
				varRadios=amcMain.getFavRadios(player);
				if(varRadios.isEmpty()){
					amcMain.amcTools.msgToPlayer(player,"none");
				} else {
					String varFavList ="";
					for(String varHandle : varRadios){
						varFavList="["+varHandle+"] "+varFavList;
					}
					amcMain.amcTools.msgToPlayer(player,">",varFavList);
				}
				return true;
			}
			
			//XM list
			if ((player.hasPermission("amchat.radio.list.fixed")||player.isOp()) && args[0].equalsIgnoreCase("list")){
				if (amcMain.amcRadMan.getRadioList().isEmpty()){
					amcMain.amcTools.msgToPlayer(player,"The radio manager isn't tracking any radio's");
					} 
				else {
					amcMain.amcTools.msgToPlayer(player,"There are "+amcMain.amcRadMan.getRadioList().size()+" being tracked by RadMan.");
					for (AMChatRadio singleRadio : amcMain.amcRadMan.getRadioList()){
						amcMain.amcTools.msgToPlayer(player,"["+singleRadio.getName()+"] fq:"+singleRadio.getChan()
								+", cd:"+singleRadio.getCode()+", pass:"+singleRadio.getPass()
								+", users:"+singleRadio.getCurUsers()+"/"+singleRadio.getMaxUsers()
								+", range:"+singleRadio.getMaxDistance());
					}
				}
				return true;
			}	
			
			
			
			//XM link
			if((player.hasPermission("amchat.radio.fixed.link")||player.isOp()) && args[0].equalsIgnoreCase("link")){
				
				//XM link
				if(args.length==1){
					if(amcMain.isPlayerLinked(player)){
						AMChatRadio linkedRadio = amcMain.amcRadMan.getRadio(amcMain.getPlayerLinkID(player));
						amcMain.amcTools.msgToPlayer(player, "["+linkedRadio.getChan()+"."+linkedRadio.getCode()+"]Radio: ","["+linkedRadio.getName()+"]");
						amcMain.amcTools.msgToPlayer(player, "["+linkedRadio.getChan()+"."+linkedRadio.getCode()+"]Users: ",linkedRadio.getCurUsers()+"/"+linkedRadio.getMaxUsers());
						amcMain.amcTools.msgToPlayer(player, "["+linkedRadio.getChan()+"."+linkedRadio.getCode()+"]Password: ",linkedRadio.getPass());
						amcMain.amcTools.msgToPlayer(player, "["+linkedRadio.getChan()+"."+linkedRadio.getCode()+"]Location: ",linkedRadio.getLocationString());			
					} else {
						amcMain.amcTools.msgToPlayer(player, "You are not linked to a transmitter, usage:", "/xm link <id> <password>");	
						
					}
				}
				//XM link args[1] something, args[1] should be a linkid
				else if(!amcMain.amcRadMan.isLinkValid(args[1])){
					amcMain.amcTools.msgToPlayer(player, "That link id:"+args[1]+" is invalid.");
					return true;
				}
				
				//XM link <radio-id>|none
				else if(args.length==2){
					if(args[1].equalsIgnoreCase("none")){
						//amcMain.setPlayerLinkID(player, "none");
						amcMain.amcRadMan.unlinkPlayerFromRadio(player, amcMain.getPlayerLinkID(player));
					} else {
						if(amcMain.amcRadMan.playerNeedsPass(player, args[1])){
							amcMain.amcTools.msgToPlayer(player, "This link requires a password to join.");
							return true;
						} else {					
							
							amcMain.amcRadMan.linkPlayerToRadio(player, args[1]);
						}		
					}
				}
				//XM link <radio-id> <password>
				else if(args.length==3){
					if(amcMain.amcRadMan.playerNeedsPass(player, args[1])){
						if(amcMain.amcRadMan.isPassValid(args[1], args[2])){
							amcMain.amcRadMan.linkPlayerToRadio(player, args[1]);	
						}else {
							amcMain.amcTools.msgToPlayer(player, "That password is invalid.");	
						}
					} else {
						amcMain.amcRadMan.linkPlayerToRadio(player, args[1]);
					}					
				}				
				
				return true;									
			}			
			//XM unlink
			if((player.hasPermission("amchat.radio.fixed.link")||player.isOp()) && args[0].equalsIgnoreCase("unlink") && args.length == 1){
				amcMain.amcRadMan.unlinkPlayerFromRadio(player, amcMain.getPlayerLinkID(player));
				return true;
			}
			//XM chown radioid <online player name>
			//XM chown radioid <offline player name> <offline player name> 
			if(player.hasPermission("amchat.radio.fixed.chown") && args[0].equalsIgnoreCase("chown")){
				if(args.length == 3){
					if(amcMain.amcRadMan.isLinkValid(args[1])){
						Player other = Bukkit.getServer().getPlayer(args[2]);
						if (other == null) {
							amcMain.amcTools.errorToPlayer(player, args[2] + " is not online! use /xm chown <name> <name> for offline transfers!");
							return true;
						}
						
						amcMain.amcRadMan.delOwnerRadio(player.getDisplayName(), args[1]);
						amcMain.amcRadMan.addOwnerRadio(args[2], args[1]);
						
						amcMain.amcRadMan.getRadio(args[1]).setOwner(other.getDisplayName());
						amcMain.amcTools.msgToPlayer(player, "you have changed the owner of "+args[1]+" to ", other.getDisplayName());
						amcMain.amcTools.msgToPlayer(other, "you have been given ownership of "+args[1]+" by ", other.getDisplayName());
					}else{
						amcMain.amcTools.msgToPlayer(player, "the supplied link id was invalid; value:"+args[1]);
					}
				}else if(args.length == 4){
					if (args[2].equals(args[3])){
						
						String oldOwner = amcMain.amcRadMan.getRadio(args[1]).getOwner();
						amcMain.amcRadMan.delOwnerRadio(oldOwner, args[1]);
						amcMain.amcRadMan.addOwnerRadio(args[2], args[1]);
						
						amcMain.amcRadMan.getRadio(args[1]).setOwner(args[2]);
						amcMain.amcTools.msgToPlayer(player, "you have changed the owner of "+args[1]+" to ", args[2]);						
					} else {
						amcMain.amcTools.errorToPlayer(player, "usage /xm chown <name> <name> "+args[2]+" did not match "+args[3]);
						return true;
					}
					
				} 
				return true;
			}			
			
			//XM set <pass|p|code|c|freq|f|admin|a>
			if((player.hasPermission("amchat.radio.fixed.set")||player.isOp()) && args[0].equalsIgnoreCase("set")){
				if(amcMain.isPlayerLinked(player)){
					String linkID = amcMain.getPlayerLinkID(player);
					if(args.length==1){
						amcMain.amcTools.msgToPlayer(player, "you didn't specify anything to set, valid options are, pass, freq, code, admin, p, f, c & a");
						return true;
					}
					
					if(player.hasPermission("amchat.radio.override.set") || player.isOp() || amcMain.amcRadMan.isPlayerRadioAdmin(player, amcMain.getPlayerLinkID(player))){
						if(args.length==2){
							if(args[1].equalsIgnoreCase("p")||args[1].equalsIgnoreCase("pass")){
								amcMain.amcRadMan.getRadio(linkID).setPass("");
								amcMain.amcTools.msgToPlayer(player, "password for ["+linkID+"] set to ", "none");
							}
							else{
								amcMain.amcTools.msgToPlayer(player, "you didn't set a value for "+args[1]);
							}
						}
						else {
							//Set
							if(args[1].equalsIgnoreCase("p")||args[1].equalsIgnoreCase("pass")){
								amcMain.amcTools.msgToPlayer(player, "password for ["+linkID+"] set to ", args[2]);
								amcMain.amcRadMan.getRadio(linkID).setPass(args[2]);
							}
							if(args[1].equalsIgnoreCase("f")||args[1].equalsIgnoreCase("freq")){
								try{ 
									Integer targetValue = Integer.parseInt(args[2]);
									if((targetValue<amcMain.varRadioMinFreq || targetValue>amcMain.varRadioMaxFreq)&&(!player.hasPermission("amchat.radio.override.tune"))&&(!player.isOp())){
										amcMain.amcTools.errorToPlayer(player,"Valid Frequencies are "+amcMain.varRadioMinFreq+"-"+amcMain.varRadioMaxFreq);
										return true;
									}
									amcMain.amcRadMan.getRadio(linkID).setChan(targetValue);
								} catch (NumberFormatException e){
									amcMain.amcTools.errorToPlayer(player,args[2] + "is not a number!");
									return true;
									}
							}
							if(args[1].equalsIgnoreCase("c")||args[1].equalsIgnoreCase("code")){
								try{ 
									Integer targetValue = Integer.parseInt(args[2]);

									if ((targetValue<amcMain.varRadioMinCode || targetValue>amcMain.varRadioMaxCode)&&(!player.hasPermission("amchat.radio.override.code"))&&(!player.isOp())){
										amcMain.amcTools.errorToPlayer(player,"Valid Code range is "+amcMain.varRadioMinCode+"-"+amcMain.varRadioMaxCode);
										return true;					
									}
									amcMain.amcRadMan.getRadio(linkID).setCode(targetValue);
								} catch (NumberFormatException e){
									amcMain.amcTools.errorToPlayer(player,args[2] + "is not a number!");
									return true;
									}								
								
							}
							if(args[1].equalsIgnoreCase("a")||args[1].equalsIgnoreCase("admin")){
								if(player.getName().equalsIgnoreCase(args[2])&&(!player.isOp())){
									amcMain.amcTools.msgToPlayer(player, "you can not admin yourself.");
								} else {
									if(amcMain.amcRadMan.getRadio(linkID).isPlayerAdmin(args[2])){
										amcMain.amcRadMan.getRadio(linkID).delAdmin(args[2]);
										amcMain.amcTools.msgToPlayer(player, args[2]+" has been removed from the admin list for "+linkID);	
										
									} else {
										amcMain.amcRadMan.getRadio(linkID).addAdmin(args[2]);
										amcMain.amcTools.msgToPlayer(player, args[2]+" has been added to the admin list for "+linkID);
									}									
								}
							}
								
						}
						
					} else {
						amcMain.amcTools.msgToPlayer(player, "you do not have admin rights over this radio.");
					}
				} else {
					amcMain.amcTools.msgToPlayer(player, "you need to be linked before you can change any settings");
				}
				return true;
			}
			
			//lists favorites, adds favorites, remove favorites.
			//XM FAV <id>
			if((player.hasPermission("amchat.radio.fixed.fav")||player.isOp()) && args[0].equalsIgnoreCase("fav")){
				if(args.length==1){
					if(amcMain.getFavRadios(player).isEmpty()){
						amcMain.amcTools.msgToPlayer(player, "you're favorites list is empty");
					} else {
					int x=1;
					String varMsg="";
					for (String varRadioID :amcMain.getFavRadios(player)){
						varMsg="#"+x+"["+varRadioID+"] "+varMsg;
						x++;
					}
					amcMain.amcTools.msgToPlayer(player,"Favorites: ",varMsg);
					}
				} else if (args.length==2){
					String varMsg = "";
					if(amcMain.amcRadMan.isLinkValid(args[1])){
						
						if(amcMain.isFavRadio(player,args[1])){
							varMsg="Deleted: "+args[1];
							amcMain.delFavRadio(player, args[1]);
						} else {
							varMsg="Added: "+args[1];
							amcMain.addFavRadio(player, args[1]);
						}
					} else {
						varMsg=args[1]+" was not a valid link id";
					}
					amcMain.amcTools.msgToPlayer(player,varMsg);
				}
				
				
				
				return true;
			}
			
			if((player.hasPermission("amchat.radio.fixed.fav")||player.isOp()) && args.length==1){
				if(amcMain.getFavRadios(player).isEmpty()){
					amcMain.amcTools.msgToPlayer(player,"you dont have any favorites saved, use the fav command to toggle favorites, ","/xm fav <radioid>");
				} else {
				int targetValue=0;
				try {
					targetValue = Integer.parseInt(args[0]);
					targetValue--;
					
				}  catch (NumberFormatException e){
					amcMain.amcTools.errorToPlayer(player,args[0] + "is not a number!");
					return true;
					}
				if (targetValue < 0){
					amcMain.amcTools.errorToPlayer(player,"invaid id #, valid numbers are 1+");
					return true;
				}
				
				ArrayList<String>playerFavs = amcMain.getFavRadios(player);
				
				if(targetValue > playerFavs.size()){
					amcMain.amcTools.errorToPlayer(player,"invalid favorite id, max id # "+playerFavs.size());
				} 
				else{
					String varRadioID= playerFavs.get(targetValue);
					if(amcMain.amcRadMan.isLinkValid(varRadioID)){
						if(amcMain.amcRadMan.playerNeedsPass(player, varRadioID)){
							amcMain.amcTools.errorToPlayer(player,"Im sorry but you need to join ["+varRadioID+"] first before you can use this function.");
						} else {
							amcMain.amcRadMan.linkPlayerToRadio(player, varRadioID);
						}						
					}else{
						amcMain.amcTools.errorToPlayer(player,"Im sorry but that link id appears to no longer be valid.");
					}					
				}
				
				}
				
				return true;
			}
			
			
		//End of XM commands
		}
		
		
		
		
		//We shouldn't ever get here
		return false;
	}

	
	
	
	
	
	// return true if we were successfully loaded
	public boolean isLoaded(AMChat amcMainPlugin) {
		if (this.amcMain.equals(amcMainPlugin)){
			return true;
			}
		return false;
	}
	
}
