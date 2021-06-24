package fr.Toze.amongus.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.Toze.amongus.Main;
import fr.Toze.amongus.enums.ArgsList;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.interfaces.CustomArgs;
import fr.Toze.amongus.utils.ArgsValue;

public class CamerasList extends CustomArgs{

	@Override
	public String getCommand() {
		return "cameras";
	}

	@Override
	public String getName() {
		return "list";
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public MessageList getUtils() {
		return MessageList.MESSAGE_COMMAND_CAMERA_LIST;
	}

	@Override
	public ArgsList[] getArgs() {
		return null;
	}

	@Override
	public void execute(CommandSender paramCommandSender, ArgsValue values, Main main) {
		((Player) paramCommandSender).openInventory(main.getCameraManager().getInventory());
	}

}
