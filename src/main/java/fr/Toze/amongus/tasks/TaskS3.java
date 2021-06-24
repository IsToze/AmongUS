package fr.Toze.amongus.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.Toze.amongus.interfaces.Task;
import fr.Toze.amongus.manager.TaskManager.TaskPlayerState;
import fr.Toze.amongus.utils.Items;

public class TaskS3 extends Task{

	@Override
	public TaskDuration getDuration() {
		return TaskDuration.SHORT;
	}
	
	private Items items;

	@Override
	public ItemStack[] getContents(Items items) {
		ItemStack[] contents = new ItemStack[45];
		this.items = items;
		contents[40] = items.getItem(Material.WOOD_BUTTON, 1, 0, "§4§lClick!", null);
		return contents;
	}

	@Override
	public void use(Player player, Inventory inventory, TaskPlayerState state) {}

	@Override
	public void click(Inventory inventory, Player player, int slot, ItemStack current, ClickType type,
			TaskPlayerState state) {
		int empty = inventory.firstEmpty();
		if(empty == 36){
			state.getState().complete(TaskState.SUCCEEDED);
		}else{
			inventory.setItem(empty, this.items.getItem(Material.STAINED_GLASS_PANE, 1, 4 + (empty % 2) , "§e§l---", null));
		}
	}

}
