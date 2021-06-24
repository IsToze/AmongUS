package fr.Toze.amongus.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.Toze.amongus.Main;
import fr.Toze.amongus.interfaces.InventoryListener;
import fr.Toze.amongus.manager.CameraManager;

public class CameraInventory extends InventoryListener{

	private CameraManager manager;
	
	@Override
	public void init(Object[] value) {
		this.manager = ((Main) value[0]).getCameraManager();
	}
	
	@Override
	public String isName() {
		return "§6Camera List :";
	}

	@Override
	public String startWith() {
		return null;
	}

	@Override
	public boolean edit(Inventory inventory, Player player, int slot, ItemStack current, ClickType type) {
		if(current.getType().equals(Material.SKULL_ITEM)){
			this.manager.see(player, this.manager.getCamera(current.getItemMeta().getDisplayName().substring(8)).get());
		}
		return true;
	}

}
