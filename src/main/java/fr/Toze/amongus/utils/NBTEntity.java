package fr.Toze.amongus.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class NBTEntity {

	private LivingEntity bukkitEntity;
	
	public NBTEntity(Entity entity) {
		this((LivingEntity) entity);
	}
	
	public NBTEntity(LivingEntity entity) {
		this.bukkitEntity = entity;
	}
	
	public boolean entityExist(){
		return bukkitEntity != null;
	}

	public void put(Equipment equipment, String key, Object value) {
		setItem(equipment, new NBTItemStack(getItem(equipment)).put(key, value));
	}
	
	public Object get(Equipment equipment, String key, Class<?> value) {
		try{
			return new NBTItemStack(getItem(equipment)).get(key, value);
		}catch (Exception e) {
			return null;
		}
	}
	
	public Set<String> keys(Equipment equipment){
		return new NBTItemStack(getItem(equipment)).keys();
	}
	
	public LivingEntity getEntity() {
		return bukkitEntity;
	}
	
	private void setItem(Equipment equipment, ItemStack item) {
		EntityEquipment equip = bukkitEntity.getEquipment();
		try {
			equip.getClass().getMethod(equipment.setMethod(), ItemStack.class).invoke(equip, item);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private ItemStack getItem(Equipment equipment) {
		EntityEquipment equip = bukkitEntity.getEquipment();
		try {
			ItemStack item = (ItemStack) equip.getClass().getMethod(equipment.getMethod()).invoke(equip);
			if(item.getType() == Material.AIR) item.setType(Material.FEATHER);
			return item;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static enum Equipment{
		
		HELMET,
		CHESTPLATE,
		LEGGINGS,
		BOOTS;
		
		public String lower(){
			String name = name();
			return "et"+name.substring(0, 1).toUpperCase()+name.substring(1, name.length()).toLowerCase();
		}

		public String getMethod() {
			return "g"+lower();
		}
		
		public String setMethod(){
			return "s"+lower();
		}
		
	}
	
}
