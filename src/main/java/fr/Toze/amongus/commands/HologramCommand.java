package fr.Toze.amongus.commands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.interfaces.CompletedCommand;
import fr.Toze.amongus.interfaces.CustomArgs;
import fr.Toze.amongus.utils.NBTEntity;
import fr.Toze.amongus.utils.NBTEntity.Equipment;

public class HologramCommand extends CompletedCommand{

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage(MessageList.MESSAGE_COMMAND_ONLY_PLAYERS.toString());
			return false;
		}
		
		if(args.length == 0){
			sender.sendMessage("§e> /hologram §6<Name>");
			return false;
		}
		StringBuilder builder = new StringBuilder();
		for(String arg : args) builder.append(" "+arg);
		
		Location location = ((Player) sender).getLocation();
		ArmorStand armor = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		armor.setCustomName(builder.substring(1).replace("&", "§"));
		armor.setCustomNameVisible(true);
		armor.setVisible(false);
		armor.setGravity(false);
		new NBTEntity(armor).put(Equipment.BOOTS, "type", "hologram");
		
		return true;
	}

	@Override
	public String getName() {
		return "hologram";
	}

	@Override
	public String getPermission() {
		return MessageList.PERMISSION_HOLOGRAM_COMMAND.toString();
	}

	@Override
	public boolean isOnlyUser() {
		return true;
	}

	@Override
	public List<String> getTabCompleter(CommandSender sender, Command command, String message, String[] args) {
		return null;
	}

	@Override
	public CustomArgs[] getSubs() {
		return null;
	}

}
