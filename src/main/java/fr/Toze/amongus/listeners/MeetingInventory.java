package fr.Toze.amongus.listeners;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.Toze.amongus.AmongGame;
import fr.Toze.amongus.Main;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.interfaces.InventoryListener;
import fr.Toze.amongus.utils.NBTItemStack;

public class MeetingInventory extends InventoryListener{

	private AmongGame game;
	
	@Override
	public void init(Object[] value) {
		this.game = ((Main) value[0]).getGame();
	}
	
	@Override
	public String isName() {
		return "§4MEETING!";
	}

	@Override
	public String startWith() {
		return null;
	}

	@Override
	public boolean edit(Inventory inventory, Player player, int slot, ItemStack current, ClickType type) {
		//String value = (String) new NBTItemStack(entry.getValue().getItem(0)).get("a", String.class);
		if(current.getType().toString().contains("HELMET")){
			NBTItemStack nbt = new NBTItemStack(inventory.getItem(0));
			Integer oldSlot = (Integer) nbt.get("b", Integer.class);
			
			if(oldSlot != 0){
				if(!MessageList.MEETING_CAN_CHANGE_VOTE.toBoolean()) return true;
				ItemStack old = inventory.getItem(oldSlot);
				ItemMeta meta = old.getItemMeta();
				meta.removeEnchant(Enchantment.DURABILITY);	
				old.setItemMeta(meta);
			}
			
			ItemMeta meta = current.getItemMeta();	
			String[] namea = meta.getDisplayName().split(" ");
			String name = namea[namea.length-1];
			name = name.substring(3, name.length()-1);
			
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			current.setItemMeta(meta);
			
			nbt.put("a", name);
			nbt.put("b", slot);
			inventory.setItem(0, nbt.getItem());
		}
		return true;
	}
	
	@Override
	public boolean close(Inventory inventory, Player player) {
		return game.isMeeting();
	}

}
