package fr.Toze.amongus.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import fr.Toze.amongus.Main;
import fr.Toze.amongus.enums.ArgsList;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.interfaces.CustomArgs;
import fr.Toze.amongus.manager.CameraManager;
import fr.Toze.amongus.utils.ArgsValue;

public class CamerasCreate extends CustomArgs{

	@Override
	public String getCommand() {
		return "cameras";
	}

	@Override
	public String getName() {
		return "create";
	}

	@Override
	public String getUsage() {
		return "<Name> <Group>";
	}

	@Override
	public MessageList getUtils() {
		return MessageList.MESSAGE_COMMAND_CAMERA_CREATE;
	}

	@Override
	public ArgsList[] getArgs() {
		return new ArgsList[]{ArgsList.STRING, ArgsList.INTEGER};
	}

	@Override
	public void execute(CommandSender paramCommandSender, ArgsValue values, Main main) {
		CameraManager manager = main.getCameraManager();
		
		Object[] value = values.getValues();
		String name = (String) value[0];
		if(manager.getCamera(name).isPresent()){
			paramCommandSender.sendMessage(MessageList.MESSAGE_COMMAND_CAMERA_CREATE_ALEARDY_EXIST
					.toString().replace("%name%", name));
			return;
		}
		
		BlockIterator iterator = new BlockIterator((Player) paramCommandSender, 10);
		while(iterator.hasNext()){
			Block block = iterator.next();
			if(block.getType() != Material.AIR){
				Location location = block.getLocation();
				int group = (int) value[1];
				manager.createCamera(name, location, group);
				paramCommandSender.sendMessage(MessageList.MESSAGE_COMMAND_CAMERA_CREATE_SUCCESS.toString()
						.replace("%name%", name)
						.replace("%x%", String.valueOf(location.getBlockX()))
						.replace("%y%", String.valueOf(location.getBlockY()))
						.replace("%z%", String.valueOf(location.getBlockZ()))
						.replace("%world%", location.getWorld().getName())
						.replace("%group%", String.valueOf(group)));
				
				ArmorStand clicked = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5, 1, 0.5), EntityType.ARMOR_STAND);
				clicked.setVisible(false);
				clicked.setCustomName("CameraClicker "+name);
				clicked.setGravity(false);
				
				return;
			}
		}
		
		paramCommandSender.sendMessage(MessageList.MESSAGE_COMMAND_CAMERA_CREATE_NO_SEE.toString());
		
	}

}
