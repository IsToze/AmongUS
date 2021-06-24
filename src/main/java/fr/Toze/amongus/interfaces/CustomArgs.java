package fr.Toze.amongus.interfaces;

import org.bukkit.command.CommandSender;

import fr.Toze.amongus.Main;
import fr.Toze.amongus.enums.ArgsList;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.utils.ArgsValue;

public abstract class CustomArgs {

	public abstract String getCommand();
	public abstract String getName();
	public abstract String getUsage();
	public abstract MessageList getUtils();
	public abstract ArgsList[] getArgs();
	public abstract void execute(CommandSender paramCommandSender, ArgsValue values, Main main);
	
}
