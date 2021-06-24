package fr.Toze.amongus.objects;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.Toze.amongus.Main;
import fr.Toze.amongus.interfaces.InventoryListener;
import fr.Toze.amongus.manager.TaskManager;
import fr.Toze.amongus.manager.TaskManager.TaskPlayerState;

public class TaskGlobal extends InventoryListener{

	private TaskManager manager;
	
	@Override
	public void init(Object[] value) {
		this.manager = ((Main) value[0]).getTasks();
	}
	
	@Override
	public String isName() {
		return null;
	}

	@Override
	public String startWith() {
		return "§7Task > §e";
	}

	@Override
	public boolean close(Inventory inventory, Player player) {
		if(manager.getTask(player) != null) this.manager.cancelTask(player);
		return false;
	}
	
	@Override
	public boolean edit(Inventory inventory, Player player, int slot, ItemStack current, ClickType type) {
		TaskPlayerState task = manager.getTask(player);
		if(task != null) task.getTask().click(inventory, player, slot, current, type, task);
		return false;
	}

}
