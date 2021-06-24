package fr.Toze.amongus.interfaces;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class CompletedCommand implements CommandExecutor{

	public abstract String getName();
	public abstract String getPermission();
	public abstract boolean isOnlyUser();
	public abstract List<String> getTabCompleter(CommandSender sender, Command command, String message, String[] args);
	public abstract CustomArgs[] getSubs();
	public void init(Object[] array){}

}
