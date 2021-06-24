package fr.Toze.amongus.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.Toze.amongus.AmongGame;
import fr.Toze.amongus.Main;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.interfaces.Task;
import fr.Toze.amongus.interfaces.Task.TaskDuration;
import fr.Toze.amongus.interfaces.Task.TaskState;
import fr.Toze.amongus.tasks.TaskL1;
import fr.Toze.amongus.tasks.TaskL2;
import fr.Toze.amongus.tasks.TaskM1;
import fr.Toze.amongus.tasks.TaskM2;
import fr.Toze.amongus.tasks.TaskS1;
import fr.Toze.amongus.tasks.TaskS3;
import fr.Toze.amongus.throwable.TaskException;
import fr.Toze.amongus.utils.Items;
public class TaskManager {

	private List<Task> tasks;
	private Map<String, TaskPlayerState> maps;
	private Map<String, List<Task>> playerTask;
	private Items items;
	
	public TaskManager(Main main) {
		this.tasks = Arrays.asList(new TaskL1(), new TaskL2(), new TaskM1(), new TaskM2(), new TaskS1(), new TaskS3());
		this.maps = new HashMap<>();
		this.playerTask = new HashMap<>();
		this.items = new Items();
	}

	public Optional<Task> getTask(String id){
		return tasks.stream().filter(task -> task.getClass().getSimpleName().equalsIgnoreCase(id)).findFirst();
	}

	public TaskPlayerState getTask(Player player){
		return this.maps.get(player.getName());
	}

	public List<Task> getAllTasks() {
		return tasks;
	}

	public CompletableFuture<TaskState> execute(Player player, Task task) throws TaskException {
		String name = player.getName();

		if(maps.containsKey(name)){
			throw new TaskException("The player is currently doing a mission.", task);
		}

		CompletableFuture<TaskState> state = new CompletableFuture<TaskState>();
		TaskPlayerState pstate = new TaskPlayerState(player, task, state);

		this.maps.put(name, pstate);

		ItemStack[] contents = task.getContents(this.items);
		int length = contents.length;
		if(length != 54) length+=9;

		Inventory inventory = Bukkit.createInventory(null, length, "§7Task > §e"+task.getClass().getSimpleName()+" §c("+task.getDuration().getToLanguage()+")");
		inventory.setContents(contents);
		
		ItemStack blueGlass = this.items.getGlass(3);
		int lastline = length-9;
		if(inventory.getItem(lastline) == null){
			for(int i = lastline; i <= length-1; i++){
				inventory.setItem(i, blueGlass);
			}
		}
		
		inventory.setItem(length-5, this.items.getItem(Material.SIGN, 1, 0, 
				MessageList.RULES_LANGUAGE.toString(), 
				Arrays.asList("§e"+task.getRules()+".")));
		
		player.openInventory(inventory);
		task.use(player, inventory, pstate);

		return state;
	}

	public void complete(Player player, Task task) {
		playerTask.get(player.getName()).remove(task);
	}
	
	public Map<String, List<Task>> initPlayerTask(AmongGame game){
		for(Player player : game.getState().getCrewmates()){
			List<Task> taskList = new ArrayList<>();
			for(MessageList messages : new MessageList[]{MessageList.TASK_SHORT_COUNT,
					MessageList.TASK_MEDIUM_COUNT, MessageList.TASK_LONG_COUNT}){
				List<Task> tasks = getAllTasks().stream().filter(d -> d.
						getDuration().equals(TaskDuration.valueOf(messages.name().split("_")[1])))
						.collect(Collectors.toList());
				Collections.shuffle(tasks);
				taskList.addAll(tasks.subList(0, messages.toInt()));	
			}
			this.playerTask.put(player.getName(), taskList);
		}
		return playerTask;
	}
	
	public int getTaskRemaining() {
		return playerTask.values().stream().mapToInt(i -> i.size()).sum();
	}
	
	public class TaskPlayerState{

		private Player player;
		private Task task;
		private CompletableFuture<TaskState> state;
		private Map<String, Object> map;

		public TaskPlayerState(Player player, Task task, CompletableFuture<TaskState> state) {
			this.player = player;
			this.task = task;
			this.state = state;
			this.map = new HashMap<>();
		}

		public Player getPlayer() {
			return player;
		}

		public Task getTask() {
			return task;
		}

		public CompletableFuture<TaskState> getState() {
			return state;
		}

		public Map<String, Object> getMap() {
			return map;
		}

	}

	public void cancelTask(Player player) {
		this.maps.remove(player.getName()).getState().complete(TaskState.CANCELED);
	}

	public Map<String, List<Task>> getPlayerTask() {
		return playerTask;
	}

}
