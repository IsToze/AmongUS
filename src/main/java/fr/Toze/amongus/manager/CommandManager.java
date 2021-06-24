package fr.Toze.amongus.manager;

import java.util.Arrays;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.Toze.amongus.Main;
import fr.Toze.amongus.enums.ArgsList;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.interfaces.CustomArgs;
import fr.Toze.amongus.utils.ArgsValue;

public class CommandManager {

	private Main main;
	private String command, permission;
	private boolean onlyPlayer;
	private CustomArgs[] subList;
	
	public CommandManager(Main main, String command, String permission,
			boolean onlyPlayer, CustomArgs[] subs) {
		this.main = main;
		this.command = command;
		this.permission = permission;
		this.onlyPlayer = onlyPlayer;
		this.subList = subs;
	}

	public String getCommand() {
		return command;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public boolean isOnlyPlayer() {
		return onlyPlayer;
	}
	
	public CustomArgs[] getSubList() {
		return this.subList;
	}
	
	public void use(CommandSender sender, String args[]){
		if(onlyPlayer && !(sender instanceof Player)){
			sender.sendMessage(MessageList.MESSAGE_COMMAND_ONLY_PLAYERS.toString());
			return;
		}
		
		if(getPermission() != null && !sender.hasPermission(getPermission())){
			sender.sendMessage(MessageList.MESSAGE_COMMAND_NO_PERMISSION.toString());
			return;
		}
		
		if(args.length == 0){
			sender.sendMessage("§e§m-----------------------------------------------");
			sender.sendMessage("");
			String name = getCommand();
			sender.sendMessage(MessageList.MESSAGE_HELP_FOR.toString().replace("%command%", name));
			sender.sendMessage("");
			for(CustomArgs list : getSubList()){
				String usage = list.getUsage();
				usage = (usage == null ? "" : usage+" ");
				sender.sendMessage("§a> §6/"+name+" §e"+list.getName()+" "+usage+"§6: §e"+list.getUtils().toString()+".");
			}
			sender.sendMessage("");
			return;
		}
		
		String name = args[0];
		Optional<CustomArgs> optiSub = Arrays.stream(getSubList()).filter(s -> s.getName().equalsIgnoreCase(name)).findFirst();
		if(!optiSub.isPresent()){
			sender.sendMessage(MessageList.MESSAGE_SUB_COMMAND_NOT_FOUND.toString().replace("%subname%", name));
			return;
		}
		
		CustomArgs sub = optiSub.get();
		ArgsList[] subArgs = sub.getArgs();
		
		if(subArgs == null){
			sub.execute(sender, null, main);
			return;
		}
		
		int index = 0;
		ArgsValue value = new ArgsValue();
		
		for(ArgsList currentArgs : subArgs){
			try{
				String writedArgs = args[++index];
				switch(currentArgs){
				case STRING:
					value.add(writedArgs);
					break;
				case INTEGER:
					value.add(Integer.parseInt(writedArgs));
					break;
				case OFFLINE_PLAYER:
					value.add(Bukkit.getOfflinePlayer(writedArgs));
					break;
				case ONLINE_PLAYER:
					value.add(Bukkit.getPlayerExact(writedArgs));
					break;
				default:
					break;
				}
			}catch (Exception e) {
				sender.sendMessage("");
				String usage = sub.getUsage();
				usage = (usage == null ? "" : usage+" ");
				sender.sendMessage("§a> §6/"+getCommand()+" §e"+sub.getName()+" "+usage+"§6: §e"+sub.getUtils()+".");
				sender.sendMessage("");
				return;
			}
			
		}
		for(int i = ++index; i < args.length; i++){
			value.add(args[i]);
		}
		sub.execute(sender, value, main);
		
	}

	
}
