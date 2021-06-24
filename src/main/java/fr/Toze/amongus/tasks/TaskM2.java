package fr.Toze.amongus.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.Toze.amongus.interfaces.Task;
import fr.Toze.amongus.manager.TaskManager.TaskPlayerState;
import fr.Toze.amongus.utils.Items;

public class TaskM2 extends Task{

	@Override
	public TaskDuration getDuration() {
		return TaskDuration.MEDIUM;
	}

	@Override
	public ItemStack[] getContents(Items items) {
		ItemStack[] contents = new ItemStack[18];

		List<Integer> slot = new ArrayList<>();
		IntStream.rangeClosed(1, 10).forEach(no -> slot.add(no));
		Collections.shuffle(slot);

		for(int i = 1; i <= 14; i++){
			if(i == 8) i+=4;
			contents[i] = items.getItem(Material.WOOL, 1, 4, "§e"+slot.remove(0), null);
		}
		return contents;
	}

	@Override
	public void use(Player player, Inventory inventory, TaskPlayerState state) {
		state.getMap().put("a", 1);
	}

	@Override
	public void click(Inventory inventory, Player player, int slot, ItemStack current, ClickType type,
			TaskPlayerState state) {
		Map<String, Object> map = state.getMap();
		int i = (int) map.get("a");
		if(current != null && current.getItemMeta().getDisplayName().equalsIgnoreCase("§e"+i)){
			i++;
			if(i < 11){
				map.put("a", i);
				current.setDurability((short) 5);
				ItemMeta meta = current.getItemMeta();
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				meta.addEnchant(Enchantment.DURABILITY, 1, true);
				current.setItemMeta(meta);
			}else{
				state.getState().complete(TaskState.SUCCEEDED);
			}

		}
	}

}
