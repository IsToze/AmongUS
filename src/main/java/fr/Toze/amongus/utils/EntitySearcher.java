package fr.Toze.amongus.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;

import fr.Toze.amongus.utils.NBTEntity.Equipment;


public class EntitySearcher{

	private List<LivingEntity> entity = null;
	
	public EntitySearcher(EntityType type, Equipment equipment, String nbtkey, String nbtvalue) {
		entity = allEntities().stream().filter(e -> {
			if(type != null && !type.equals(e.getType())) return false;
			Object o = new NBTEntity(e).get(equipment, nbtkey, String.class);
			return o != null && o.toString().equalsIgnoreCase(nbtvalue);
		}).collect(Collectors.toList());
	}

	public EntitySearcher(String nbtkey, String nbtvalue) {
		entity = allEntities().stream().filter(e -> {
			if(!e.getType().equals(EntityType.DROPPED_ITEM)) return false;
			Object o = new NBTItemStack(((Item) e).getItemStack()).get(nbtkey, String.class);
			return o != null && o.toString().equalsIgnoreCase(nbtvalue);
		}).collect(Collectors.toList());
	}

	public EntitySearcher(String name) {
		entity = allEntities().stream().filter(e -> {
			if(e instanceof LivingEntity){
				final String entityName = ((LivingEntity) e).getCustomName();
				return entityName != null && name.equals(entityName);
			}
			return false;
		}).collect(Collectors.toList());
	}

	public EntitySearcher(EntityType type) {
		entity = allEntities().stream().filter(e -> e.getType().equals(type)).collect(Collectors.toList());
	}

	public List<LivingEntity> getAll() {
		return entity;
	}
	
	public LivingEntity first(){
		return entity.size() >= 1 ? entity.get(0) : null;
	}
	
	private List<LivingEntity> allEntities() {
		List<LivingEntity> list = new ArrayList<>();
		Bukkit.getWorlds().stream().map(World::getLivingEntities).
				collect(Collectors.toList()).stream()
				.forEach(e -> e.stream().forEach(f -> list.add(f)));
		return list;
	}

}
