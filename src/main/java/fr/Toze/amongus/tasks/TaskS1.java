package fr.Toze.amongus.tasks;

import java.util.ArrayList;
import java.util.Arrays;
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

public class TaskS1 extends Task{

	@Override
	public TaskDuration getDuration() {
		return TaskDuration.SHORT;
	}

	@Override
	public ItemStack[] getContents(Items item) {
		ItemStack[] contents = new ItemStack[54];
		int[][] usableSlot = new int[][]{{11, 20, 29, 38}, {13, 22, 31, 40}, {15, 24, 33, 42}};

		List<Integer> wools = new ArrayList<>();
		IntStream.rangeClosed(0, 15).forEach(no -> wools.add(no));
		Collections.shuffle(wools);

		List<Integer> locs = Arrays.asList(0, 1, 2, 3);
		List<List<Integer>> used = new ArrayList<>();
		
		for(int i = 0; i < usableSlot.length; i++){
			List<Integer> slot = new ArrayList<>(locs);
			Collections.shuffle(slot);
			used.add(slot);
		}
		
		for(int i = 0; i < 4; i++){
			ItemStack wool = item.getItem(Material.WOOL, 1, wools.get(i), "§6§l----", null);
			for(int j = 0; j < 3; j++){
				int slot = usableSlot[j][used.get(j).remove(0)];
				contents[slot] = wool;
			}
		}

		return contents;
	}

	@Override
	public void use(Player player, Inventory inventory, TaskPlayerState state) {
		Map<String, Object> map = state.getMap();
		map.put("a", 0); //STREAK
		map.put("b", -1); //DATA
		map.put("c", new ArrayList<Integer>()); //ENCHANTED ITEMS
	}

	@Override
	public void click(Inventory inventory, Player player, int slot, ItemStack current, ClickType type,
			TaskPlayerState state) {
		if(current.getType().equals(Material.WOOL)){
			if(current.getItemMeta().hasEnchant(Enchantment.DURABILITY)) return;
			
			Map<String, Object> map = state.getMap();
			
			int a = (int) map.get("a"), b = (int) map.get("b");
			int dura = current.getDurability();
			
			if(b == -1 || b == dura){
				if(b == -1) map.put("b", dura);
				map.put("a", a+1);
				((ArrayList<Integer>) map.get("c")).add(slot);
				ItemMeta meta = current.getItemMeta();
				meta.addEnchant(Enchantment.DURABILITY, 1, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				current.setItemMeta(meta);
			}else{
				if(a == 3){
					map.put("b", dura);
					((ArrayList<Integer>) map.get("c")).add(slot);
					ItemMeta meta = current.getItemMeta();
					meta.addEnchant(Enchantment.DURABILITY, 1, true);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					current.setItemMeta(meta);
					map.put("a", 1);
				}else{
					ArrayList<Integer> slots = (ArrayList<Integer>) map.get("c");
					slots.forEach(i -> {
						ItemStack stack = inventory.getItem(i);
						ItemMeta meta = stack.getItemMeta();
						meta.removeEnchant(Enchantment.DURABILITY);
						stack.setItemMeta(meta);
					});
					slots.clear();
					map.put("b", -1);
					map.put("a", 0);
				}
			}
			
			if(((ArrayList<Integer>)map.get("c")).size() == 12){
				state.getState().complete(TaskState.SUCCEEDED);
			}
			
		}
	}

}
