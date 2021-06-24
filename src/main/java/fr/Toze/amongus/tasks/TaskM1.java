package fr.Toze.amongus.tasks;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.Toze.amongus.Main;
import fr.Toze.amongus.interfaces.Task;
import fr.Toze.amongus.manager.TaskManager.TaskPlayerState;
import fr.Toze.amongus.utils.Items;

public class TaskM1 extends Task{

	@Override
	public TaskDuration getDuration() {
		return TaskDuration.MEDIUM;
	}

	@Override
	public ItemStack[] getContents(Items items) {
		ItemStack[] contents = new ItemStack[36];
		ItemStack stack = items.getItem(Material.WOOL, 1, 4, "§6§lDownloading...", null);
		for(int i = 10; i <= 26; i++){
			if(i == 18 || i % 9 == 8) continue;
			contents[i] = stack;
		}
		return contents;
	}

	@Override
	public void use(Player player, Inventory inventory, TaskPlayerState state) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Optional<? extends ItemStack> wool = inventory.all(Material.WOOL).values().stream().filter(w -> w.getDurability() == 4).findFirst();
				if(wool.isPresent()){
					wool.get().setDurability((short) 5);
				}else{
					state.getState().complete(TaskState.SUCCEEDED);
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 13, 12);
	}

	@Override
	public void click(Inventory inventory, Player player, int slot, ItemStack current, ClickType type,
			TaskPlayerState state) {}

}
