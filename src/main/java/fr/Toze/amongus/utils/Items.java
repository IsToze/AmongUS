package fr.Toze.amongus.utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Items {

	public ItemStack getItem(Material material, int count, int data, String name, List<String> lore){
		ItemStack stack = new ItemStack(material, count, (short) data);
		ItemMeta meta = stack.getItemMeta();
		if(name != null) meta.setDisplayName(name);
		if(lore != null) meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}

	public ItemStack getSkull(int count, String name, List<String> lore, String owner){
		ItemStack stack = getItem(Material.SKULL_ITEM, count, 3, name, lore);
		SkullMeta meta = (SkullMeta) stack.getItemMeta();
		meta.setOwner(owner);
		stack.setItemMeta(meta);
		return stack;
	}

	public ItemStack getGlass(int data) {
		return getItem(Material.STAINED_GLASS_PANE, 1, data, "§r", null);
	}
	
	public ItemStack[] getOutline(int line, int data) {
		int size = line*9;
		ItemStack[] contents = new ItemStack[size];
		ItemStack glass = getGlass(data);
		
		for(int i = 0; i < size; i++){
			int j = i % 9;
			if(i <= 8 || i >= size-9 || j == 0 || j == 8) contents[i] = glass;
		}
		
		return contents;
	}
	
}
