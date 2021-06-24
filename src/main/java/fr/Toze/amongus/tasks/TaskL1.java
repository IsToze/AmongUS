package fr.Toze.amongus.tasks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

public class TaskL1 extends Task{

	@Override
	public TaskDuration getDuration() {
		return TaskDuration.LONG;
	}

	private Items items;
	
	@Override
	public ItemStack[] getContents(Items item) {
		this.items = item;
		ItemStack[] items = item.getOutline(5, 0);
		ItemStack glass = item.getGlass(0);
		for(int i = 13; i <= 31; i+=9){
			items[i]=glass;
		}
		return items;
	}

	@Override
	public void use(Player player, Inventory inventory, TaskPlayerState state) {
		task(inventory, state);
	}

	@Override
	public void click(Inventory inventory, Player player, int slot, ItemStack current, ClickType type, TaskPlayerState state) {
		if(current.getType().equals(Material.WOOL) && current.getDurability() == 0){
			Map<String, Object> map = state.getMap();
			int count = (int) map.get("b");
			
			if(((List<Integer>) map.get("a")).get(count) == slot-4){
				count++;
				if(count == 5){
					int total = map.containsKey("c") ? (int) map.get("c") : 0;
					total++;
					if(total == 3){
						state.getState().complete(TaskState.SUCCEEDED);
					}else{
						map.put("c", total);
						schulde(inventory, new ItemStack(Material.WOOL, 1, (short) 5), state);
					}
				}else{
					map.put("b", count);
					current.setDurability((short) 1);
				}
				
			}else{
				schulde(inventory, new ItemStack(Material.WOOL, 1, (short) 14), state);
			}
		}
	}

	private void schulde(Inventory inventory, ItemStack item, TaskPlayerState state){
		setToInventory(inventory, item);
		new BukkitRunnable() {
			
			@Override
			public void run() {		
				task(inventory, state);
			}
			
		}.runTaskLater(Main.getInstance(), 40);
	}
	
	private List<Integer> shuffle(){
		List<Integer> var1 = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30);
		Collections.shuffle(var1);
		return var1.subList(0, 5);
	}

	private void setToInventory(Inventory inventory, ItemStack item){
		Arrays.asList(14, 15, 16, 23, 24, 25, 32, 33, 34).forEach(i -> inventory.setItem(i, item));
	}

	private List<Integer> task(Inventory inventory, TaskPlayerState state){
		setToInventory(inventory, null);
		ItemStack wool = this.items.getItem(Material.WOOL, 1, 5, "§6§l---", null);
		List<Integer> slot = shuffle();

		new BukkitRunnable() {
			int count = -1;
			@Override
			public void run() {
				if(count == 4){
					inventory.setItem(slot.get(count), null);
					setToInventory(inventory, new ItemStack(Material.WOOL));
					cancel();
					return;
				}
				if(count != -1){
					inventory.setItem(slot.get(count), null);
				}
				count++;
				inventory.setItem(slot.get(count), wool);
			}
		}.runTaskTimer(Main.getInstance(), 10, MessageList.TASK_L1_SPEED.toInt());
		
		Map<String, Object> map = state.getMap();
		map.put("a", slot);
		map.put("b", 0);
		
		return slot;
	}

}
