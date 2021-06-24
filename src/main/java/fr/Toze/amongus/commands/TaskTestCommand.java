package fr.Toze.amongus.commands;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.Toze.amongus.Main;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.interfaces.CompletedCommand;
import fr.Toze.amongus.interfaces.CustomArgs;
import fr.Toze.amongus.interfaces.Task;
import fr.Toze.amongus.manager.TaskManager;
import fr.Toze.amongus.utils.NBTEntity;
import fr.Toze.amongus.utils.NBTEntity.Equipment;
import fr.Toze.amongus.utils.VisualEntity;

public class TaskTestCommand extends CompletedCommand{

	private TaskManager manager;

	@Override
	public void init(Object[] array) {
		this.manager = ((Main) array[2]).getTasks();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length == 0){
			sender.sendMessage(MessageList.MESSAGE_COMMAND_TASKTEST.toString());
			return false;
		}

		String id = args[0];
		Optional<Task> optinalTask = this.manager.getTask(id);

		if(!optinalTask.isPresent()){
			sender.sendMessage(MessageList.MESSAGE_COMMAND_TASKTEST_NO_EXIST.toString().replace("%id%", id));
			return false;
		}
		Player player = (Player) sender;
		
		
		Task task = optinalTask.get();
		VisualEntity entity = new VisualEntity(player, player.getLocation().subtract(0, 0.6, 0), EntityType.ARMOR_STAND);
		ArmorStand armor = (ArmorStand) entity.getEntity();
		armor.setCustomNameVisible(true);
		armor.setCustomName("§e"+task.getName());
		armor.setVisible(false);
		armor.setGravity(false);
		armor.setSmall(true);
		armor.setHelmet(new ItemStack(Material.GOLD_BLOCK));
		//new EntitySearcher(EntityType.ARMOR_STAND, Equipment.BOOTS, "task-entity", "true").getAll().forEach(e -> e.remove());
		new NBTEntity(entity.getEntity()).put(Equipment.BOOTS, "task-entity", "true");
		
		
		/*
		try {
			CompletableFuture<TaskState> state = manager.execute(player, task.get());
			state.thenAccept(states -> {
				Bukkit.broadcastMessage("State > "+states.toString());
				player.closeInventory();
			});
		} catch (AmongTaskException e) {
			e.printStackTrace();
		}
		 */
		return true;
	}

	@Override
	public String getName() {
		return "tasktest";
	}

	@Override
	public String getPermission() {
		return MessageList.PERMISSION_TASKTEST_COMMAND.toString();
	}

	@Override
	public boolean isOnlyUser() {
		return true;
	}

	@Override
	public List<String> getTabCompleter(CommandSender sender, Command command, String message, String[] args) {
		return manager.getAllTasks().stream().map(task -> task.getClass().getSimpleName()).collect(Collectors.toList());
	}

	@Override
	public CustomArgs[] getSubs() {
		return null;
	}

}
