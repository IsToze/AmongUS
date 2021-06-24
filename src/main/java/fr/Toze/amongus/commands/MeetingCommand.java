package fr.Toze.amongus.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import fr.Toze.amongus.AmongGame;
import fr.Toze.amongus.Main;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.interfaces.CompletedCommand;
import fr.Toze.amongus.interfaces.CustomArgs;

public class MeetingCommand extends CompletedCommand{

	private AmongGame manager;
	
	@Override
	public void init(Object[] array) {
		this.manager = ((Main) array[2]).getGame();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		this.manager.meeting();
		return true;
	}

	@Override
	public String getName() {
		return "meeting";
	}

	@Override
	public String getPermission() {
		return MessageList.PERMISSION_MEETING_COMMAND.toString();
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
