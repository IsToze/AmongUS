package fr.Toze.amongus.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.interfaces.CompletedCommand;
import fr.Toze.amongus.interfaces.CustomArgs;
import fr.Toze.amongus.manager.CommandManager;

public class CamerasCommand extends CompletedCommand{

	private CommandManager manager;

	@Override
	public void init(Object[] array) {
		super.init(array);
		this.manager = (CommandManager) array[0];
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		manager.use(sender, arg3);
		return false;
	}

	@Override
	public String getName() {
		return "cameras";
	}

	@Override
	public String getPermission() {
		return MessageList.PERMISSION_CAMERAS_COMMAND.toString();
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
		return new CustomArgs[]{new CamerasCreate(), new CamerasList()};
	}

}
