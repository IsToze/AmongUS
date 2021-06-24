package fr.Toze.amongus.utils;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class NBTItemStack {

	private ItemStack item;

	public NBTItemStack(ItemStack item) {
		this.item = item;
	}

	public ItemStack getItem() {
		return item;
	}

	public ItemStack put(String key, Object value){
		Object[] array = getNms();
		NBTTagCompound nbt = (NBTTagCompound) array[1];
		switch(value.getClass().getSimpleName()){
		case "String":
			nbt.setString(key, (String) value);
			break;
		case "Integer":
			nbt.setInt(key, (int) value);
			break;
		}
		net.minecraft.server.v1_8_R3.ItemStack stack = (net.minecraft.server.v1_8_R3.ItemStack) array[0];
		stack.setTag(nbt);
		item = CraftItemStack.asBukkitCopy(stack);
		return item;
	}
	
	public Object get(String key, Class<?> clazz){
		NBTTagCompound nms = ((NBTTagCompound) getNms()[1]);
		try {
			String name = clazz.getSimpleName();
			name = name.substring(0, 1).toUpperCase()+name.substring(1, name.length()).toLowerCase();
			name = name.replace("Integer", "Int");
			return nms.getClass().getMethod("get"+name, String.class).invoke(nms, key);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Set<String> keys(){
		return ((NBTTagCompound) getNms()[1]).c();
	}

	private Object[] getNms() {
		net.minecraft.server.v1_8_R3	.ItemStack stack = CraftItemStack.asNMSCopy(item);
		return new Object[]{stack, stack.hasTag() ? stack.getTag() : new NBTTagCompound()};
	}

	public ItemStack remove(String key) {
		return remove(Arrays.asList(key));
	}

	public ItemStack remove(List<String> list) {
		Object[] array = getNms();
		NBTTagCompound nbt = (NBTTagCompound) array[1];
		list.forEach(n -> nbt.remove(n));
		net.minecraft.server.v1_8_R3.ItemStack stack = (net.minecraft.server.v1_8_R3.ItemStack) array[0];
		stack.setTag(nbt);
		item = CraftItemStack.asBukkitCopy(stack);
		return item;
	}

	public List<String> hasString() {
		List<String> list = new ArrayList<>();
		for(String key : keys()){
			list.add(key+" "+get(key, String.class));
		}
		return list;
	}

	public static ItemStack hasString(ItemStack itemStack, List<String> list) {
		if(list == null || list.isEmpty()) return itemStack;
		ItemMeta meta = itemStack.getItemMeta().clone();
		NBTItemStack item = new NBTItemStack(itemStack);
		for(String value : list){
			String[] array = value.split(" ");
			item.put(array[0], value.substring(array[0].length()+1));
		}
		ItemStack returnValue = item.getItem();
		ItemMeta meta2 =returnValue.getItemMeta();
		meta2.setDisplayName(meta.getDisplayName());
		meta2.setLore(meta.getLore());
		
		for(Entry<org.bukkit.enchantments.Enchantment, Integer> ench : meta.getEnchants().entrySet()){
			meta2.addEnchant(ench.getKey(), ench.getValue(), true);
		}
		meta.getItemFlags().forEach(i -> meta2.addItemFlags(i));
		
		returnValue.setItemMeta(meta2);
		return item.getItem();
	}

}
