package fr.Toze.amongus.event;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import fr.Toze.amongus.Main;
import fr.Toze.amongus.interfaces.InventoryListener;
import fr.Toze.amongus.listeners.CameraInventory;
import fr.Toze.amongus.listeners.MeetingInventory;

public class InteractListener implements Listener{

	private List<InventoryListener> inventory;
	
	public InteractListener(Main main) {
		this.inventory = Arrays.asList(new CameraInventory(), new MeetingInventory());
		Object[] value = new Object[]{main};
		inventory.forEach(e -> e.init(value));
	}

	@EventHandler
	public void onOpen(InventoryOpenEvent e){
		Inventory inventory = e.getInventory();
		Optional<InventoryListener> optiInv = optiInv(inventory);
		if(optiInv.isPresent()) e.setCancelled(optiInv.get().open(inventory, (Player) e.getPlayer()));
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e){
		Inventory inventory = e.getInventory();
		Optional<InventoryListener> optiInv = optiInv(inventory);
		if(optiInv.isPresent()){
			Player player = (Player) e.getPlayer();
			if(optiInv.get().close(inventory, player)){
				new BukkitRunnable() {
					
					@Override
					public void run() {
						player.openInventory(inventory);
					}
					
				}.runTaskLater(Main.getInstance(), 2);
			}
		}
	}

	@EventHandler
	public void onOpen(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		if(e.getCurrentItem() == null) return;
		Optional<InventoryListener> optiInv = optiInv(e.getInventory());
		if(optiInv.isPresent()) e.setCancelled(optiInv.get()
				.edit(inv, (Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getClick()));
	}

	private Optional<InventoryListener> optiInv(Inventory inventory) {
		return this.inventory.stream()
				.filter(l -> (l.isName() != null && l.isName().equalsIgnoreCase(inventory.getName())  
						|| (l.startWith() != null && inventory.getName().startsWith(l.startWith()))))
				.findFirst();
	}

}
