package fr.Toze.amongus.tasks;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.Toze.amongus.Main;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.interfaces.Task;
import fr.Toze.amongus.manager.TaskManager.TaskPlayerState;
import fr.Toze.amongus.utils.Items;

public class TaskL2 extends Task{

	@Override
	public TaskDuration getDuration() {
		return TaskDuration.LONG;
	}

	@Override
	public ItemStack[] getContents(Items items) {
		return items.getOutline(6, 4);
	}

	@Override
	public void use(Player player, Inventory inventory, TaskPlayerState state) {
		new BukkitRunnable() {
			Random random = new Random();
			int i = -1, size = inventory.getSize();
			ItemStack wool = new ItemStack(Material.WOOL, 1, (short) 4);
			@Override
			public void run() {
				if(i != -1) inventory.setItem(i, null);
				while(inventory.getItem((i = random.nextInt(size))) != null) continue;
				inventory.setItem(i, wool);
			}
			
		}.runTaskTimer(Main.getInstance(), 10, MessageList.TASK_L2_SPEED.toInt());
	}

	@Override
	public void click(Inventory inventory, Player player, int slot, ItemStack current, ClickType type,
			TaskPlayerState state) {
		if(current.getType() == Material.WOOL && current.getDurability() == 4){
			current.setDurability((short) 5);
			inventory.getItem(inventory.all(Material.STAINED_GLASS_PANE).
					entrySet().stream().filter(i -> i.getValue().getDurability() == 4).
					findFirst().get().getKey()).setDurability((short) 5);
			if(inventory.getItem(53).getDurability() == 5){
				state.getState().complete(TaskState.SUCCEEDED);
			}
		}
	}

}
