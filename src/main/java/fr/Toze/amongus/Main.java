package fr.Toze.amongus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import fr.Toze.amongus.enums.CommandList;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.event.CameraAddons;
import fr.Toze.amongus.event.CameraEvent;
import fr.Toze.amongus.event.CustomMap;
import fr.Toze.amongus.event.GameBefore;
import fr.Toze.amongus.event.GameEvent;
import fr.Toze.amongus.event.InteractListener;
import fr.Toze.amongus.interfaces.CompletedCommand;
import fr.Toze.amongus.interfaces.CustomArgs;
import fr.Toze.amongus.manager.CameraManager;
import fr.Toze.amongus.manager.CommandManager;
import fr.Toze.amongus.manager.FileManager;
import fr.Toze.amongus.manager.TaskManager;
import fr.Toze.amongus.utils.EntitySearcher;
import fr.Toze.amongus.utils.NBTEntity.Equipment;
import fr.Toze.amongus.utils.TimerManager;

public class Main extends JavaPlugin{

	private FileManager files;
	private AmongGame game;	
	private CameraManager cameraManager;
	private TaskManager tasks;
	
	private static Main instance;
	public static Main getInstance(){
		return instance;
	}
	
	public void onEnable() {	
		instance = this;
		
		MessageList.init(this);
		
		if(!getDataFolder().exists()) getDataFolder().mkdirs();
		TimerManager.init();
		
		this.files = new FileManager(getDataFolder());
		this.game = new AmongGame(this);
		
		this.cameraManager = new CameraManager(this);

		this.tasks = new TaskManager(this);
		
		for(CommandList commandList : CommandList.values()){
			CompletedCommand command = commandList.getCommand();
			String commandName = command.getName();
			PluginCommand registering = getCommand(commandName);
			CommandManager commandManager = new CommandManager(this, commandName, 
					command.getPermission(), command.isOnlyUser(), command.getSubs());

			List<String> subList = new ArrayList<>();
			CustomArgs[] subs = commandManager.getSubList();
			if(subs != null) Arrays.stream(commandManager.getSubList()).forEach(e -> subList.add(e.getName()));

			registering.setExecutor(command);
			registering.setTabCompleter(new TabCompleter() {

				@Override
				public List<String> onTabComplete(CommandSender sender, Command cmd, String message, String[] args) {
					return args.length == 1 && !subList.isEmpty() ? subList : command.getTabCompleter(sender, cmd, message, args);
				}
			});

			command.init(new Object[]{commandManager, cameraManager, this});
		}
		
		PluginManager pluginManager = Bukkit.getPluginManager();
		
		pluginManager.registerEvents(new InteractListener(this), this);
		pluginManager.registerEvents(new CameraEvent(this), this);
		pluginManager.registerEvents(new CameraAddons(this), this);
		
		pluginManager.registerEvents(new GameBefore(game), this);
		pluginManager.registerEvents(new GameEvent(this), this);
		pluginManager.registerEvents(new CustomMap(this), this);
		
		//Bukkit.getPlayer("Is").setMaxHealth(20);
		
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

		//scoreboard.getTeam("§cPlayers").unregister();
		
		for(String teams : Arrays.asList("§cPlayers", "§cTraps")){
			if(scoreboard.getTeam(teams) ==null){
				Team team = scoreboard.registerNewTeam(teams);
				team.setNameTagVisibility(NameTagVisibility.ALWAYS);
				team.setCanSeeFriendlyInvisibles(false);
				team.setPrefix("§b");
				System.out.println("Register a new Team");
			}
		}
		
		Bukkit.getOnlinePlayers().forEach(p -> {
			Bukkit.getPluginManager().callEvent(new PlayerQuitEvent(p, "AmongUS Call Auto-Quit"));
			Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(p, "AmongUS Call Auto-Join"));
		});
		
	}

	@Override
	public void onDisable() {
		this.cameraManager.dismountAll();
		new EntitySearcher(EntityType.ARMOR_STAND, Equipment.BOOTS, "task-entity", "true").getAll().forEach(e -> e.remove());
		TimerManager.stopAll();
	}
	
	public AmongGame getGame() {
		return game;
	}
	
	public FileManager getFiles() {
		return files;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}
	
	public TaskManager getTasks() {
		return tasks;
	}

}
