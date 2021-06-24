package fr.Toze.amongus.event;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.Toze.amongus.AmongGame;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.utils.Converter;

public class GameBefore implements Listener {

	private AmongGame game;
	private Location spawnLocation;
	
	public GameBefore(AmongGame game) {
		this.game = game;
		this.spawnLocation = new Converter().toLocation(MessageList.LOBBY_LOCATION.toString());
	}
	
	@EventHandler	
	public void onJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		
		if(!game.join(player)){
			player.kickPlayer(MessageList.KICK_SERVER_FULL.toString());
			return;
		}

		event.setJoinMessage("§8[§a+§8] §a"+player.getName());
		player.getActivePotionEffects().clear();
		player.setMaxHealth(20);
		player.setHealth(20);
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().clear();
		player.updateInventory();
		player.setFoodLevel(6);
		player.setFireTicks(0);
		player.teleport(spawnLocation);
		Bukkit.getScoreboardManager().getMainScoreboard().getTeam("§cPlayers").addPlayer(player);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		event.setQuitMessage("§8[§c-§8] §c"+player.getName());
		game.quit(player);
	}

}
