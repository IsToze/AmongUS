package fr.Toze.amongus.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryListener {

	public void init(Object[] value){}
	public abstract String isName();
	public abstract String startWith();
	public boolean open(Inventory inventory, Player player){return false;}
	public boolean close(Inventory inventory, Player player){return false;}
	public abstract boolean edit(Inventory inventory, Player player, int slot, ItemStack current, ClickType type);
	
}
