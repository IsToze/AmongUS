package fr.Toze.amongus.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.Toze.amongus.AmongGame;
import fr.Toze.amongus.AmongGame.GameState;
import fr.Toze.amongus.Main;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.interfaces.CompletedCommand;
import fr.Toze.amongus.interfaces.CustomArgs;

public class RanksCommand extends CompletedCommand{

	private AmongGame game;
	
	@Override
	public void init(Object[] array) {
		this.game = ((Main) array[2]).getGame();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		GameState state = game.getState();
		List<Player> crewmates = state.getCrewmates(), impostors = state.getImpostors();
		sender.sendMessage("§b> "+MessageList.CREWMATES_NAME+"§b : "+String.join(", ", 
				crewmates.stream().map(p -> p.getName()).collect(Collectors.toList()))+". §7("+crewmates.size()+")");
		sender.sendMessage("§c> "+MessageList.IMPOSTORS_NAME+"§c : "+String.join(", ", 
				impostors.stream().map(p -> p.getName()).collect(Collectors.toList()))+". §7("+impostors.size()+")");
		return false;
	}

	@Override
	public String getName() {
		return "ranks";
	}

	@Override
	public String getPermission() {
		return MessageList.PERMISSION_RANKS_COMMAND.toString();
	}

	@Override
	public boolean isOnlyUser() {
		return false;
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
